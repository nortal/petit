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
package com.nortal.petit.beanmapper;

import java.lang.reflect.Type;

/**
 * A T-typed property of a B-typed bean. It can read and write the property and
 * knows its type, name, and column mapping. It can be read-only (usually
 * meaning that it should not participate in updates or inserts) and may have
 * additional configuration.
 * 
 * @author Aleksei Lissitsin
 */
public interface Property<B, T> {
    Type type();

    String name();

    String column();

    void write(B bean, T value);

    T read(B bean);

    boolean readOnly();
    
    boolean isIdProperty();
    
    PropertyConfiguration getConfiguration();
}
