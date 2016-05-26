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
import com.nortal.petit.beanmapper.PropertyConfiguration;

public class PropertyWrapper<B, T> implements Property<B, T>{
	private Property<B, T> delegate;
	
	public PropertyWrapper(Property<B, T> delegate) {
		this.delegate = delegate;
	}

	public Type type() {
		return delegate.type();
	}

	public String name() {
		return delegate.name();
	}

	public String column() {
		return delegate.column();
	}

	public void write(B bean, T value) {
		delegate.write(bean, value);
	}

	public T read(B bean) {
		return delegate.read(bean);
	}

	public boolean readOnly() {
		return delegate.readOnly();
	}

	public boolean isIdProperty() {
		return delegate.isIdProperty();
	}

	public PropertyConfiguration getConfiguration() {
		return delegate.getConfiguration();
	}
}
