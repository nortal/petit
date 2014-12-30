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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.orm.persist.BeanHandler;
import com.nortal.petit.orm.persist.CollectionPersistMethods;
import com.nortal.petit.orm.persist.CollectionPersistUtil;
import com.nortal.petit.orm.persist.ItemHandler;
import com.nortal.petit.orm.relation.RelationLoader;
import com.nortal.petit.orm.relation.RelationMapper;
import com.nortal.petit.orm.relation.RelationUtil;
import com.nortal.petit.orm.statement.DeleteStatement;
import com.nortal.petit.orm.statement.InsertStatement;
import com.nortal.petit.orm.statement.LoadStatement;
import com.nortal.petit.orm.statement.QueryStatement;
import com.nortal.petit.orm.statement.StatementBuilder;
import com.nortal.petit.orm.statement.UpdateStatement;
import com.nortal.petit.orm.statement.clause.SimpleWherePart;
import com.nortal.petit.orm.statement.clause.Where;

/**
 * Support class for statement APIs.
 * 
 * @author Alrik Peets
 * @author Aleksei Lissitsin
 */
public class StatementSupport {

    private StatementConfiguration statementConfiguration;

    public StatementSupport(StatementConfiguration statementConfiguration) {
        this.statementConfiguration = statementConfiguration;
    }

    private JdbcTemplate getJdbcTemplate() {
        return statementConfiguration.getJdbcTemplate();
    }

    private StatementBuilder getStatementBuilder() {
        return statementConfiguration.getStatementBuilder();
    }

    // Insert methods

    /**
     * Creates insert statement by bean class
     * 
     * @param beanClass
     *            Bean class
     * @return {@link InsertStatement}
     */
    public <B> InsertStatement<B> insertStmForType(Class<B> beanClass) {
        return new InsertStatement<B>(getJdbcTemplate(), getStatementBuilder(), beanClass);
    }

    /**
     * Creates insert statement by bean
     * 
     * @param bean
     *            Bean to insert
     * @return {@link InsertStatement}
     */
    @SuppressWarnings("unchecked")
    public <B> InsertStatement<B> insertStm(B bean) {
        return insertStm(Arrays.asList(bean));
    }

    /**
     * Creates insert statement by beans
     * 
     * @param beans
     *            Beans to insert
     * @return {@link InsertStatement}
     */
    public <B> InsertStatement<B> insertStm(Collection<B> beans) {
        return new InsertStatement<B>(getJdbcTemplate(), getStatementBuilder(), (B[]) beans.toArray());
    }

    /**
     * Inserts bean by primary key mapping
     * 
     * @param bean
     *            Bean to insert
     */
    @SuppressWarnings("unchecked")
    public <B> void insert(B bean) {
        if (bean == null) {
            return;
        }
        insert(Arrays.asList(bean));
    }

