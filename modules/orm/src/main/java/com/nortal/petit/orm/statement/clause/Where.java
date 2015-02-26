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

import java.util.Arrays;

import com.nortal.petit.orm.statement.OperationEnum;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class Where {

    public static SimpleWherePart of(String property, OperationEnum operation, Object value) {
        return new SimpleWherePart(property, operation, value);
    }

    public static SimpleWherePart eq(String property, Object value) {
        return new SimpleWherePart(property, OperationEnum.EQ, value);
    }

    public static SimpleWherePart neq(String property, Object value) {
        return new SimpleWherePart(property, OperationEnum.NEQ, value);
    }

    public static SimpleWherePart like(String property, Object value) {
        return new SimpleWherePart(property, OperationEnum.LIKE, value);
    }
    
    public static SimpleWherePart ilike(String property, Object value) {
        return new SimpleWherePart(property, OperationEnum.ILIKE, value);
    }

    public static SimpleWherePart gt(String property, Object value) {
        return new SimpleWherePart(property, OperationEnum.GT, value);
    }

    public static SimpleWherePart lt(String property, Object value) {
        return new SimpleWherePart(property, OperationEnum.LT, value);
    }

    public static SimpleWherePart gte(String property, Object value) {
        return new SimpleWherePart(property, OperationEnum.GTE, value);
    }

    public static SimpleWherePart lte(String property, Object value) {
        return new SimpleWherePart(property, OperationEnum.LTE, value);
    }

    public static SimpleWherePart range(String property, Object startValue, Object endValue) {
        return new SimpleWherePart(property, OperationEnum.RANGE, new Object[] { startValue, endValue });
    }

    public static SimpleWherePart custom(String property, Object value) {
        return new SimpleWherePart(property, OperationEnum.CUSTOM, value);
    }

    public static SimpleWherePart custom(String property) {
        return new SimpleWherePart(property, OperationEnum.CUSTOM, ValueEnum.NO_VALUE);
    }

    public static CompositeWherePart and(SqlPart... parts) {
        return new CompositeWherePart(LogicalOperation.AND, Arrays.asList(parts));
    }

    public static CompositeWherePart or(SqlPart... parts) {
        return new CompositeWherePart(LogicalOperation.OR, Arrays.asList(parts));
    }

    /**
     * Convenience method for creating an SqlPropertyParam.
     */
    public static Object prop(String prop) {
        return new SqlPropertyParam(prop);
    }
}
