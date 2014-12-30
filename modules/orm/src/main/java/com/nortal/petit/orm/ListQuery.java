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
package com.nortal.petit.orm;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.nortal.petit.orm.statement.clause.Limit;
import com.nortal.petit.orm.statement.clause.OrderEnum;
import com.nortal.petit.orm.statement.clause.OrderSql;
import com.nortal.petit.orm.statement.clause.Where;
import com.nortal.petit.orm.statement.clause.WherePart;

/**
 * Class encapsulating a list query with parameters and result data. Meant to be
 * populated in the web layer, executed in business layer and result shown in
 * web layer.
 * 
 * @author Alrik Peets
 * 
 */
public class ListQuery<B> {

    private Class<B> beanClass;
    private WherePart filter;
    private OrderSql order;
    private Limit limit;

    // Result data
    private QueryResult<B> queryResult;

    public ListQuery(Class<B> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<B> getBeanClass() {
        return beanClass;
    }

    public WherePart getFilter() {
        return filter;
    }

    public OrderSql getOrder() {
        return order;
    }

    public Limit getLimit() {
        return limit;
    }

    public QueryResult<B> getQueryResult() {
        return queryResult;
    }

    public void initQueryResult(String queryString) {
        if (queryResult == null) {
            queryResult = new QueryResult<B>(queryString);
            return;
        }
        if (StringUtils.equals(queryString, queryResult.getQueryString())) {
            queryResult.clearActiveResult();
            // QueryResult'i pole vaja üle initsialiseerida - sama päring.
            // Kasutaja
            // liigub lehekülgede vahel
            // Kontrollime, kas küsitav vahemik on cachetud, kui jah, siis
            // märgime
            // selle aktiivseks vastuseks
            List<B> result = queryResult.getCachedResult(limit);
            if (result != null) {
                queryResult.setResultList(result, limit);
            }
        } else {
            queryResult = new QueryResult<B>(queryString);
        }
    }

    // ======== Mutators =========
    public void addOrder(String property, OrderEnum ordering) {
        if (order == null) {
            order = new OrderSql();
        }
        order.add(property, ordering);
    }

    public void clearOrder() {
        order = null;
    }

    public void addFilter(WherePart filter) {
        if (this.filter == null) {
            this.filter = filter;
        } else {
            this.filter = Where.and(this.filter, filter);
        }
    }

    public void clearFilter() {
        filter = null;
    }

    public void setLimit(int offset, int limit) {
        this.limit = new Limit(offset, limit);
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }
}
