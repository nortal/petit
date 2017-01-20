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
package com.nortal.petit.converter.columnreader;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FailingColumnReader implements ColumnReader<Object> {

  @Override
  public Object getColumnValue(ResultSet rs, int index) throws SQLException {
    throw ex("index " + index);
  }

  @Override
  public Object getColumnValue(ResultSet rs, String column) throws SQLException {
    throw ex("column " + column);
  }
  
  private ConversionException ex(String subject) {
    return new ConversionException("Could not find conversion strategy for " + subject);
  }
}
