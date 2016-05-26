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
package com.nortal.petit.converter;

import java.lang.reflect.Type;

public class CompositeConverter<F, M, T> implements Converter<F, T> {
    private final Converter<F, M> c1;
    private final Converter<M, T> c2;

    public CompositeConverter(Converter<F, M> c1, Converter<M, T> c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

	@Override
    public T convert(F value) {
        return c2.convert(c1.convert(value));
    }

    @Override
    public Type getFromType() {
        return c1.getFromType();
    }

    @Override
    public Type getToType() {
        return c2.getToType();
    }
}
