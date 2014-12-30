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
package com.nortal.petit.orm.statement;

import java.util.List;

import org.springframework.util.CollectionUtils;

/**
 * @author Lauri Lättemäe (lauri.lattemae@nortal.com)
 * @created 29.04.2013
 */
public abstract class ExecutableStatement<B> extends SimpleStatement<B> {

    /**
     * Returns statements sql with parameter values
     * 
     * @return
     */
    @Override
    public String getSqlWithParams() {
        prepare();

        StringBuffer sb = new StringBuffer();
        if (!CollectionUtils.isEmpty(getBeans())) {
            for (B bean : getBeans()) {
                prepare(bean);
                sb.append(super.getSqlWithParams()).append("\n");
            }
        } else {
            sb.append(super.getSqlWithParams()).append("\n");
        }
        return sb.toString();
    }

    protected abstract List<B> getBeans();

    protected abstract void prepare(B bean);

    public abstract void exec();
}
