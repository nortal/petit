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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class OrderSql implements SqlPart {
    private List<Order> orderList = new ArrayList<Order>();

    public OrderSql() {
    }

    public OrderSql add(OrderSql orderSql) {
        orderList.addAll(orderSql.orderList);
        return this;
    }

    public OrderSql add(Order... orders) {
        if (orders != null && orders.length > 0) {
            for (Order order : orders) {
                orderList.add(order);
            }
        }
        return this;
    }

    public OrderSql add(String property, OrderEnum ordering) {
        orderList.add(new Order(property, ordering));
        return this;
    }

    public OrderSql add(String property) {
        orderList.add(new Order(property));
        return this;
    }

    public OrderSql asc(String... properties) {
        for (String p : properties) {
            add(Order.asc(p));
        }
        return this;
    }

    public OrderSql desc(String... properties) {
        for (String p : properties) {
            add(Order.desc(p));
        }
        return this;
    }

    public String sql(Function<String, String> propertyNameMapper) {
        if (orderList.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder("ORDER BY ");
        Joiner.on(", ").appendTo(
                result,
                Iterables.transform(orderList, new SqlPartFunction(propertyNameMapper != null ? propertyNameMapper
                        : Functions.<String> identity())));

        return result.toString();
    }

    // ================ Static initializers =====================
    public static OrderSql order(String property, OrderEnum ordering) {
        return new OrderSql().add(new Order(property, ordering));
    }

    public static OrderSql order(String property) {
        return order(property, OrderEnum.ASC);
    }

    public static OrderSql order(Order... orders) {
        return new OrderSql().add(orders);
    }

    @Override
    public List<Object> params(Function<String, Object> paramMapper) {
        return Arrays.asList();
    }
}
