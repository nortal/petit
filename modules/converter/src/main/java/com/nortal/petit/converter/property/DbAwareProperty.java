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

import com.nortal.petit.beanmapper.Property;
import com.nortal.petit.beanmapper.PropertyReader;

public class DbAwareProperty<B, T> extends PropertyWrapper<B, T>{
	private PropertyReader propertyReader;
	
	private PropertyAdapter<?, T> readAdapter;
	
	private PropertyAdapter<T, ?> writeAdapter;
	
	public DbAwareProperty(Property<B, T> delegate) {
		super(delegate);
	}

	public PropertyReader getPropertyReader() {
		return propertyReader;
	}

	public void setPropertyReader(PropertyReader propertyReader) {
		this.propertyReader = propertyReader;
	}

	public PropertyAdapter<?, T> getReadAdapter() {
		return readAdapter;
	}

	public void setReadAdapter(PropertyAdapter<?, T> readAdapter) {
		this.readAdapter = readAdapter;
	}

	public PropertyAdapter<T, ?> getWriteAdapter() {
		return writeAdapter;
	}

	public void setWriteAdapter(PropertyAdapter<T, ?> writeAdapter) {
		this.writeAdapter = writeAdapter;
	}
}
