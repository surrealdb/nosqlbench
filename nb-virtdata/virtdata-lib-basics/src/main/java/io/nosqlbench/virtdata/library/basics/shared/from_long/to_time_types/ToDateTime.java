/*
 * Copyright (c) nosqlbench
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

package io.nosqlbench.virtdata.library.basics.shared.from_long.to_time_types;

import io.nosqlbench.virtdata.api.annotations.Categories;
import io.nosqlbench.virtdata.api.annotations.Category;
import io.nosqlbench.virtdata.api.annotations.DeprecatedFunction;
import io.nosqlbench.virtdata.api.annotations.ThreadSafeMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.function.LongFunction;

/**
 * Convert the input value to a {@code org.joda.time.DateTime}
 */
@ThreadSafeMapper
@DeprecatedFunction("This will be replaced by the more aptly named toJodaDateTime(...)")
@Categories({Category.datetime})
public class ToDateTime implements LongFunction<DateTime> {

    private final long spacing;
    private final long repeat_count;

    public ToDateTime(int spacing, int repeat_count){
        this.spacing = spacing;
        this.repeat_count = repeat_count;
    }

    public ToDateTime(String spacing){
        this(Integer.valueOf(spacing), 1);
    }

    public ToDateTime(){
        this.spacing=1;
        this.repeat_count=1;
    }

    @Override
    public DateTime apply(long input) {
        input = (input*spacing)/repeat_count;
        return new DateTime(input, DateTimeZone.UTC);
    }

    public String toString() {
        return getClass().getSimpleName() + ":" + spacing+ ":" + repeat_count;
    }
}
