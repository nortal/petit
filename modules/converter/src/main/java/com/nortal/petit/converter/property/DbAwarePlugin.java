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
package com.nortal.petit.converter.property;

import java.util.HashMap;
import java.util.Map;

import com.nortal.petit.beanmapper.Property;
import com.nortal.petit.beanmapper.PropertyPlugin;

public class DbAwarePlugin implements PropertyPlugin {

	private Map<Class<?>, Object> cache = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <B> Property<B, Object> decorate(Property<B, Object> prop) {
		DbAware dbAware = prop.getConfiguration().getAnnotation(DbAware.class);
		if (dbAware != null) {
			DbAwareProperty<B, Object> dbAwareProperty = new DbAwareProperty<>(prop);
			if (dbAware.propertyReader() != null) {
				dbAwareProperty.setPropertyReader(getInstance(dbAware.propertyReader()));
			}

			if (dbAware.readAdapter() != null) {
				dbAwareProperty.setReadAdapter(getInstance(dbAware.readAdapter()));
			}

			if (dbAware.writeAdapter() != null) {
				dbAwareProperty.setWriteAdapter(getInstance(dbAware.writeAdapter()));
			}
			return dbAwareProperty;
		}
		return prop;
	}

	@SuppressWarnings("unchecked")
	private <T> T getInstance(Class<T> clazz) {
		if (!cache.containsKey(clazz)) {
			cache.put(clazz, createInstance(clazz));
		}
		return (T) cache.get(clazz);
	}

	private <T> T createInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> void register(Class<T> clazz, T object) {
		cache.put(clazz, object);
	}
}
