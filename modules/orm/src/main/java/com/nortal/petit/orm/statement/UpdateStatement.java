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

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @created 30.04.2013
 * @param <B>
 */
public class UpdateStatement<B> extends BeansStatement<B, UpdateStatement<B>> {

    public UpdateStatement(JdbcOperations jdbcTemplate, StatementBuilder statementBuilder, B... beans) {
        Assert.isTrue(ArrayUtils.isNotEmpty(beans), "UpdateStatement.construct: beans are mandatory");

        this.beans = Arrays.asList(beans);
        super.init(jdbcTemplate, statementBuilder, (Class<B>) beans[0].getClass());

        setBy(StatementUtil.toStringArray(getWritableProps(getMapping())));

        if (getMapping().id() != null) {
            whereBy(getMapping().id().name());
        }
    }

    public UpdateStatement(JdbcOperations jdbcTemplate, StatementBuilder statementBuilder, Class<B> beanClass) {
        Assert.isTrue(beanClass != null, "UpdateStatement.construct: beanClass is mandatory");
        super.init(jdbcTemplate, statementBuilder, beanClass);
    }

    @Override
    protected void prepare() {
        super.prepare();
        prepareSet();
        setSql(getStatementBuilder().getUpdate());
    }

    @Override
    protected StatementType getStatementType() {
        return StatementType.UPDATE;
    }
}
