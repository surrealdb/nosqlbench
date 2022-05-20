package io.nosqlbench.activitytype.cql.api;

/*
 * Copyright (c) 2022 nosqlbench
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


/**
 * When an error filter allows us to see and handle an error in a specific way,
 * the ErrorResponse determines exactly how we handle it. Each level represents
 * a starting point in handling, including everything after the starting point.
 * The first enum is the most severe response
 */
public enum ErrorResponse {

    stop("S"),      // Rethrow this error to the runtime, forcing it to handle the error or stop
    warn("W"),      // log a warning with some details about this error
    retry("R"),     // resubmit this operation up to the available tries
    histogram("H"),     // record this metric in a histogram
    count("C"),     // count this metric separately
    counter("C"),
    ignore("I");    // do nothing

    private final String symbol;

    ErrorResponse(String symbol) {
        this.symbol = symbol;
    }
}
