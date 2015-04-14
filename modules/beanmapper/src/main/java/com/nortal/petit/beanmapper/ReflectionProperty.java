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

import java.lang.reflect.Method;

/**
 * A property specified by reflection methods.
 * 
 * @author Aleksei Lissitsin
 * 
 */
public class ReflectionProperty<B, T> extends PropertySupport<B, T> {
    protected Method write;
    protected Method read;

    public ReflectionProperty(String name, Class<T> type, String column, Method write, Method read) {
        this.name = name;
        this.type = type;
        this.column = column;
        this.write = write;
        this.read = read;
    }

    @Override
    public void write(B bean, T value) {
        try {
            write.invoke(bean, value);
        } catch (Exception e) {
            throw ex(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T read(B bean) {
        try {
            return (T) read.invoke(bean);
        } catch (Exception e) {
            throw ex(e);
        }
    }

    private RuntimeException ex(Exception e) {
        return (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
    }

}
