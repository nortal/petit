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
package com.nortal.petit.converter.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.nortal.persistence.converter.config.fixture.*;
import com.nortal.persistence.converter.config.fixture.types.CustomLongWrapperA;
import com.nortal.persistence.converter.config.fixture.types.CustomLongWrapperB;
import com.nortal.persistence.converter.config.fixture.types.CustomLongWrapperC;
import com.nortal.petit.beanmapper.BeanMapper;
import com.nortal.petit.beanmapper.BeanMapping;
import com.nortal.petit.beanmapper.BeanMappings;
import com.nortal.petit.converter.columnreader.ConversionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * This test verifies that registered {@link com.nortal.petit.converter.config.ReadConverters} are properly
 * used by {@link BeanMapper}
 */
public class ReadConvertersTest {

    @Before
    public void prepare() {
        ConverterConfig.reset();
    }

    @Test
    public void notAddingReadConverters_failsTryingToReadProperty() throws SQLException {
        ResultSet resultSet = createMockingResultSet();
        verifyConversionExceptionIsThrown(ConversionTestBean.class, resultSet);
    }

    @Test
    public void convertersAddedStaticallyByType() {
        ConverterConfig.instance().getReadConverters().add(CustomPropertyTypeOne.createConverter());
        ConverterConfig.instance().getReadConverters().add(CustomPropertyTypeTwo.createConverter());

        verifyFieldsReadFromMockResultSet();
    }

    @Test
    public void someConverterResolvedWithProvider() {
        ConverterConfig.instance().getReadConverters().add(CustomPropertyTypeOne.createConverter());
        ConverterConfig.instance().getReadConverters().setConverterProvider((type) -> {
            if (type.equals(CustomPropertyTypeTwo.class)) {
                return CustomPropertyTypeTwo.createConverter();
            }
            return null;
        });

        verifyFieldsReadFromMockResultSet();
    }

    @Test
    public void allConvertersResolvedWithProvider() {
        ConverterConfig.instance().getReadConverters().setConverterProvider((type) -> {
            if (type.equals(CustomPropertyTypeOne.class)) {
                return CustomPropertyTypeOne.createConverter();
            }
            if (type.equals(CustomPropertyTypeTwo.class)) {
                return CustomPropertyTypeTwo.createConverter();
            }
            return null;
        });

        verifyFieldsReadFromMockResultSet();
    }

    @Test
    public void readConvertersAreComposed() throws SQLException {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        mockResultSetColumn(resultSet, 2, "long_value", "12345");

        // no conversion strategy for column long_value
        verifyConversionExceptionIsThrown(LongWrapperTestBean.class, resultSet);

        ConverterConfig.reset(); // clear off cached providers

        /*
          Converters that should add up to composite converter:
              { String -> `WrapperA` -> 'WrapperB' -> `WrapperC` }
        */
        ConverterConfig.instance().getReadConverters().add(CustomLongWrapperC.createConverterFromWrapperB());
        ConverterConfig.instance().getReadConverters().add(CustomLongWrapperB.createConverterFromWrapperA());
        ConverterConfig.instance().getReadConverters().add(CustomLongWrapperA.createConverterFromString());

        LongWrapperTestBean conversionTestBean = readOneRow(LongWrapperTestBean.class, resultSet);
        Assert.assertEquals(12345, conversionTestBean.getLongValue().getVal().longValue());
    }

    private <T> void verifyConversionExceptionIsThrown(Class<T> beanType, ResultSet resultSet) {
        try {
            readOneRow(beanType, resultSet);
            Assert.fail(); // expecting exception
        } catch (Exception e) {
            Assert.assertEquals(ConversionException.class, e.getCause().getClass());
        }
    }

    private void verifyFieldsReadFromMockResultSet() {
        ResultSet resultSet = createMockingResultSet();
        ConversionTestBean mapperReadBean;
        try {
            mapperReadBean = createMapper().mapRow(resultSet, 1);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        verifyAllFieldsSet(mapperReadBean);
    }

    private void verifyAllFieldsSet(ConversionTestBean mapperReadBean) {
        Assert.assertEquals("string1", mapperReadBean.getTestField());
        Assert.assertEquals(1234567, mapperReadBean.getCustomTypeFieldOne().getVal().longValue());
        Assert.assertTrue(mapperReadBean.getCustomTypeFieldTwo().getChars().containsAll(Arrays.asList('a', 'b', 'c')));
        Assert.assertEquals(3, mapperReadBean.getCustomTypeFieldTwo().getChars().size());
    }

    private ResultSet createMockingResultSet() {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        mockResultSetColumn(resultSet, 1, "db_field", "string1");
        mockResultSetColumn(resultSet, 2, "custom_type_field_one", "1234567");
        mockResultSetColumn(resultSet, 3, "custom_type_field_two", "abc");
        return resultSet;
    }

    private void mockResultSetColumn(ResultSet rs, int colIndex, String colName, Object returnVal) {
        try {
            Mockito.doReturn(colIndex).when(rs).findColumn(Matchers.eq(colName));
            Mockito.doReturn(returnVal).when(rs).getString(Matchers.eq(colIndex));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private <T> T readOneRow(Class<T> beanClass, ResultSet rs) throws SQLException {
        BeanMapping<T> mapping = BeanMappings.get(beanClass);
        BeanMapper<T> mapper = new BeanMapper<>(mapping, ConverterConfig.instance().getPropertyReader());
        return mapper.mapRow(rs, 1);
    }

    private BeanMapper<ConversionTestBean> createMapper() {
        BeanMapping<ConversionTestBean> mapping = BeanMappings.get(ConversionTestBean.class);
        return new BeanMapper<>(mapping, ConverterConfig.instance().getPropertyReader());
    }
}
