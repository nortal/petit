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

import com.nortal.persistence.converter.config.fixture.types.CustomLongWrapperA;
import com.nortal.persistence.converter.config.fixture.types.CustomLongWrapperB;
import com.nortal.petit.converter.Converter;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class WriteConvertersTest {
    @Test
    public void convertersAddedStaticallyByType() {
        /* given: no extra converters are registered */
        Converters writeConverters = ConverterConfig.instance().getWriteConverters();
        Assert.assertNull(writeConverters.get(String.class));
        Assert.assertNull(writeConverters.get(CustomLongWrapperA.class));
        Assert.assertNull(writeConverters.get(CustomLongWrapperB.class));

        /* when */
        // adding { `WrapperA` -> `String` } write converter
        writeConverters.add(CustomLongWrapperA.createConverterToString());
        // .. and { `WrapperB` -> `WrapperA` } write converter
        writeConverters.add(CustomLongWrapperB.createConverterToWrapperA());

        /* then */
        // write converter should be registered by "from" type (and not by `String`)
        Assert.assertNull(writeConverters.get(String.class));
        Assert.assertNotNull(writeConverters.get(CustomLongWrapperA.class));
        Converter<?, ?> fromWrapperB = writeConverters.get(CustomLongWrapperB.class);
        Assert.assertNotNull(fromWrapperB);
        // .. while also composing matching converters, resulting in { `WrapperB` -> ... -> `String` }
        Assert.assertEquals(String.class, fromWrapperB.getToType());
    }
}
