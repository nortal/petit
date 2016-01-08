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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SqlUtil.class);

    /**
     * <pre>
     * Takes parameters of query and creates IN operator block.
     * Note:
     * 1. do nothing, if quantity is 0.
     * 2. uses '=' (not 'IN' operator), if quantity is 1.
     * 3. gives warn in logs, if more than 30 parameters are given.
     * </pre>
     * <p/>
     * Separate blocks are created if quantity exceeds 1000
     * 
     * @param parametersQuantity
     *            - quantity of parameters in IN operator
     * @param columnName
     *            - name of column on which search (with using IN operator) is
     *            performed
     * @param result
     *            - <code>StringBuilder</code> object to which result is
     *            appended
     * @author Anton Stalnuhhin (antons@webmedia.ee)
     */
    public static void setInOperator(int parametersQuantity, String columnName, StringBuilder result) {
        setInOperatorHelper(parametersQuantity, columnName, result, false);
    }

    /**
     * <pre>
     * Takes parameters of query and creates NOT IN operator block.
     * Note:
     * 1. do nothing, if quantity is 0.
     * 2. uses '!=' (not 'NOT IN' operator), if quantity is 1.
     * 3. gives warn in logs, if more than 30 parameters are given.
     * </pre>
     * <p/>
     * Separate blocks are created if quantity exceeds 1000
     * 
     * @param parametersQuantity
     *            - quantity of parameters in NOT IN operator
     * @param columnName
     *            - name of column on which search (with using NOT IN operator)
     *            is performed
     * @param result
     *            - <code>StringBuilder</code> object to which result is
     *            appended
     * @author Anton Stalnuhhin (antons@webmedia.ee)
     */
    public static void setNotInOperator(int parametersQuantity, String columnName, StringBuilder result) {
        setInOperatorHelper(parametersQuantity, columnName, result, true);
    }

    /**
     * @author Anton Stalnuhhin (antons@webmedia.ee)
     * @see #setInOperator(int, String, StringBuilder)
     * @see #setNotInOperator(int, String, StringBuilder)
     */
    private static void setInOperatorHelper(int parametersQuantity, String columnName, StringBuilder result,
            boolean isNotIn) {
        if (parametersQuantity < 0) {
            throw new IllegalArgumentException("Quantity must be not negative.");
        }
        if (parametersQuantity == 0) {
            return;
        } else if (parametersQuantity == 1) {
            setInOperatorIfOneParameter(columnName, result, isNotIn);
            return;
        } else if (parametersQuantity > 30) {
            LOG.warn("Review logic of the calling #setInOperator() method! " + parametersQuantity + " parameters used.");
        }

        int maxExpressions = 1000;
        int fullBlocksCount = parametersQuantity / maxExpressions;
        int remainderBlockSize = parametersQuantity % maxExpressions;

        String operator, logicalOp;

        if (isNotIn) {
            operator = "NOT IN";
            logicalOp = "AND";
        } else {
            operator = "IN";
            logicalOp = "OR";
        }

        StringBuilder fullBlocks = addFullBlocks(columnName, maxExpressions, fullBlocksCount, operator, logicalOp);
        addRemainderBlock(columnName, result, remainderBlockSize, operator, logicalOp, fullBlocks);

        result.append(" ) ");
    }

    private static StringBuilder addFullBlocks(String columnName, int maxExpressions, int fullBlocksCount,
            String operator, String logicalOp) {
        StringBuilder fullBlocks = new StringBuilder();
        if (fullBlocksCount > 0) {
            for (int i = 0; i < fullBlocksCount; i++) {
                fullBlocks.append(columnName).append(' ').append(operator).append(" (?");
                fullBlocks.append(StringUtils.repeat(",?", maxExpressions - 1)).append(')');
                if (i < (fullBlocksCount - 1)) {
                    fullBlocks.append(' ').append(logicalOp).append(' ');
                }
            }
        }
        return fullBlocks;
    }

    private static void addRemainderBlock(String columnName, StringBuilder result, int remainderBlockSize,
            String operator, String logicalOp, StringBuilder fullBlocks) {
        StringBuilder remainderBlock = new StringBuilder();
        if (remainderBlockSize > 0) {
            remainderBlock.append(columnName).append(' ').append(operator).append(" (?");
            remainderBlock.append(StringUtils.repeat(",?", remainderBlockSize - 1)).append(')');
        }

        result.append(" ( ");
        result.append(fullBlocks);
        if (fullBlocks.length() > 0 && remainderBlock.length() > 0) {
            result.append(' ').append(logicalOp).append(' ');
        }
        result.append(remainderBlock);
    }

    private static void setInOperatorIfOneParameter(String columnName, StringBuilder result, boolean isNotIn) {
        result.append(' ').append(columnName).append(isNotIn ? " <> " : " = ").append("? ");
    }

    public static Long getLong(ResultSet rs, String columnLabel) throws SQLException {
        return getLong(rs.getObject(columnLabel));
    }

    public static Long getLong(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof BigDecimal) {
            return Long.valueOf(((BigDecimal) object).longValue());
        } else if (object instanceof Long) {
            return (Long) object;
        } else if (object instanceof String) {
            return Long.valueOf((String) object);
        }
        throw new IllegalArgumentException("You can give only String, BigDecimal and Long types!");
    }

}
