/*
 * Copyright (c) 2024 nosqlbench
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nosqlbench.engine.cli.atfiles;

import io.nosqlbench.nb.api.nbio.Content;
import io.nosqlbench.nb.api.nbio.NBIO;
import io.nosqlbench.nb.api.nbio.NBPathsAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NBAtFile {
    private final static Logger logger = LogManager.getLogger(NBAtFile.class);

    /**
     * This will take a command line in raw form, which may include some arguments
     * in the <pre>{@code @filepath:datapath>format}</pre> format. For each of these,
     * the contents are expanded from the specified file, interior data path, and in
     * the format requested.
     * <UL>
     *     <LI>{@code >= } formats maps as key=value</LI>
     *     <LI>{@code >: } formats maps as key:value</LI>
     *     <LI>{@code >-- } asserts each value starts with global option syntax (--)</LI>
     * </UL>
     *
     * @param processInPlace The linked list which is statefully modified. If you need
     *                       an unmodified copy, then this is the responsibility of the caller.
     * @return An updated list with all values expanded and injected
     * @throws RuntimeException for any errors finding, traversing, parsing, or rendering values
     */
    public static LinkedList<String> includeAt(LinkedList<String> processInPlace) {
        ListIterator<String> iter = processInPlace.listIterator();
        while (iter.hasNext()) {
            String spec = iter.next();
            if (spec.startsWith("@")) {
                iter.previous();
                iter.remove();
                LinkedList<String> spliceIn = includeAt(spec);
                for (String s : spliceIn) {
                    iter.add(s);
                }
            }
        }
        return processInPlace;
    }
    private final static Pattern includePattern =
        Pattern.compile("@(?<filepath>[a-zA-Z_][a-zA-Z_./]*)(:(?<datapath>[a-zA-Z_][a-zA-Z0-9_./]*))?(>(?<formatter>.+))?");

    /**
     * Format specifiers:
     * <pre>{@code
     * -- means each value must come from a list, and that each line should contain a global argument
     *    Specifically, each line must start with a --
     *
     * =  means each entry should be a key-value pair, and that it will be formatted as key=value on insertion
     *
     * :  means each entry should be a key-value pair, and that it will be formatted as key:value on insertion*
     * }</pre>
     * @param spec The include-at specifier, in the form of @file[:datapath]
     * @return The linked list of arguments which is to be spliced into the caller's command list
     */
    public static LinkedList<String> includeAt(String spec) {
        Matcher matcher = includePattern.matcher(spec);
        if (matcher.matches()) {
            String filepathSpec = matcher.group("filepath");
            String dataPathSpec = matcher.group("datapath");
            String formatSpec = matcher.group("formatter");
            String[] datapath = (dataPathSpec!=null && !dataPathSpec.isBlank()) ? dataPathSpec.split("(/|\\.)") : new String[] {};

            String[] parts = filepathSpec.split("\\.",2);
            if (parts.length==2 && !parts[1].toLowerCase().matches("yaml")) {
                throw new RuntimeException("Only the yaml format and extension is supported for at-files." +
                    " You specified " + parts[1]);
            }

            NBPathsAPI.GetExtensions wantsExtension = NBIO.local().pathname(filepathSpec);
            String extension = (!filepathSpec.toLowerCase().endsWith(".yaml")) ? "yaml" : "";
            if (!extension.isEmpty()) {
                logger.debug("adding extension 'yaml' to at-file path '" + filepathSpec + "'");
                wantsExtension.extensionSet("yaml");
            }
            Content<?> argsContent = wantsExtension.one();
            String argsdata = argsContent.asString();
            NBAtFileFormats fmt = (formatSpec!=null) ? NBAtFileFormats.valueOfSymbol(formatSpec) : NBAtFileFormats.Default;

            Object scopeOfInclude = null;
            try {
                Load yaml = new Load(LoadSettings.builder().build());
                scopeOfInclude= yaml.loadFromString(argsdata);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (datapath.length>0) {
                if (scopeOfInclude instanceof Map<?,?> mapdata) {
                    scopeOfInclude = traverseData(filepathSpec,(Map<String,Object>) mapdata,datapath);
                } else {
                    throw new RuntimeException("You can not traverse a non-map object type with spec '" + spec + "'");
                }
            }
            return formatted(scopeOfInclude, fmt);
        } else {
            throw new RuntimeException("Unable to match at-file specifier: " + spec + " to known syntax");
        }

    }

    private static LinkedList<String> formatted(Object scopeOfInclude, NBAtFileFormats fmt) {
        LinkedList<String> emitted = new LinkedList<>();
        if (scopeOfInclude instanceof Map<?,?> map) {
            Map<String,String> included = new LinkedHashMap<>();
            map.forEach((k,v) -> {
                included.put(k.toString(),v.toString());
            });
            included.forEach((k,v) -> {
                fmt.validate(new String[]{k,v});
                String formatted = fmt.format(new String[]{k,v});
                emitted.add(formatted);
            });
        } else if (scopeOfInclude instanceof List<?> list) {
            List<String> included = new LinkedList<>();
            list.forEach(item -> included.add(item.toString()));
            included.forEach(item -> {
                fmt.validate(new String[]{item});
                String formatted = fmt.format(new String[]{item});
                emitted.add(formatted);
            });
        } else {
            throw new RuntimeException(scopeOfInclude.getClass().getCanonicalName() + " is not a valid data structure at-file inclusion.");
        }
        return emitted;
    }

    private static Object traverseData(String sourceName, Map<String, Object> map, String[] datapath) {
        String leaf = datapath[datapath.length-1];
        String[] traverse = Arrays.copyOfRange(datapath,0,datapath.length-1);

        for (String name : traverse) {
            if (map.containsKey(name)) {
                Object nextMap = map.get(name);
                if (nextMap instanceof Map<?, ?> nextmap) {
                    map = (Map<String, Object>) nextmap;
                }
            } else {
                throw new RuntimeException(
                    "Unable to traverse to '" + name + "' node " +
                        " in path '" + String.join("/",Arrays.asList(datapath) +
                        " in included data from source '" + sourceName + "'")
                );
            }
        }

        if (map.containsKey(leaf)) {
            return (map.get(leaf));
        } else {
            throw new RuntimeException("Unable to find data path '" + String.join("/",Arrays.asList(datapath)) + " in included data from source '" + sourceName + "'");
        }

    }
}