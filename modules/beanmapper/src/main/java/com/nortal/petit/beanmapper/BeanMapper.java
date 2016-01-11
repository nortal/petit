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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A RowMapper relying on an underlying BeanMapping and a provided
 * ResultSetReader.
 * 
 * @author Aleksei Lissitsin
 * 
 */
public class BeanMapper<B> {

    private BeanMapping<B> mapping;

    private ResultSetReader resultSetReader;

    public BeanMapper(BeanMapping<B> mapping, ResultSetReader resultSetReader) {
        this.mapping = mapping;
        this.resultSetReader = resultSetReader;
    }

    public BeanMapping<B> mapping() {
        return mapping;
    }

    public B mapRow(ResultSet rs, int rowNum) throws SQLException {
        B o = mapping().instance();

        for (Property<B, Object> p : mapping().props().values()) {
            mapProperty(rs, o, p);
        }

        return o;
    }

    private void mapProperty(ResultSet rs, B o, Property<B, Object> p) throws SQLException {
        try {
            p.write(o, resultSetReader.get(p.type(), rs, p.column()));
        } catch (RuntimeException e) {
            throw new RuntimeException(getErrorMsg(p, o), e);
        } catch (SQLException e) {
            throw new SQLException(getErrorMsg(p, o), e.getSQLState(), e.getErrorCode());
        }
    }

    private String getErrorMsg(Property<B, Object> p, B o) {
        return "Error setting value for field " + p.name() + " of " + o.getClass();
    }
}
