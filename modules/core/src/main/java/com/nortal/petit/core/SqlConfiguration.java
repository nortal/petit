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
package com.nortal.petit.core;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.nortal.petit.core.dialect.SqlDialect;

/**
 * Base interface for using Petit SQLSupport.
 * See {@link BaseDAO} for an example implementation.
 * 
 * @author Alrik Peets
 * 
 */
public interface SqlConfiguration {
    /**
     * @return
     */
    JdbcTemplate getJdbcTemplate();

    /**
     * Returns {@link JdbcTemplate} as an interface.
     * 
     * @return
     */
    JdbcOperations getJdbcOperations();

    /**
     * Returns SQL Dialect specific implementation.
     * 
     * @return
     */
    SqlDialect getSqlDialect();
}
