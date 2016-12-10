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
package com.nortal.petit.orm.statement.clause;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class SqlPropertyParam {
    private String prop;

    public SqlPropertyParam(String prop) {
        this.prop = prop;
    }

    public static List<Object> resolvePropParams(List<Object> params, Function<String, Object> paramMapper) {
        if (paramMapper == null) {
            return params;
        }

        List<Object> resolvedParams = new ArrayList<Object>(params.size());

        for (Object v : params) {
            if (v instanceof SqlPropertyParam) {
                resolvedParams.add(paramMapper.apply(((SqlPropertyParam) v).prop));
            } else {
                resolvedParams.add(v);
            }
        }

        return resolvedParams;
    }
}
