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
package com.nortal.petit.orm.relation.fixture;

import java.util.ArrayList;
import java.util.List;

import com.nortal.petit.orm.relation.model.RelationBean;
import com.nortal.petit.orm.relation.model.TargetBean;

public class RelationFixture {

	public static final String DEFAULT_TARGET_DESCRIPTION = "default target";

	public static TargetBean getDefaultTargetBean() {
		TargetBean tb = new TargetBean();

		tb.setId(10L);
		tb.setDescription(DEFAULT_TARGET_DESCRIPTION);

		return tb;
	}

	public static List<RelationBean> getDefaultRelations(Long targetBeanId) {
		List<RelationBean> rbs = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			RelationBean rb = new RelationBean();
			rb.setId(20L + i);
			rb.setRelationDescription("relation nr. " + i);
			rb.setTargetBeanId(targetBeanId == null ? 10L : targetBeanId);
			rbs.add(rb);
		}

		return rbs;
	}

}
