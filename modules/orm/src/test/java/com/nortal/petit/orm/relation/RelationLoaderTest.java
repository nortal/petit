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
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.nortal.petit.orm.relation.fixture.RelationFixture;
import com.nortal.petit.orm.relation.model.RelationBean;
import com.nortal.petit.orm.relation.model.TargetBean;

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

    private static class TestRelationLoader implements RelationLoader<RelationBean> {

        @Override
        public List<RelationBean> loadRelations(Collection<Object> targetIds) {
            return RelationFixture.getDefaultRelations(null);
        }

    }

}
