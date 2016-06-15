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
package com.nortal.petit.converter;

import java.lang.reflect.Type;

public class ConverterWrapper<F, T> implements Converter<F, T> {
	protected Converter<F, T> delegate;
	
	public ConverterWrapper(Converter<F, T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T convert(F value) {
		return delegate.convert(value);
	}

	@Override
	public Type getFromType() {
		return delegate.getFromType();
	}

	@Override
	public Type getToType() {
		return delegate.getToType();
	}
}
