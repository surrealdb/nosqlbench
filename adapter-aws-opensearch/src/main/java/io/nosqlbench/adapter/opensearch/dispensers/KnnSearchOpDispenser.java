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

package io.nosqlbench.adapter.opensearch.dispensers;

import io.nosqlbench.adapter.opensearch.OpenSearchAdapter;
import io.nosqlbench.adapter.opensearch.ops.KnnSearchOp;
import io.nosqlbench.adapter.opensearch.pojos.Doc;
import io.nosqlbench.adapters.api.templating.ParsedOp;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.KnnQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;

import java.util.List;
import java.util.function.LongFunction;

public class KnnSearchOpDispenser extends BaseOpenSearchOpDispenser {
    private Class<?> schemaClass;

    public KnnSearchOpDispenser(OpenSearchAdapter adapter, ParsedOp op, LongFunction<String> targetF) {
        super(adapter, op, targetF);
        String schemaClassStr = op.getStaticConfigOr("schema", "io.nosqlbench.adapter.opensearch.pojos.Doc");
        try {
            schemaClass = Class.forName(schemaClassStr);
        } catch (Exception e) {
            schemaClass = Doc.class;
        }
    }

    @Override
    public LongFunction<KnnSearchOp> createOpFunc(LongFunction<OpenSearchClient> clientF, ParsedOp op,LongFunction<String> targetF) {
        LongFunction<KnnQuery.Builder> knnfunc = l -> new KnnQuery.Builder();
        knnfunc = op.enhanceFuncOptionally(knnfunc, "k",Integer.class, KnnQuery.Builder::k);
        knnfunc = op.enhanceFuncOptionally(knnfunc, "vector", List.class, this::convertVector);
        knnfunc = op.enhanceFuncOptionally(knnfunc, "field",String.class, KnnQuery.Builder::field);

        //TODO: Implement the filter query builder here
        //knnfunc = op.enhanceFuncOptionally(knnfunc, "filter",Query.class, KnnQuery.Builder::filter);

        LongFunction<KnnQuery.Builder> finalKnnfunc = knnfunc;
        LongFunction<SearchRequest.Builder> bfunc =
            l -> new SearchRequest.Builder().size(op.getStaticValueOr("size", 100))
                .index(targetF.apply(l))
                .query(new Query.Builder().knn(finalKnnfunc.apply(l).build()).build());

        return (long l) -> new KnnSearchOp(clientF.apply(l), bfunc.apply(l).build(), schemaClass);
    }

    private KnnQuery.Builder convertVector(KnnQuery.Builder builder, List list) {
        float[] vector = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            vector[i] = (float) list.get(i);
        }
        return builder.vector(vector);
    }

}
