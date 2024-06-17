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

package io.nosqlbench.adapter.dataapi.ops;

import com.datastax.astra.client.Collection;
import com.datastax.astra.client.Database;
import com.datastax.astra.client.admin.AstraDBAdmin;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;

public class DataApiCreateDatabaseOp extends DataApiAdminOp {
    private final String name;
    private final CloudProviderType cloud;
    private final String cloudRegion;
    private final boolean waitForDb;

    public DataApiCreateDatabaseOp(Database db, AstraDBAdmin admin, String name, CloudProviderType cloud, String cloudRegion, boolean waitForDb) {
        super(db, admin);
        this.name = name;
        this.cloud = cloud;
        this.cloudRegion = cloudRegion;
        this.waitForDb = waitForDb;
    }

    @Override
    public Object apply(long value) {
        return admin.createDatabase(name, cloud, cloudRegion, waitForDb);
    }
}