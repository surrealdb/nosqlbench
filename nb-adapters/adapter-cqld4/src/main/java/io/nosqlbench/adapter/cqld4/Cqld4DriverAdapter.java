/*
 * Copyright (c) 2022-2023 nosqlbench
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

package io.nosqlbench.adapter.cqld4;

import io.nosqlbench.adapter.cqld4.opmappers.Cqld4CoreOpMapper;
import io.nosqlbench.adapter.cqld4.optypes.Cqld4BaseOp;
import io.nosqlbench.adapters.api.activityimpl.uniform.Validator;
import io.nosqlbench.adapters.api.templating.ParsedOp;
import io.nosqlbench.nb.api.config.standard.NBConfigModel;
import io.nosqlbench.nb.api.config.standard.NBConfiguration;
import io.nosqlbench.adapters.api.activityimpl.OpMapper;
import io.nosqlbench.adapters.api.activityimpl.uniform.BaseDriverAdapter;
import io.nosqlbench.adapters.api.activityimpl.uniform.DriverAdapter;
import io.nosqlbench.nb.api.labels.NBLabels;
import io.nosqlbench.nb.api.components.core.NBComponent;
import io.nosqlbench.nb.annotations.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.LongFunction;

@Service(value = DriverAdapter.class, selector = "cqld4")
public class Cqld4DriverAdapter extends BaseDriverAdapter<Cqld4BaseOp<?>, Cqld4Space> {
    private final static Logger logger = LogManager.getLogger(Cqld4DriverAdapter.class);

    public Cqld4DriverAdapter(NBComponent parentComponent, NBLabels labels) {
        super(parentComponent, labels);
    }

    @Override
    public OpMapper<Cqld4BaseOp<?>, Cqld4Space> getOpMapper() {
        NBConfiguration config = getConfiguration();
        return new Cqld4CoreOpMapper(this, config);
    }

    @Override
    public LongFunction<Cqld4Space> getSpaceInitializer(NBConfiguration cfg) {
        return s -> new Cqld4Space(this,s,cfg);
    }

    @Override
    public NBConfigModel getConfigModel() {
        return super.getConfigModel().add(Cqld4Space.getConfigModel());
    }


    @Override
    public List<Function<Map<String, Object>, Map<String, Object>>> getOpFieldRemappers() {
        List<Function<Map<String, Object>, Map<String, Object>>> remappers = new ArrayList<>();
        remappers.addAll(super.getOpFieldRemappers());

        // Simplify to the modern form and provide a helpful warning to the user
        // This auto updates to 'simple: <stmt>' or 'prepared: <stmt>' for cql types
        remappers.add(m -> {
            Map<String,Object> map = new LinkedHashMap<>(m);

            if (map.containsKey("stmt")) {
                String type = map.containsKey("type") ? map.get("type").toString() : "cql";
                if (type.equals("cql")){
                    boolean prepared = (!map.containsKey("prepared")) || map.get("prepared").equals(true);
                    map.put(prepared?"prepared":"simple",map.get("stmt"));
                    map.remove("stmt");
                    map.remove("type");
                }
            }
            if (map.containsKey("type")) {
                String type = map.get("type").toString();
                if (type.equals("gremlin")&&map.containsKey("script")) {
                    map.put("gremlin",map.get("script").toString());
                    map.remove("script");
                    map.remove("type");
                }
                if (type.equals("gremlin")&&map.containsKey("stmt")) {
                    map.put("gremlin",map.get("stmt"));
                    map.remove("type");
                    map.remove("stmt");
                }
                if (type.equals("fluent")&&map.containsKey("fluent")) {
                    map.remove("type");
                }
                if (type.equals("fluent")&&map.containsKey("stmt")) {
                    map.put("fluent",map.get("stmt"));
                    map.remove("stmt");
                }
            }

            return map;
        });

        return remappers;
    }

}
