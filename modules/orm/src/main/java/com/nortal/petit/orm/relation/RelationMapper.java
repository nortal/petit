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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import com.google.common.base.Function;
import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.BeanMappings;
import com.nortal.petit.beanmapper.Property;
import com.nortal.petit.orm.statement.clause.WherePart;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @created 21.05.2013
 */
public class RelationMapper<T, R> implements RelationInfo<T, R> {
	private BeanMapping<T> targetMapper;
	private BeanMapping<R> relationMapper;
	private Class<R> relationClass;
	private Property<T, Object> targetProperty;
	private Function<T, Object> targetId;
	private Property<R, Object> relationProperty;
	private Function<R, Object> relationId;
	private Method associateMethod;
	private WherePart where;

	public RelationMapper(Class<T> target, Class<R> relation,
			String targetProperty, String relationProperty, WherePart where) {
		this(target, relation, targetProperty, relationProperty, null, where);
	}

	public RelationMapper(Class<T> target, Class<R> relation,
			String targetProperty, String relationProperty,
			String targetMapping, WherePart where) {
		Assert.notNull(target, "RelationMapper.construct: target is mandatory");
		Assert.notNull(relation,
				"RelationMapper.construct: relation is mandatory");
		Assert.isTrue(
				StringUtils.isNotEmpty(targetProperty)
						|| StringUtils.isNotEmpty(relationProperty),
				"RelationMapper.construct: targetProperty or relationProperty is mandatory");

		this.relationClass = relation;

		this.targetMapper = BeanMappings.get(target);
		this.relationMapper = BeanMappings.get(relation);

		// Init target mapping property
		if (StringUtils.isEmpty(targetProperty)) {
			this.targetProperty = targetMapper.id();
		} else {
			this.targetProperty = targetMapper.props().get(targetProperty);
		}
		Assert.notNull(this.targetProperty,
				"RelationMapper.construct: targetProperty is mandatory");

		targetId = new PropertyFunction<T, Object>(this.targetProperty);

		// Init target mapping property
		if (StringUtils.isEmpty(relationProperty)) {
			this.relationProperty = relationMapper.id();
		} else {
			this.relationProperty = relationMapper.props()
					.get(relationProperty);
		}
		Assert.notNull(this.relationProperty,
				"RelationMapper.construct: relationProperty is mandatory");

		relationId = new PropertyFunction<R, Object>(this.relationProperty);

		if (StringUtils.isNotEmpty(targetMapping)) {
			PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(target,
					targetMapping);
			this.associateMethod = pd.getWriteMethod();
		}

		// Mapping conditions
		this.where = where;
	}

	public WherePart getWhere() {
		return where;
	}

	public static <T, R> RelationMapper<T, R> oneToMany(Class<T> target,
			Class<R> relation, String mappedBy, String targetMapping,
			WherePart where) {
		return oneToMany(target, relation, null, mappedBy, targetMapping, where);
	}

	public static <T, R> RelationMapper<T, R> oneToMany(Class<T> target,
			Class<R> relation, String targetProperty, String relationProperty,
			String targetMapping, WherePart where) {
		return new RelationMapper<T, R>(target, relation, targetProperty,
				relationProperty, targetMapping, where);
	}

	public static <T, R> RelationMapper<T, R> manyToOne(Class<T> target,
			Class<R> relation, String mappedBy, String targetMapping,
			WherePart where) {
		return manyToOne(target, relation, mappedBy, null, targetMapping, where);
	}

	public static <T, R> RelationMapper<T, R> manyToOne(Class<T> target,
			Class<R> relation, String targetProperty, String relationProperty,
			String targetMapping, WherePart where) {
		return new RelationMapper<T, R>(target, relation, targetProperty,
				relationProperty, targetMapping, where);
	}

	public static <T, R> RelationMapper<T, R> oneToOne(Class<T> target,
			Class<R> relation, String mappedBy, String targetMapping,
			WherePart where) {
		return manyToOne(target, relation, mappedBy, null, targetMapping, where);
	}

	public static <T, R> RelationMapper<T, R> oneToOne(Class<T> target,
			Class<R> relation, String targetProperty, String relationProperty,
			String targetMapping, WherePart where) {
		return new RelationMapper<T, R>(target, relation, targetProperty,
				relationProperty, targetMapping, where);
	}

	@Override
	public Function<T, Object> targetId() {
		return targetId;
	}

	@Override
	public Function<R, Object> relationId() {
		return relationId;
	}

	public Class<R> getRelationClass() {
		return relationClass;
	}

	public String getRelationProperty() {
		return relationProperty.name();
	}

	public String getTargetProperty() {
		return targetProperty.name();
	}

	@Override
	public void associate(T target, List<R> relation) {
		if (associateMethod == null) {
			throw new IllegalStateException(
					"RelationMapper.initRelation: targetMapping is mandatory when using default relation initialization");
		}

		if (associateMethod.getParameterTypes().length != 1) {
			throw new IllegalStateException(
					"RelationMapper.initRelation: no proper relation injection method found for target="
							+ targetMapper.table());
		}

		Class<?> propertyArgs = associateMethod.getParameterTypes()[0];

		try {
			if (Collection.class.isAssignableFrom(propertyArgs)) {
				associateMethod.invoke(target, relation);
			} else {
				if (relation.size() == 1) {
					associateMethod.invoke(target, relation.get(0));
				} else {
					throw new IllegalStateException(
							"RelationMapper.initRelation: expected only one relation object for target="
									+ targetMapper.table() + " relation="
									+ relationMapper.table());
				}
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}