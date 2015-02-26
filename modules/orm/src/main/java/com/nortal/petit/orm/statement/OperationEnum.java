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
import java.util.Arrays;
import java.util.Collection;

import com.nortal.petit.core.util.SqlUtil;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @author Alrik Peets
 * @created 29.04.2013
 */
public enum OperationEnum {
    EQ("=") {
        @Override
        public String getNullCondition(String columnName) {
            return columnName + " IS NULL ";
        }

        @Override
        public String getCollectionCondition(String columnName, Collection<?> values) {
            StringBuilder result = new StringBuilder();
            SqlUtil.setInOperator(values.size(), columnName, result);
            return result.toString();
        }
    },
    NEQ("<>") {
        @Override
        public String getNullCondition(String columnName) {
            return columnName + " IS NOT NULL ";
        }

        @Override
        public String getCollectionCondition(String columnName, Collection<?> values) {
            StringBuilder result = new StringBuilder();
            SqlUtil.setNotInOperator(values.size(), columnName, result);
            return result.toString();
        }
    },
    LIKE("LIKE"), GT(">"), LT("<"), GTE(">="), LTE("<="), //
    ILIKE("LIKE") {
        @Override
        public String getOperationCondition(String columnName) {
            return "lower(" + columnName + ") LIKE lower(?)"; 
        }
    },
    RANGE("RANGE") {
        @Override
        public String getOperationCondition(String columnName) {
            throw new IllegalStateException("Range operation must be supplied exactly 2 parameters!");
        }

        @Override
        public String getCollectionCondition(String columnName, Collection<?> values) {
            if (values == null || values.size() != 2) {
                throw new IllegalStateException("Wrong number of parameters (" + values + ") for range operation!");
            }
            return columnName + " BETWEEN ? AND ? ";
        }
    },
    // This parameter treats property as a custom SQL snippet
    CUSTOM("CUSTOM") {
        @Override
        public String getNullCondition(String columnName) {
            return columnName;
        }

        @Override
        public String getCollectionCondition(String columnName, Collection<?> values) {
            return columnName;
        }

        @Override
        public String getOperationCondition(String columnName) {
            return columnName;
        }

        @Override
        public boolean supportsMultipleValues() {
            return true;
        }

        @Override
        public boolean isMappable() {
            return false;
        }

    };

    private String operation;

    private OperationEnum(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public String getNullCondition(String columnName) {
        throw new IllegalStateException("StatementBuilder.getWhereClause: operation " + this.operation
                + " is not supported for NULL values");
    }

    public String getCollectionCondition(String columnName, Collection<?> values) {
        throw new IllegalStateException("StatementBuilder.getWhereClause: operation " + operation
                + " is not supported for collection values");
    }

    public String getOperationCondition(String columnName) {
        return columnName + " " + operation + " ?";
    }

    public boolean supportsMultipleValues() {
        return false;
    }

    public boolean isMappable() {
        return true;
    }

    public String getOperationSql(String property, Object value) {
        if (value == null) {
            return getNullCondition(property);
        } else if (value instanceof Collection) {
            return getCollectionCondition(property, (Collection<?>) value);
        } else if (value.getClass().isArray()) {
            return getCollectionCondition(property, new ArrayList<Object>(Arrays.asList((Object[]) value)));
        } else {
            return getOperationCondition(property);
        }
    }
}
