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
package com.nortal.persistence.converter.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.nortal.petit.converter.columnreader.ColumnReader;
import com.nortal.petit.converter.columnreader.ConversionException;
import com.nortal.petit.converter.config.ConverterConfig;

public class CatchAllReaderTest {

  private final class NullingCatchAllReader implements ColumnReader<Object> {
    @Override
    public Object getColumnValue(ResultSet rs, int index) throws SQLException {
      return null;
    }

    @Override
    public Object getColumnValue(ResultSet rs, String column) throws SQLException {
      return null;
    }
  }

  @Test
  public void testNullingCatchAllReader() throws SQLException {
    ConverterConfig.instance().setCatchAllReader(new NullingCatchAllReader());
    Assert.assertNull(getValue());
  }

  @Test(expected = ConversionException.class)
  public void testNoConverterFound() throws SQLException {
    getValue();
  }

  private Object getValue() throws SQLException {
    return ConverterConfig.instance().getResultSetReader().get(CatchAllReaderTest.class, null, "test_column");
  }
}
