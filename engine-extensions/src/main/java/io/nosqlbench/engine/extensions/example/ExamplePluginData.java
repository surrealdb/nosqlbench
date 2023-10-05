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

package io.nosqlbench.engine.extensions.example;

import io.nosqlbench.api.extensions.ScriptingExtensionPluginInfo;
import io.nosqlbench.components.NBComponent;
import io.nosqlbench.nb.annotations.Service;
import org.apache.logging.log4j.Logger;

@Service(value = ScriptingExtensionPluginInfo.class, selector = "adder")
public class ExamplePluginData implements ScriptingExtensionPluginInfo<ExamplePlugin> {

    @Override
    public String getDescription() {
        return "This is an example of a dynamically loadable script extension. It just adds two ints when" +
                "you call the getSum(...) method.";
    }

    @Override
    public ExamplePlugin getExtensionObject(final Logger logger, final NBComponent baseComponent) {
        return new ExamplePlugin(baseComponent);
    }

}
