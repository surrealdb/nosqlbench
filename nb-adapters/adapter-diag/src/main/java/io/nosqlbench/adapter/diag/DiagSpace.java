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

package io.nosqlbench.adapter.diag;

import io.nosqlbench.adapters.api.activityimpl.uniform.BaseSpace;
import io.nosqlbench.engine.api.activityapi.core.ActivityDefObserver;
import io.nosqlbench.engine.api.activityapi.simrate.RateLimiter;
import io.nosqlbench.nb.api.engine.activityimpl.ActivityDef;
import io.nosqlbench.nb.api.config.standard.ConfigModel;
import io.nosqlbench.nb.api.config.standard.NBConfigModel;
import io.nosqlbench.nb.api.config.standard.NBConfiguration;
import io.nosqlbench.nb.api.config.standard.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiagSpace extends BaseSpace<DiagSpace> implements ActivityDefObserver {
    private final Logger logger = LogManager.getLogger(DiagSpace.class);

    private final NBConfiguration cfg;
    private RateLimiter diagRateLimiter;
    private long interval;
    private boolean errorOnClose;

    public DiagSpace(DiagDriverAdapter adapter, long idx, NBConfiguration cfg) {
        super(adapter, idx);
        this.cfg = cfg;
        applyConfig(cfg);
        logger.trace(() -> "diag space initialized as '" + idx + "'");
    }

    public void applyConfig(NBConfiguration cfg) {
        this.interval = cfg.get("interval",long.class);
        this.errorOnClose = cfg.get("erroronclose",boolean.class);
    }

    public static NBConfigModel getConfigModel() {
        return ConfigModel.of(DiagSpace.class)
            .add(Param.defaultTo("interval",1000))
            .add(Param.defaultTo("erroronclose", false))
            .asReadOnly();
    }

    public void maybeWaitForOp(double diagrate) {
        if (diagRateLimiter != null) {
            long waittime = diagRateLimiter.block();
        }
    }

    @Override
    public void onActivityDefUpdate(ActivityDef activityDef) {
        NBConfiguration cfg = getConfigModel().apply(activityDef.getParams().getStringStringMap());
        this.applyConfig(cfg);
    }

    @Override
    public void close() throws Exception {
        logger.debug(() -> "closing diag space '" + getName() + "'");
        if (errorOnClose) {
            throw new RuntimeException("diag space was configured to throw this error when it was configured.");
        }
    }
}
