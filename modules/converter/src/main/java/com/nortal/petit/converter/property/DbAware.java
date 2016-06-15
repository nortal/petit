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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nortal.petit.beanmapper.PropertyReader;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD})
@SuppressWarnings("rawtypes")
public @interface DbAware {
	Class<? extends PropertyAdapter> readAdapter() default UNDEFINED.class;
	Class<? extends PropertyAdapter> writeAdapter() default UNDEFINED.class;
	Class<? extends PropertyReader> propertyReader() default UNDEFINED.class;
	Class<? extends PropertyAdapterFactory> readAdapterFactory() default UNDEFINED.class;
	Class<? extends PropertyAdapterFactory> writeAdapterFactory() default UNDEFINED.class;
	
	static interface UNDEFINED extends PropertyAdapter, PropertyReader, PropertyAdapterFactory {
		
	}
}
