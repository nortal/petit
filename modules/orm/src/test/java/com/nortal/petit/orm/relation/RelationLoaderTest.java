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
package com.nortal.petit.orm.relation;

import static com.nortal.petit.orm.statement.clause.Where.like;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.nortal.petit.orm.relation.fixture.RelationFixture;
import com.nortal.petit.orm.relation.model.RelationBean;
import com.nortal.petit.orm.relation.model.TargetBean;
import com.nortal.petit.orm.statement.clause.SimpleWherePart;
import com.nortal.petit.orm.statement.clause.Where;
import com.nortal.petit.orm.statement.clause.WherePart;

public class RelationLoaderTest {

    @Test
    public void test__loadRelationsOK() {
        TargetBean tb = RelationFixture.getDefaultTargetBean();
        List<TargetBean> tbs = new ArrayList<>();
        tbs.add(tb);

        assertThat(tb.getRelations(), is(emptyCollectionOf(RelationBean.class)));

        RelationUtil.loadRelations(tbs,
                RelationMapper.oneToMany(TargetBean.class, RelationBean.class, "targetBeanId", "relations", null),
                new TestRelationLoader());
        assertThat(tb.getRelations(), is(not(emptyCollectionOf(RelationBean.class))));
        assertThat(tb.getRelations().size(), equalTo(5));
    }

    @Test
    public void test__loadRelationsAddRelationToResult() {
        TargetBean tb = RelationFixture.getDefaultTargetBean();
        List<TargetBean> tbs = new ArrayList<>();
        tbs.add(tb);

        RelationUtil.loadRelations(tbs,
                RelationMapper.oneToMany(TargetBean.class, RelationBean.class, "targetBeanId", "relations", null),
                new TestRelationLoader());

        tb.getRelations().add(new RelationBean());
        assertThat(tb.getRelations().size(), equalTo(6));

    }

    /**
     * TODO: rewrite these tests when we can use H2 or some other in-memory databases
     * Currently the OutputtingTestRelationLoader class contains almost the same logic are real RelationLoader in action
     */
    @Test
    public void test__loadRelationsCustomWhere() {
        TargetBean tb = RelationFixture.getDefaultTargetBean();
        List<TargetBean> tbs = new ArrayList<>();
        tbs.add(tb);

        WherePart additionalWhere = Where.eq("id", 2L).and(like("description", "default"));

        RelationMapper<TargetBean, RelationBean> rm2 = RelationMapper.oneToMany(TargetBean.class, RelationBean.class, "targetBeanId", "relations", additionalWhere);
        String rm2Additional = rm2.getWhere().toString();
        
        OutputtingTestRelationLoader outputRL = new OutputtingTestRelationLoader(rm2);
        RelationUtil.loadRelations(tbs, rm2, outputRL);

        assertThat(stripBrackets(outputRL.getWhereSql()), containsString(stripBrackets(rm2Additional)));
    }

    private String stripBrackets(String input) {
        return input.replace("(", "").replace(")", "");
    }
    
    private static class TestRelationLoader implements RelationLoader<RelationBean> {

        @Override
        public List<RelationBean> loadRelations(Collection<Object> targetIds) {
            return RelationFixture.getDefaultRelations(null);
        }

    }

    private static class OutputtingTestRelationLoader implements RelationLoader<RelationBean> {
        private RelationMapper relationMapper;
        private String whereSql;
        
        public OutputtingTestRelationLoader(RelationMapper relationMapper) {
            this.relationMapper = relationMapper;
        }
        
        public String getWhereSql() {
            return whereSql;
        }
        
        @Override
        public List<RelationBean> loadRelations(Collection<Object> targetIds) {
            WherePart where = Where.eq(relationMapper.getRelationProperty(), targetIds);
            if (relationMapper.getWhere() != null) {
                where = where.and(relationMapper.getWhere());
            }
            whereSql = where.toString();
            return RelationFixture.getDefaultRelations(null);
        }
        
    }
    
}
