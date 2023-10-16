/*
 * Copyright (c) 2023 nosqlbench
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
package io.nosqlbench.adapter.pgvector.optypes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PGVectorExecuteOp extends PGVectorOp {
    private static final Logger LOGGER = LogManager.getLogger(PGVectorExecuteOp.class);
    private static final String LOG_UPDATE_COUNT_ERROR = "Exception occurred while attempting to fetch the update count of the query operation";
    private static final String LOG_UPDATE_COUNT = "Executed a normal DDL/DML (non-SELECT) operation. DML query updated [%d] records";

    public PGVectorExecuteOp(Connection connection, Statement statement, String queryString) {
        super(connection, statement, queryString);
    }

    @Override
    public Object apply(long value) {
        List<ResultSet> queryResults = new ArrayList<>();
        try {
            boolean isResultSet = statement.execute(queryString);

            ResultSet rs;
            if (isResultSet) {
                int countResults = 0;
                rs = statement.getResultSet();
                Objects.requireNonNull(rs);
                countResults += rs.getRow();
                queryResults.add(rs);
                while (null != rs) {
                    while (statement.getMoreResults() && -1 > statement.getUpdateCount()) {
                        countResults += rs.getRow();
                    }
                    rs = statement.getResultSet();
                    queryResults.add(rs);
                }

                finalResultCount = countResults;
                LOGGER.debug(() -> LOG_ROWS_PROCESSED);
            }
            connection.commit();
            LOGGER.debug(() -> LOG_COMMIT_SUCCESS);
            return queryResults;
        } catch (SQLException sqlException) {
            String exMsg = String.format("ERROR: [ state => %s, cause => %s, message => %s ]",
                sqlException.getSQLState(), sqlException.getCause(), sqlException.getMessage());
            LOGGER.error(exMsg, sqlException);
            throw new RuntimeException(exMsg, sqlException);
        } catch (Exception ex) {
            LOGGER.error(LOG_GENERIC_ERROR, ex);
            throw new RuntimeException(LOG_GENERIC_ERROR, ex);
        }
    }
}