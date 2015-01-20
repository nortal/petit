package com.nortal.petit.orm.relation;

import org.junit.Assert;
import org.junit.Test;

public class RelationMapperTest {

	@Test
	public void testTargetProperty__NameAndFunction() {
		TargetBean tb = new TargetBean();
		tb.setId(10L);

		RelationMapper<TargetBean, RelationBean> rm = new RelationMapper<TargetBean, RelationBean>(
				TargetBean.class, RelationBean.class, "id", "targetBeanId",
				null);

		Assert.assertNotNull(rm.getTargetProperty());
		Assert.assertEquals("id", rm.getTargetProperty());
		Assert.assertEquals(10L, rm.targetId().apply(tb));
	}
}
