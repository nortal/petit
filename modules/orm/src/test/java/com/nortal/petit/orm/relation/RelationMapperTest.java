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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import com.nortal.petit.orm.relation.fixture.RelationFixture;
import com.nortal.petit.orm.relation.model.InvalidTargetBean;
import com.nortal.petit.orm.relation.model.RelationBean;
import com.nortal.petit.orm.relation.model.TargetBean;

public class RelationMapperTest {

    @Test
    public void testTargetProperty__NameAndFunction() {
        TargetBean tb = new TargetBean();
        tb.setId(10L);

        RelationMapper<TargetBean, RelationBean> rm = new RelationMapper<TargetBean, RelationBean>(
                TargetBean.class, RelationBean.class, "id", "targetBeanId",
                null);

        assertThat(rm.getTargetProperty(), is(notNullValue()));
        assertThat(rm.getTargetProperty(), equalTo("id"));
        assertThat((Long) rm.targetId().apply(tb), equalTo(10L));
    }

    @Test
    public void testTargetProperty__DefaultName() {
        RelationMapper<TargetBean, RelationBean> rm = new RelationMapper<TargetBean, RelationBean>(
                TargetBean.class, RelationBean.class, null, "targetBeanId", null);

        assertThat(rm.getTargetProperty(), is(notNullValue()));
        assertThat(rm.getTargetProperty(), equalTo("id"));
    }

    @Test
    public void test__oneToMany() {
        RelationMapper<TargetBean, RelationBean> rm = RelationMapper.oneToMany(TargetBean.class, RelationBean.class,
                "targetBeanId", "relations", null);
        assertThat(rm.getTargetProperty(), is(notNullValue()));
        assertThat(rm.getTargetProperty(), equalTo("id"));
    }

    @Test
    public void test__oneToManyAlternative() {
        RelationMapper<TargetBean, RelationBean> rm = RelationMapper.oneToMany(TargetBean.class, RelationBean.class,
                "id", "targetBeanId", "relations", null);
        assertThat(rm.getTargetProperty(), is(notNullValue()));
        assertThat(rm.getTargetProperty(), equalTo("id"));
    }

    @Test
    public void testRelationProperty__NameAndFunction() {
        RelationBean rb = new RelationBean();
        rb.setId(20L);
        rb.setRelationDescription("relation");
        rb.setTargetBeanId(10L);

        RelationMapper<TargetBean, RelationBean> rm = new RelationMapper<TargetBean, RelationBean>(
                TargetBean.class, RelationBean.class, "id", "targetBeanId",
                null);

        assertThat(rm.getRelationProperty(), is(notNullValue()));
        assertThat(rm.getRelationProperty(), equalTo("targetBeanId"));
        assertThat((Long) rm.relationId().apply(rb), equalTo(Long.valueOf(10L)));
    }

    @Test
    public void test__associateOK() {
        TargetBean tb = RelationFixture.getDefaultTargetBean();

        RelationMapper<TargetBean, RelationBean> rm = new RelationMapper<TargetBean, RelationBean>(
                TargetBean.class, RelationBean.class, "id", "targetBeanId",
                "relations", null);

        rm.associate(tb, RelationFixture.getDefaultRelations(tb.getId()));

        assertThat(tb, is(notNullValue()));
        assertThat(tb.getRelations(), is(not(empty())));
        assertThat(tb.getRelations().size(), equalTo(5));

    }

    @Test(expected = IllegalStateException.class)
    public void test__associateInvalidTargetMapping() {
        TargetBean tb = RelationFixture.getDefaultTargetBean();

        RelationMapper<TargetBean, RelationBean> rm = new RelationMapper<TargetBean, RelationBean>(
                TargetBean.class, RelationBean.class, "id", "targetBeanId",
                null);

        rm.associate(tb, RelationFixture.getDefaultRelations(tb.getId()));

    }

    @Test(expected = IllegalStateException.class)
    public void test__associateInvalidTargetMappingMethod() {
        InvalidTargetBean itb = new InvalidTargetBean();

        RelationMapper<InvalidTargetBean, RelationBean> rm = new RelationMapper<InvalidTargetBean, RelationBean>(
                InvalidTargetBean.class, RelationBean.class, "id",
                "targetBeanId", "relations", null);

        rm.associate(itb, RelationFixture.getDefaultRelations(null));
    }

}
