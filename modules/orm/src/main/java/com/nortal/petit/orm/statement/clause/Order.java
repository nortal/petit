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
import java.util.List;

import com.google.common.base.Function;

public class Order implements SqlPart {
    private String property;
    private OrderEnum ordering;

    public Order(String property) {
        this(property, OrderEnum.ASC);
    }

    public Order(String property, OrderEnum ordering) {
        this.property = property;
        this.ordering = ordering;
    }

    public String sql(Function<String, String> propertyNameMapper) {
        return propertyNameMapper.apply(property) + " " + ordering.name();
    }

    // ======= Static initializers =======
    public static Order asc(String property) {
        return new Order(property, OrderEnum.ASC);
    }

    public static Order desc(String property) {
        return new Order(property, OrderEnum.DESC);
    }

    @Override
    public List<Object> params(Function<String, Object> paramMapper) {
        return Arrays.asList();
    }
}
