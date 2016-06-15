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
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nortal.petit.beanmapper.Property;
import com.nortal.petit.beanmapper.PropertyReader;
import com.nortal.petit.converter.provider.Provider;
import com.nortal.petit.converter.util.ResultSetReader;

public class SimplePropertyReader implements PropertyReader {
	private ResultSetReader rsr;

	private Provider<Type, PropertyReader> propertyReaders;
	private Provider<Type, PropertyAdapter<?, ?>> propertyAdapters;

	public SimplePropertyReader(ResultSetReader rsr) {
		this.rsr = rsr;
	}
	
	public SimplePropertyReader(ResultSetReader rsr, Provider<Type, PropertyReader> propertyReaders,
			Provider<Type, PropertyAdapter<?, ?>> propertyAdapters) {
		this.rsr = rsr;
		this.propertyReaders = propertyReaders;
		this.propertyAdapters = propertyAdapters;
	}


	@Override
	public <T> T get(ResultSet rs, Property<?, T> p) throws SQLException {
		if (p instanceof DbAwareProperty) {
			DbAwareProperty<?, T> prop = (DbAwareProperty<?, T>) p;
			if (prop.getPropertyReader() != null) {
				return prop.getPropertyReader().get(rs, prop);
			}
			
			if (prop.getReadAdapter() != null) {
				return readWithAdapter(rs, prop.getReadAdapter(), prop);
			}
		}
		
		PropertyReader propertyReader = propertyReaders.get(p.type());
		if (propertyReader != null) {
			return propertyReader.get(rs, p);
		}
		
		@SuppressWarnings("unchecked")
		PropertyAdapter<?,T> adapter = (PropertyAdapter<?, T>) propertyAdapters.get(p.type());
		if (adapter != null) {
			return readWithAdapter(rs, adapter, p);
		}
		
		return rsr.get(p.type(), rs, p.column());
	}

	private <T> T readWithAdapter(ResultSet rs, PropertyAdapter<?, T> adapter, Property<?, T> prop) throws SQLException {
		@SuppressWarnings("unchecked")
		PropertyAdapter<Object, T> propertyAdapter = (PropertyAdapter<Object, T>) adapter;
		Object object = rsr.get(propertyAdapter.getFromType(), rs, prop.column());
		return propertyAdapter.convert(object, prop);
	}
}
