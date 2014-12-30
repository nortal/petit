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
package com.nortal.petit.core.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Roman Tekhov
 */
public class ParameterizedExpressionBuilder<T extends ParameterizedExpressionBuilder<T>> implements Serializable {
    private static final long serialVersionUID = 1L;

    protected StringBuilder sb;
    protected List<Object> params;

    public ParameterizedExpressionBuilder(String sql) {
        sb = new StringBuilder(sql);
        this.params = new ArrayList<Object>();
    }

    public ParameterizedExpressionBuilder() {
        sb = new StringBuilder();
        this.params = new ArrayList<Object>();
    }

    public ParameterizedExpressionBuilder(String sql, Object... params) {
        sb = new StringBuilder(sql);
        this.params = new ArrayList<Object>(Arrays.asList(params));
    }

    public T add(Object o) {
        params.add(o);
        return getThis();
    }

    public T addParams(Collection<?> c) {
        params.addAll(c);
        return getThis();
    }

    public T addParams(Object... params) {
        this.params.addAll(Arrays.asList(params));
        return getThis();
    }

    public T append(String str) {
        sb.append(str);
        return getThis();
    }

    @SuppressWarnings("unchecked")
    private T getThis() {
        return (T) this;
    }

    public T append(String sql, Object... params) {
        append(sql);
        return addParams(params);
    }

    @SuppressWarnings("unchecked")
    public T appendIfTrue(boolean condition, String sql, Object... params) {
        return condition ? append(sql, params) : (T) this;
    }

    public String getExpression() {
        return sb.toString();
    }

    public Object[] getParams() {
        return params.toArray();
    }

    public List<Object> getParamsAsList() {
        return params;
    }

    public StringBuilder getStringBuilder() {
        return sb;
    }

    public boolean isEmpty() {
        return sb.length() == 0;
    }
}
