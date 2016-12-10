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
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.nortal.petit.orm.statement.OperationEnum;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @created 29.04.2013
 */
public class SimpleWherePart extends WherePart {

    private String property;
    private Object value;
    private OperationEnum operation;

    public SimpleWherePart(String property, OperationEnum operation, Object value) {
        this.property = property;
        this.operation = operation;
        this.value = value;
    }

    public String getProperty() {
        return this.property;
    }

    public Object getValue() {
        return this.value;
    }

    public OperationEnum getOperation() {
        return operation;
    }

    public String getWhereSql(Function<String, String> propertyNameMapper) {
        String propertyName = operation.isMappable() ? propertyNameMapper.apply(property) : property;
        if (propertyName == null) {
            return "";
        } else {
            return operation.getOperationSql(propertyName, value);
        }
    }

    public void addParameters(List<Object> parameters) {
        if (this.value == null || ValueEnum.NO_VALUE.equals(this.value)) {
            return;
        }

        if (value instanceof Collection) {
            parameters.addAll((Collection<?>) value);

        } else if (value.getClass().isArray()) {
            parameters.addAll(new ArrayList<Object>(Arrays.asList((Object[]) value)));
        } else {
            parameters.add(value);
        }
    }

    @Override
    public String sql(Function<String, String> nameMapper) {
        return getWhereSql(nameMapper);
    }

    @Override
    public List<Object> params(Function<String, Object> paramMapper) {
        List<Object> params = new ArrayList<Object>();
        addParameters(params);
        return SqlPropertyParam.resolvePropParams(params, paramMapper);
    }
}
