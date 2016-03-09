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
package com.nortal.petit.core.util;

import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.StatementCreatorUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public abstract class ArgPreparedStatementSetter {
    public static int setValues(PreparedStatement ps, Object[] args, int startIndex) throws SQLException {
        int j = startIndex;
        if (args != null) {
            for (int i = 0; i < args.length; i++, j++) {
                Object arg = args[i];
                if (arg instanceof SqlParameterValue) {
                    SqlParameterValue paramValue = (SqlParameterValue) arg;
                    StatementCreatorUtils.setParameterValue(ps, j, paramValue, paramValue.getValue());
                } else {
                    StatementCreatorUtils.setParameterValue(ps, j, Types.OTHER, arg);
                }
            }
        }
        return j;
    }

    public static int setValues(PreparedStatement ps, Object... args) throws SQLException {
        return setValues(ps, args, 1);
    }
}
