package io.nosqlbench.activitytype.cqlverify;

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


import io.nosqlbench.activitytype.cql.core.CqlActivity;
import io.nosqlbench.activitytype.cql.core.CqlAsyncAction;
import io.nosqlbench.engine.api.activityapi.core.ActivityDefObserver;
import io.nosqlbench.engine.api.activityimpl.ActivityDef;

public class CqlVerifyAsyncAction extends CqlAsyncAction implements ActivityDefObserver {

    public CqlVerifyAsyncAction(ActivityDef activityDef, int slot, CqlActivity cqlActivity) {
        super(cqlActivity, slot);
    }

    @Override
    public void onActivityDefUpdate(ActivityDef activityDef) {
        super.onActivityDefUpdate(activityDef);
    }


}