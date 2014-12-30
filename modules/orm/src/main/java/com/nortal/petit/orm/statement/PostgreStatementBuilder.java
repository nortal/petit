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

import java.util.ArrayList;
import java.util.List;

import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.Property;
import com.nortal.petit.orm.statement.clause.Limit;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @created 18.03.2013
 * 
 */
public class PostgreStatementBuilder extends StatementBuilder {
    @Override
    public String limitSql(Limit limit) {
        StringBuffer expression = new StringBuffer("SELECT t.* FROM (");
        expression.append(getLoad());
        expression.append(") t LIMIT ").append(limit.getCount()).append(" OFFSET ").append(limit.getStart());
        return expression.toString();
    }

    public <B> List<String> getWritableProps(BeanMapping<B> mapping, StatementType stmtType) {
        List<String> res = new ArrayList<String>();
        for (Property<B, Object> p : mapping.props().values()) {
            // Skip read-only and ID fields
            if (!p.readOnly() && !p.equals(mapping.id())) {
                res.add(p.name());
            }
        }
        return res;
    }
}
