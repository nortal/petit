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
package com.nortal.petit.orm.statement.interceptor;

/**
 * Interceptor that is can be added to StatementBuilder and is called when entities are modified.
 */
public interface StatementInterceptor {

    /**
     * Called after update statement has executed
     * @param table table name that the entity is stored in
     * @param id primary key value of the entity
     * @param currentState values of the the properties after update
     * @param previuosState values of the the properties before update
     * @param columnNames names of the the database columns
     */
    void afterUpdate(String table, Object id, Object[] currentState, Object[] previuosState, String[] columnNames);

    /**
     * Called after insert statement has executed
     * @param table table name that the entity is stored in
     * @param id primary key value of the entity. This can be the value generated in database
     * @param currentState values of the properties inserted
     * @param columnNames names of the the database columns
     */
    void afterInsert(String table, Object id, Object[] currentState, String[] columnNames);
}
