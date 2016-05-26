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

import java.lang.reflect.Type;

import com.nortal.petit.beanmapper.Property;
import com.nortal.petit.converter.Converter;
import com.nortal.petit.converter.provider.Provider;

public class SimplePropertyWriter implements PropertyWriter {
	
	private Provider<Type, Converter<?, ?>> writeConverters;
	
	private Provider<Type, PropertyAdapter<?, ?>> adapters;
	
	public SimplePropertyWriter(Provider<Type, Converter<?, ?>> writeConverters, Provider<Type, PropertyAdapter<?, ?>> adapters) {
		this.writeConverters = writeConverters;
		this.adapters = adapters;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Object convert(Object value, Property<?, ?> property) {
		Type type = property.type();
		PropertyAdapter<Object,Object> propertyAdapter = getPropertyAdapter(property);
		if (propertyAdapter != null) {
			value = propertyAdapter.convert(value, (Property<?, Object>) property);
			type = propertyAdapter.getToType();
		}
		Converter<Object, Object> converter = (Converter<Object, Object>) writeConverters.get(type);
		if (converter != null) {
			return converter.convert(value);
		}
		
		return value;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PropertyAdapter<Object, Object> getPropertyAdapter(Property<?, ?> property) {
		if (property instanceof DbAwareProperty) {
			return ((DbAwareProperty)property).getWriteAdapter();
		}
		return (PropertyAdapter<Object, Object>) adapters.get(property.type());
	}

}
