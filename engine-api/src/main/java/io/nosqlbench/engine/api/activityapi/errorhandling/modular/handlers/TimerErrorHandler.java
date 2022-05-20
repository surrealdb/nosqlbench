package io.nosqlbench.engine.api.activityapi.errorhandling.modular.handlers;

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


import io.nosqlbench.engine.api.activityapi.errorhandling.ErrorMetrics;
import io.nosqlbench.engine.api.activityapi.errorhandling.modular.ErrorDetail;
import io.nosqlbench.engine.api.activityapi.errorhandling.modular.ErrorHandler;
import io.nosqlbench.engine.api.metrics.ExceptionTimerMetrics;
import io.nosqlbench.nb.annotations.Service;

import java.util.function.Supplier;

@Service(value = ErrorHandler.class, selector = "timer")
public class TimerErrorHandler implements ErrorHandler, ErrorMetrics.Aware {


    private ExceptionTimerMetrics exceptionTimerMetrics;

    @Override
    public ErrorDetail handleError(String name, Throwable t, long cycle, long durationInNanos, ErrorDetail detail) {
        exceptionTimerMetrics.update(name, durationInNanos);
        return detail;
    }

    @Override
    public void setErrorMetricsSupplier(Supplier<ErrorMetrics> supplier) {
        this.exceptionTimerMetrics = supplier.get().getExceptionTimerMetrics();
    }
}
