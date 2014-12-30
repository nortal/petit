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

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;

import com.nortal.petit.core.util.SqlUtil;

/**
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 */
public class SqlBuilder extends ParameterizedExpressionBuilder<SqlBuilder> {
    private static final long serialVersionUID = 1L;

    public SqlBuilder(String sql) {
        super(sql);
    }

    public SqlBuilder() {
    }

    public SqlBuilder(String sql, Object... params) {
        super(sql, params);
    }

    /**
     * @deprecated use {@link #in(String, Object...)} instead
     */
    @Deprecated
    public <T> SqlBuilder appendInClause(String columnName, T... params) {
        return in(columnName, Arrays.asList(params));
    }

    /**
     * @deprecated use {@link #in(String, Collection)} instead
     */
    @Deprecated
    public <T> SqlBuilder appendInClause(String columnName, Collection<T> params) {
        return in(columnName, params);
    }

    public <T> SqlBuilder in(String columnName, T... params) {
        return in(columnName, Arrays.asList(params));
    }

    public <T> SqlBuilder in(String columnName, Collection<T> params) {
        SqlUtil.setInOperator(params.size(), columnName, sb);
        addParams(params);
        return this;
    }

    /**
     * @deprecated use {@link #notIn(String, Object...)} instead
     */
    @Deprecated
    public <T> SqlBuilder appendNotInClause(String columnName, T... params) {
        return notIn(columnName, Arrays.asList(params));
    }

    /**
     * @deprecated use {@link #notIn(String, Collection)} instead
     */
    @Deprecated
    public <T> SqlBuilder appendNotInClause(String columnName, Collection<T> params) {
        return notIn(columnName, params);
    }

    public <T> SqlBuilder notIn(String columnName, T... params) {
        return notIn(columnName, Arrays.asList(params));
    }

    public <T> SqlBuilder notIn(String columnName, Collection<T> params) {
        SqlUtil.setNotInOperator(params.size(), columnName, sb);
        addParams(params);
        return this;
    }

    /**
     * @deprecated use {@link #and()} instead
     */
    @Deprecated
    public SqlBuilder appendAnd() {
        return and();
    };

    public SqlBuilder and() {
        return append(" AND ");
    }

    /**
     * @deprecated use {@link #and(String, Object...)} instead
     */
    @Deprecated
    public SqlBuilder appendAnd(String str, Object... params) {
        return and(str, params);
    }

    public SqlBuilder and(String str, Object... params) {
        and().append(str).append(" ");
        if (ArrayUtils.isNotEmpty(params)) {
            addParams(params);
        }
        return this;
    }

    public SqlBuilder and(SqlBuilder sql) {
        return and().append(sql);
    }

    /**
     * @deprecated use {@link #or()} instead
     */
    @Deprecated
    public SqlBuilder appendOr() {
        return or();
    }

    public SqlBuilder or() {
        return append(" OR ");
    }

    /**
     * @deprecated use {@link #or(String, Object...)} instead
     */
    @Deprecated
    public SqlBuilder appendOr(String str, Object... params) {
        return or(str, params);
    }

    public SqlBuilder or(String str, Object... params) {
        or().append(str).append(" ");
        if (ArrayUtils.isNotEmpty(params)) {
            addParams(params);
        }
        return this;
    }

    public SqlBuilder or(SqlBuilder sql) {
        return or().append(sql);
    }

    /**
     * @deprecated use {@link #eq(String, Object)} instead
     */
    @Deprecated
    public SqlBuilder appendEq(String str, Object param) {
        return eq(str, param);
    }

    public SqlBuilder eq(String str, Object param) {
        append(" ");
        append(str);
        if (param == null) {
            append(" IS NULL ");
        } else {
            append(" = ? ", param);
        }
        return this;
    }

    public String getSql() {
        return getExpression();
    }

    /**
     * @deprecated use {@link #firstRow()} instead
     */
    @Deprecated
    public SqlBuilder appendFirstRow() {
        return firstRow();
    }

    public SqlBuilder firstRow() {
        return append(" ROWNUM <=1 ");
    }

    /**
     * @deprecated use {@link #where(String)} instead
     */
    @Deprecated
    public SqlBuilder appendWhere(String str) {
        return where(str);
    }

    /**
     * @deprecated use {@link #where(String, Object)} instead
     */
    @Deprecated
    public SqlBuilder appendWhere(String str, Object param) {
        return where(str, param);
    }

    /**
     * @deprecated use {@link #where(SqlBuilder)} instead
     */
    @Deprecated
    public SqlBuilder appendWhere(SqlBuilder sql) {
        return where(sql);
    }

    /**
     * @deprecated use {@link #orderBy(String)} instead
     */
    @Deprecated
    public SqlBuilder appendOrderBy(String str) {
        return orderBy(str);
    }

    public SqlBuilder where(String str) {
        return append(" WHERE ").append(str).append(" ");
    }

    public SqlBuilder where(String str, Object param) {
        return append(" WHERE ").append(str).append(" ").add(param);
    }

    public SqlBuilder where(SqlBuilder sql) {
        return append(" WHERE ").append(sql).append(" ");
    }

    public SqlBuilder orderBy(String str) {
        return append(" ORDER BY ").append(str).append(" ");
    }

    public SqlBuilder append(SqlBuilder sql) {
        append(" ").append(sql.getSql(), sql.getParams()).append(" ");
        return this;
    }

    public SqlBuilder appendIfTrue(boolean expression, SqlBuilder sql) {
        if (expression) {
            append(sql);
        }
        return this;
    }

    public SqlBuilder appendIfNotNull(String str, Object param) {
        if (param != null) {
            append(" ").append(str).append(" ").add(param);
        }
        return this;
    }

    /**
     * Appends like condition: columnName LIKE '%'|| ? ||'%'
     * 
     * @param caseInsensitive
     *            if true then both column and parameter are wrapped with
     *            UPPER().
     * 
     * @deprecated use {@link #like(String, Object, boolean)} instead
     */
    @Deprecated
    public SqlBuilder appendLike(String columnName, Object param, boolean caseInsensitive) {
        return like(columnName, param, caseInsensitive);
    }

    /**
     * Appends like condition: columnName LIKE '%'|| ? ||'%'
     * 
     * @param caseInsensitive
     *            if true then both column and parameter are wrapped with
     *            UPPER().
     */
    public SqlBuilder like(String columnName, Object param, boolean caseInsensitive) {
        append(" ");
        if (caseInsensitive) {
            append("UPPER(").append(columnName).append(")");
        } else {
            append(columnName);
        }
        append(" LIKE '%' || ");
        if (caseInsensitive) {
            append("UPPER(?)");
        } else {
            append("?");
        }
        append(" || '%' ");

        addParams(param);

        return this;
    }

    @Override
    public String toString() {
        return "SQL: [\n  " + sb.toString() + "\n], args: {\n  " + params + "\n}";
    }

    /**
     * Appends a series of clauses separated by 'OR'-s.
     */
    public SqlBuilder matchingAny(Iterable<SqlBuilder> clauses) {
        sb.append("(0=1");
        for (SqlBuilder t : clauses) {
            or(t);
        }
        sb.append(')');
        return this;
    }

}
