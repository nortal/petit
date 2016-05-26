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
package com.nortal.petit.converter.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ColumnPosition {
    public final boolean isNamed;
    private String name;
    private int index; // 1-based

    public ColumnPosition(String columnName) {
        isNamed = true;
        this.name = columnName;
    }

    public ColumnPosition(int index) {
        isNamed = false;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
    
    public int getIndex(ResultSet rs) throws SQLException {
        return index == 0 ? rs.findColumn(name) : index;
    }
}