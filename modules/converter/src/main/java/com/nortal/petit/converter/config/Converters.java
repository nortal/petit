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

import java.lang.reflect.Type;
import java.util.Map;

import com.nortal.petit.converter.Converter;
import com.nortal.petit.converter.provider.Container;
import com.nortal.petit.converter.provider.Provider;
import com.nortal.petit.converter.provider.SimpleContainer;


public abstract class Converters implements Container<Type, Converter<?, ?>> {

	private final SimpleContainer<Type, Converter<?, ?>> staticProvider = new SimpleContainer<>();
	private Provider<Type, Converter<?, ?>> factory = ($) -> null;


	protected abstract Type getKey(Converter<?, ?> converter);

	public void add(Converter<?, ?> converter) {
		put(getKey(converter), converter);
	}

	/**
	 * Sets dynamic provider of Converters. It will be used in addition to converters that are already
	 * mapped with {@link #add(Converter)} method.
	 *
	 * @param converterFactory must be not null
	 */
	public void setConverterProvider(Provider<Type, Converter<?, ?>> converterFactory) {
		factory = converterFactory;
	}

	@Override
	public Converter<?, ?> get(Type type) {
		Converter<?, ?> converter = staticProvider.get(type);
		if (converter != null) {
			return converter;
		}
		Converter<?, ?> dynamicallyProvided = factory.get(type);
		if (dynamicallyProvided != null) {
			return dynamicallyProvided;
		}
		return null;
	}

	@Override
	public void put(Type type, Converter<?, ?> converter) {
		staticProvider.put(type, converter);
	}

	@Override
	public void putAll(Map<Type, Converter<?, ?>> converters) {
		staticProvider.putAll(converters);
	}
}
