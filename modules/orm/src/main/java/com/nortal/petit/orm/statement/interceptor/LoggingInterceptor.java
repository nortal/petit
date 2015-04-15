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

import org.apache.log4j.Logger;


public class LoggingInterceptor implements StatementInterceptor {
    protected final static Logger LOG = Logger.getLogger(LoggingInterceptor.class);

    @Override
    public void afterUpdate(String table, Object id, Object[] currentState, Object[] previuosState, String[] columnNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE  table = " + table + " id = " + id );
        for (int i = 0; i < currentState.length; i++) {
            if (previuosState != null) {
                sb.append("\n      " + columnNames[i] + " : " + previuosState[i] + " -> " + currentState[i]);
            } else {
                sb.append("\n      " + columnNames[i] + " : " + currentState[i]);
            }

        }
        LOG.debug(sb.toString());
    }

    @Override
    public void afterInsert(String table, Object id, Object[] currentState, String[] columnNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT  table = " + table + " id = " + id );
        for (int i = 0; i < currentState.length; i++) {
            sb.append("\n      " + columnNames[i] + " : "  + currentState[i]);
        }
        LOG.debug(sb.toString());
    }

}
