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
package com.nortal.petit.converter.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.nortal.petit.converter.CompositeConverter;
import com.nortal.petit.converter.Converter;

public class WriteConverters extends Converters {

	@Override
	protected Type getKey(Converter<?, ?> converter) {
		return converter.getFromType();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Converter<?, ?> get(Type from) {
		Converter<?, ?> converter = getConverter(from);
		if (converter != null) {
			Converter<?, ?> converter2 = get(converter.getToType());
			if (converter2 != null) {
				return new CompositeConverter(converter, converter2);
			}
		}
		return converter;
	}
	
	private Converter<?, ?> getConverter(Type type) {
		Converter<?, ?> converter = super.get(type);
		if (converter != null) {
			return converter;
		}
		
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType)type).getRawType();
		}
		
		if (type instanceof Class) {
			return getWithSuperclasses((Class<?>)type);
		}
		
		return null;
	}

	private Converter<?, ?> getWithSuperclasses(Class<?> fromType) {
		Class<?> type = fromType;
		while (type != null) {
			Converter<?, ?> converter = getForInterface(type);

			if (converter != null) {
				return converter;
			}
			
			type = type.getSuperclass();
		}

		return null;
	}

	private Converter<?, ?> getForInterface(Class<?> type) {
		Converter<?, ?> converter = super.get(type);
		if (converter != null) {
			return converter;
		}
		for (Class<?> interfaceType : type.getInterfaces()) {
			converter = getForInterface(interfaceType);
			if (converter != null) {
				return converter;
			}
		}
		return null;
	}
}
