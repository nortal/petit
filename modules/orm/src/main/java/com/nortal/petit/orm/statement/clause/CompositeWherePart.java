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
package com.nortal.petit.orm.statement.clause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class CompositeWherePart extends WherePart {
    private LogicalOperation operation;
    private List<SqlPart> parts;

    public static CompositeWherePart of(LogicalOperation operation, SqlPart part, SqlPart... parts) {
        List<SqlPart> ps = new ArrayList<SqlPart>();
        ps.add(part);
        ps.addAll(Arrays.asList(parts));
        return new CompositeWherePart(operation, ps);
    }

    public CompositeWherePart(LogicalOperation operation, List<SqlPart> parts) {
        this.operation = operation;
        this.parts = new ArrayList<SqlPart>(parts.size());
        for (SqlPart part : parts) {
            if (part instanceof CompositeWherePart) {
                CompositeWherePart cpart = (CompositeWherePart) part;
                if (cpart.operation() != null) {
                    if (this.operation == null) {
                        this.operation = cpart.operation();
                    }
                    if (this.operation == cpart.operation()) {
                        this.parts.addAll(cpart.parts());
                        continue;
                    }
                }
            }
            this.parts.add(part);
        }
    }

    public LogicalOperation operation() {
        return operation;
    }

    public void operation(LogicalOperation op) {
        operation = op;
    }

    public List<SqlPart> parts() {
        return parts;
    }

    @Override
    public String sql(Function<String, String> nameMapper) {
        List<SqlPart> parts = parts();
        if (parts == null || parts.isEmpty()) {
            return null;
        }

        if (parts.size() == 1) {
            return parts.get(0).sql(nameMapper);
        }

        List<String> sqls = new ArrayList<String>(parts.size());
        for (SqlPart part : parts) {
            sqls.add(part.sql(nameMapper));
        }

        return "(" + StringUtils.join(sqls, " " + operation().toString() + " ") + ")";
    }

    @Override
    public List<Object> params(Function<String, Object> paramMapper) {
        List<Object> res = new ArrayList<Object>();
        for (SqlPart part : parts()) {
            res.addAll(part.params(paramMapper));
        }
        return res;
    }

    public CompositeWherePart add(WherePart part) {
        parts().add(part);
        return this;
    }

    public CompositeWherePart eq(String property, Object value) {
        return add(Where.eq(property, value));
    }

    public CompositeWherePart neq(String property, Object value) {
        return add(Where.neq(property, value));
    }

    public CompositeWherePart like(String property, Object value) {
        return add(Where.like(property, value));
    }

    public CompositeWherePart gt(String property, Object value) {
        return add(Where.gt(property, value));
    }

    public CompositeWherePart lt(String property, Object value) {
        return add(Where.lt(property, value));
    }

    public CompositeWherePart gte(String property, Object value) {
        return add(Where.gte(property, value));
    }

    public CompositeWherePart lte(String property, Object value) {
        return add(Where.lte(property, value));
    }

    public CompositeWherePart range(String property, Object startValue, Object endValue) {
        return add(Where.range(property, startValue, endValue));
    }

    public CompositeWherePart custom(String property, Object value) {
        return add(Where.custom(property, value));
    }

    public CompositeWherePart custom(String property) {
        return add(Where.custom(property));
    }
}
