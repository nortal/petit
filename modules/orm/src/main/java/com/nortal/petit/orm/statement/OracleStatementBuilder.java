/**
 *   Copyright 2014 Nortal AS
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.nortal.petit.orm.statement;

import com.nortal.petit.orm.statement.clause.Limit;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @created 18.03.2013
 * 
 */
public class OracleStatementBuilder extends StatementBuilder {
    public String limitSql(Limit limit) {
        StringBuffer expression = new StringBuffer("SELECT t.* FROM (");
        expression.append(getLoad());
        expression.append(") t WHERE rownum >= ").append(limit.getFirstElementPosition()).append(" AND rownum <= ")
                .append(limit.getLastElementPosition());
        return expression.toString();
    }
}
