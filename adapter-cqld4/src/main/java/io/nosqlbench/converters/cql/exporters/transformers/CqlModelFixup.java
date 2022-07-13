/*
 * Copyright (c) 2022 nosqlbench
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

package io.nosqlbench.converters.cql.exporters.transformers;

import io.nosqlbench.converters.cql.cqlast.CqlColumnDef;
import io.nosqlbench.converters.cql.cqlast.CqlModel;
import io.nosqlbench.converters.cql.cqlast.CqlTable;

import java.util.List;
import java.util.function.Function;

public class CqlModelFixup implements Function<CqlModel,CqlModel> {

    @Override
    public CqlModel apply(CqlModel model) {
        List<String> toReplace = model.getTypes().stream().map(t -> t.getKeyspace() + "." + t.getName()).toList();
        for (CqlTable table : model.getAllTables()) {
            String table_ddl = table.getRefddl();
            for (CqlColumnDef coldef : table.getColumnDefinitions()) {
                String coldefDdl = coldef.getRefddl();
                for (String searchFor : toReplace) {
                    if (coldefDdl.contains(searchFor)) {
                        String typedef = coldef.getType();
                        coldef.setType("blob");
                        coldef.setRefddl(coldef.getRefddl().replaceAll(typedef,"blob"));
                        table_ddl= table_ddl.replaceAll(typedef,"blob");
                    }
                }
            }
            table.setRefDdl(table_ddl);
        }

        return model;
    }


}