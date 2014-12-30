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

/**
 * An abstract class for reducing boilerplate code in property implementations
 * 
 * @author Aleksei Lissitsin
 * 
 */
public abstract class PropertySupport<B, T> implements Property<B, T> {
    protected String name;
    protected Class<T> type;
    protected String column;
    protected boolean readOnly = false;
    protected PropertyConfiguration configuration;

    public Class<T> type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String column() {
        return column;
    }

    public boolean readOnly() {
        return readOnly;
    }

    public void readOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public PropertyConfiguration getConfiguration() {
        if (configuration == null) {
            configuration = new PropertyConfiguration();
        }
        return configuration;
    }
}
