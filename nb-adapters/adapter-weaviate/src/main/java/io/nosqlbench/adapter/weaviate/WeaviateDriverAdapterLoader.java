/*
 * Copyright (c) 2020-2024 nosqlbench
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

package io.nosqlbench.adapter.weaviate;

import static io.nosqlbench.adapter.weaviate.WeaviateAdapterUtils.WEAVIATE;

import io.nosqlbench.adapter.diag.DriverAdapterLoader;
import io.nosqlbench.nb.annotations.Service;
import io.nosqlbench.nb.api.components.core.NBComponent;
import io.nosqlbench.nb.api.labels.NBLabels;

@Service(value = DriverAdapterLoader.class, selector = WEAVIATE)
public class WeaviateDriverAdapterLoader implements DriverAdapterLoader {
	@Override
	public WeaviateDriverAdapter load(NBComponent parent, NBLabels childLabels) {
		return new WeaviateDriverAdapter(parent, childLabels);
	}
}
