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

package io.nosqlbench.virtdata.library.curves4.discrete.long_int;

import io.nosqlbench.virtdata.api.annotations.Categories;
import io.nosqlbench.virtdata.api.annotations.Category;
import io.nosqlbench.virtdata.api.annotations.ThreadSafeMapper;
import io.nosqlbench.virtdata.library.curves4.discrete.int_int.IntToIntDiscreteCurve;
import io.nosqlbench.virtdata.library.curves4.discrete.int_long.IntToLongDiscreteCurve;
import org.apache.commons.math4.legacy.distribution.EnumeratedIntegerDistribution;

import static io.nosqlbench.virtdata.library.curves4.discrete.int_int.EnumeratedInts.parseDoubleWeights;
import static io.nosqlbench.virtdata.library.curves4.discrete.int_int.EnumeratedInts.parseIntLabels;

@ThreadSafeMapper
@Categories({Category.distributions})
public class EnumeratedInts extends LongToIntDiscreteCurve {
    public EnumeratedInts(String data, String... mods) {
        super(new EnumeratedIntegerDistribution(parseIntLabels(data), parseDoubleWeights(data)), mods);
    }
}
