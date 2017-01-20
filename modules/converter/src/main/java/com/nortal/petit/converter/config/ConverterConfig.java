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

import com.nortal.petit.beanmapper.PropertyReader;
import com.nortal.petit.converter.columnreader.ColumnReader;
import com.nortal.petit.converter.columnreader.ConverterColumnReaders;
import com.nortal.petit.converter.columnreader.FailingColumnReader;
import com.nortal.petit.converter.columnreader.StandardStrategies;
import com.nortal.petit.converter.property.PropertyAdapter;
import com.nortal.petit.converter.property.PropertyWriter;
import com.nortal.petit.converter.property.SimplePropertyReader;
import com.nortal.petit.converter.property.SimplePropertyWriter;
import com.nortal.petit.converter.provider.CachingProvider;
import com.nortal.petit.converter.provider.ChainProvider;
import com.nortal.petit.converter.provider.Container;
import com.nortal.petit.converter.provider.SimpleContainer;
import com.nortal.petit.converter.util.ResultSetReader;

public class ConverterConfig {
    private static ConverterConfig instance;
    
    public static ConverterConfig instance() {
        if (instance == null) {
            instance = new ConverterConfig();
        }
        return instance;
    }
    
    private ReadConverters readConverters = new ReadConverters();
    private Container<Type, ColumnReader<?>> readers;
    private ResultSetReader resultSetReader;
    
    
    private Container<Type, PropertyReader> propertyReaders = new SimpleContainer<>();
	private Container<Type, PropertyAdapter<?, ?>> readPropertyAdapters = new SimpleContainer<>();
	private PropertyReader propertyReader;
	
	private WriteConverters writeConverters;
	private Container<Type, PropertyAdapter<?, ?>> writePropertyAdapters = new SimpleContainer<>();
	
	private ColumnReader<?> catchAllReader = new FailingColumnReader();
	
  private PropertyWriter propertyWriter;
    
    public ConverterConfig() {
        readConverters = new ReadConverters();
        
        readers = new SimpleContainer<>();
        readers.putAll(new StandardStrategies().getAll());
        
        ConverterColumnReaders converterStrategies = new ConverterColumnReaders(readConverters, readers);
        
        ChainProvider<Type, ColumnReader<?>> strategies = new ChainProvider<>(
                new CachingProvider<>(converterStrategies), 
                (t) -> catchAllReader);
        
        resultSetReader = new SimpleResultSetReader(strategies);
        
        propertyReader = new SimplePropertyReader(resultSetReader, propertyReaders, readPropertyAdapters);
        
        writeConverters = new WriteConverters();
        propertyWriter = new SimplePropertyWriter(new CachingProvider<>(writeConverters), writePropertyAdapters);
    }

    public Converters getReadConverters() {
        return readConverters;
    }

    public Container<Type, ColumnReader<?>> getColumnReaders() {
        return readers;
    }

    public ResultSetReader getResultSetReader() {
        return resultSetReader;
    }
    
    public Container<Type, PropertyReader> getPropertyReaders() {
		return propertyReaders;
	}
    
    public Container<Type, PropertyAdapter<?, ?>> getReadPropertyAdapters() {
		return readPropertyAdapters;
	}
    
    public Converters getWriteConverters() {
		return writeConverters;
	}
    
    public Container<Type, PropertyAdapter<?, ?>> getWritePropertyAdapters() {
		return writePropertyAdapters;
	}
    
    public PropertyReader getPropertyReader() {
    	return propertyReader;
    }
    
    public PropertyWriter getPropertyWriter() {
		return propertyWriter;
	}
    
    public void setPropertyReader(PropertyReader propertyReader) {
		this.propertyReader = propertyReader;
	}
    
    public void setPropertyWriter(PropertyWriter propertyWriter) {
		this.propertyWriter = propertyWriter;
	}

    public void setCatchAllReader(ColumnReader<?> catchAllReader) {
      this.catchAllReader = catchAllReader;
    }
}
