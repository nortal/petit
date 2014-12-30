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

import com.nortal.petit.core.BaseDAO;
import com.nortal.petit.orm.statement.StatementBuilder;

/**
 * Example Base DAO for using Statement API for queries.
 * 
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @author Alrik Peets
 */
public abstract class BaseStatementDAO extends BaseDAO implements StatementConfiguration {

    private BeanPropertyConverter converter = new DefaultBeanPropertyConverter();

    private StatementSupport statementSupport;

    public BaseStatementDAO() {
        this.statementSupport = new StatementSupport(this);
    }

    protected abstract StatementBuilder createStatementBuilder();

    /**
     * Setter to allow overriding the default converter
     */
    public void setConverter(BeanPropertyConverter converter) {
        this.converter = converter;
    }

    public BeanPropertyConverter getConverter() {
        return converter;
    }

    /**
     * Creates statement builder
     * 
     * @return {@link StatementBuilder}
     */
    @Override
    public StatementBuilder getStatementBuilder() {
        StatementBuilder statementBuilder = createStatementBuilder();
        statementBuilder.setConverter(getConverter());
        return statementBuilder;
    }

    protected StatementSupport getStatementSupport() {
        return statementSupport;
    }
}
