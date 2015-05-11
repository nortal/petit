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

import java.util.Map;

/**
 * A simple implementation of a BeanMapping.
 * 
 * @author Aleksei Lissitsin
 * 
 */
public class SimpleBeanMapping<B> implements BeanMapping<B> {
    protected Class<B> type;
    protected String table;
    protected Map<String, Property<B, Object>> props;
    protected Property<B, Object> id;

    public SimpleBeanMapping(Class<B> type, String table, Map<String, Property<B, Object>> props) {
        this.type = type;
        this.table = table;
        this.props = props;
    }

    public B instance() {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String table() {
        return table;
    }

    public Map<String, Property<B, Object>> props() {
        return props;
    }

    public Property<B, Object> id() {
        return id;
    }

    public void id(Property<B, Object> id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return type.getSimpleName();
    }

    @Override
    public Class<B> type() {
        return type;
    }
}