    /**
     * Inserts beans by primary key mapping
     * 
     * @param beans
     *            Beans to insert
     */
    public <B> void insert(Collection<B> beans) {
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }
        insertStm(beans).exec();
    }

    // Update methods

    /**
     * Creates update statement by bean class
     * 
     * @param beanClass
     *            Bean class
     * @return {@link UpdateStatement}
     */
    public <B> UpdateStatement<B> updateStm(Class<B> beanClass) {
        return new UpdateStatement<B>(getJdbcTemplate(), getStatementBuilder(), beanClass);
    }

    /**
     * Creates update statement by bean
     * 
     * @param bean
     *            Bean to update
     * @return {@link UpdateStatement}
     */
    @SuppressWarnings("unchecked")
    public <B> UpdateStatement<B> updateStm(B bean) {
        return updateStm(Arrays.asList(bean));
    }

    /**
     * Creates update statement by beans
     * 
     * @param beans
     *            Beans to update
     * @return {@link UpdateStatement}
     */
    public <B> UpdateStatement<B> updateStm(Collection<B> beans) {
        return new UpdateStatement<B>(getJdbcTemplate(), getStatementBuilder(), (B[]) beans.toArray());
    }

    /**
     * Updates bean by primary key mapping
     * 
     * @param bean
     *            Bean to update
     */
    @SuppressWarnings("unchecked")
    public <B> void update(B bean) {
        if (bean == null) {
            return;
        }
        update(Arrays.asList(bean));
    }

    /**
     * Updates beans by primary key mapping
     * 
     * @param beans
     *            Beans to update
     */
    public <B> void update(Collection<B> beans) {
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }

        UpdateStatement<B> stm = new UpdateStatement<B>(getJdbcTemplate(), getStatementBuilder(), (B[]) beans.toArray());
        if (stm.getMapping().id() != null) {
            stm.exec();
        } else {
            throw new PersistenceException("Model " + beans.iterator().next().getClass().getSimpleName()
                    + " does not have primary key mapping");
        }
    }

    // Delete methods

    /**
     * Creates delete statement by bean class
     * 
     * @param beanClass
     *            Bean class
     * @return {@link DeleteStatement}
     */
    public <B> DeleteStatement<B> deleteStm(Class<B> beanClass) {
        return new DeleteStatement<B>(getJdbcTemplate(), getStatementBuilder(), beanClass);
    }

    /**
     * Creates delete statement by bean
     * 
     * @param bean
     *            Bean to delete
     * @return {@link DeleteStatement}
     */
    @SuppressWarnings("unchecked")
    public <B> DeleteStatement<B> deleteStm(B bean) {
        return deleteStm(Arrays.asList(bean));
    }

    /**
     * Creates delete statement by beans
     * 
     * @param beans
     *            Beans to delete
     * @return {@link DeleteStatement}
     */
    public <B> DeleteStatement<B> deleteStm(Collection<B> beans) {
        return new DeleteStatement<B>(getJdbcTemplate(), getStatementBuilder(), (B[]) beans.toArray());
    }

    /**
     * Deletes bean by primary key mapping
     * 
     * @param bean
     *            Bean to deleted
     */
    @SuppressWarnings("unchecked")
    public <B> void delete(B bean) {
        if (bean == null) {
            return;
        }
        delete(Arrays.asList(bean));
    }

    /**
     * Deletes beans by primary key mapping
     * 
     * @param beans
     *            Beans to deleted
     */
    public <B> void delete(Collection<B> beans) {
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }

        DeleteStatement<B> stm = new DeleteStatement<B>(getJdbcTemplate(), getStatementBuilder(), (B[]) beans.toArray());
        if (stm.getMapping().id() != null) {
            stm.exec();
        } else {
            throw new PersistenceException("Model " + beans.iterator().next().getClass().getSimpleName()
                    + " does not have primary key mapping");
        }
    }

    // Load methods

    /**
     * Creates load statement by bean class
     * 
     * @param beanClass
     *            Bean class
     * @return {@link LoadStatement}
     */
    public <B> LoadStatement<B> loadStm(Class<B> beanClass) {
        return new LoadStatement<B>(getJdbcTemplate(), getStatementBuilder(), beanClass);
    }

    /**
     * Loads bean by primary key mapping
     * 
     * @param beanClass
     *            Bean class
     * @param id
     *            Primary key value
     * @return Bean
     */
    public <B> B loadById(Class<B> beanClass, Object id) {
        Assert.notNull(id, "id is mandatory");

        LoadStatement<B> stm = loadStm(beanClass);
        BeanMapping<B> mapping = stm.getMapping();
        if (mapping.id() != null) {
            stm.where(mapping.id().name(), id);
        } else {
            throw new PersistenceException("Model " + mapping + " does not have primary key mapping");
        }
        return stm.single();
    }

    private class Methods<B> implements CollectionPersistMethods<B> {
        @Override
        public void insert(Collection<B> beans) {
            StatementSupport.this.insert(beans);
        }

        @Override
        public void update(Collection<B> beans) {
            StatementSupport.this.update(beans);
        }

        @Override
        public void delete(Collection<B> beans) {
            StatementSupport.this.delete(beans);
        }
    }

    private Methods<Object> methods = new Methods<Object>();

    @SuppressWarnings("unchecked")
    private <B> Methods<B> methods() {
        return (Methods<B>) methods;
    }

    public <B> void save(Collection<B> beans, ItemHandler<B> handler) {
        CollectionPersistUtil.save(beans, handler, this.<B> methods());
    }

    public <B> void save(Collection<B> beans) {
        @SuppressWarnings("unchecked")
        BeanHandler<B> handler = new BeanHandler<B>().init(beans);

        if (handler != null) {
            save(beans, handler);
        }
    }

    public <B> void persist(Collection<B> newState, Collection<B> oldState, ItemHandler<B> handler) {
        CollectionPersistUtil.persist(newState, oldState, handler, this.<B> methods());
    }

    public <B> void persist(Collection<B> newState, Collection<B> oldState) {
        @SuppressWarnings("unchecked")
        BeanHandler<B> handler = new BeanHandler<B>().init(newState, oldState);

        if (handler != null) {
            persist(newState, oldState, handler);
        }
    }

    /**
     * Inserts or updates beans by primary key mapping
     * 
     * @param beans
     *            Beans to save
     */
    public <B> void save(B... beans) {
        if (ArrayUtils.isEmpty(beans)) {
            return;
        }
        save(Arrays.asList(beans));
    }

    public <T> QueryStatement<T> queryStm(ListQuery<T> listQuery) {
        return new QueryStatement<T>(listQuery, getStatementBuilder(), getJdbcTemplate());
    }

    public <T, R> void loadRelations(Collection<T> targets, RelationMapper<T, R> relationMapper) {
        RelationUtil.loadRelations(targets, relationMapper, getRelationLoader(relationMapper));
    }

    private <R> RelationLoader<R> getRelationLoader(final RelationMapper<?, R> relationMapper) {
        return new RelationLoader<R>() {
            @Override
            public List<R> loadRelations(Collection<Object> targetIds) {
                SimpleWherePart where = Where.eq(relationMapper.getRelationProperty(), targetIds);
                if (relationMapper.getWhere() != null) {
                    where.and(relationMapper.getWhere());
                }
                return loadStm(relationMapper.getRelationClass()).where(where).all();
            }
        };
    }

    public <T> void loadRelations(Collection<T> targets, RelationMapper<T, ?>... relationMappers) {
        for (RelationMapper<T, ?> mapper : relationMappers) {
            loadRelations(targets, mapper);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void loadRelations(T target, RelationMapper<T, ?>... relationMappers) {
        loadRelations(Arrays.asList(target), relationMappers);
    }
}
