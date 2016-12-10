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
package com.nortal.petit.orm.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class RelationUtil {
    public static <T, R> void loadRelations(Collection<T> targets, final RelationInfo<T, R> info,
            final RelationLoader<R> loader) {
        Collection<T> actualTargets = filterActualTargets(targets, info);
        if (actualTargets == null || actualTargets.isEmpty()) {
            return;
        }
        Map<Object, List<T>> targetIndex = createIndexMap(actualTargets, info.targetId());

        List<R> relations = loader.loadRelations(targetIndex.keySet());

        Map<Object, List<R>> relIndex = createIndexMap(relations, info.relationId());

        for (Map.Entry<Object, List<R>> e : relIndex.entrySet()) {
            for (T target : targetIndex.get(e.getKey())) {
                info.associate(target, e.getValue());
            }
        }
    }

    private static <T, R> Collection<T> filterActualTargets(Collection<T> targets, RelationInfo<T, R> info) {
        Collection<T> result = new ArrayList<>();

        for (T target : targets) {
            if (info.targetId().apply(target) != null) {
                result.add(target);
            }
        }

        return result;
    }

    private static <T> Map<Object, List<T>> createIndexMap(Collection<T> targets, Function<T, Object> indexFunc) {
        Map<Object, List<T>> indexMap = new HashMap<>();

        for (T target : targets) {
            List<T> indexList = indexMap.get(indexFunc.apply(target));
            if (indexList == null) {
                indexList = new ArrayList<T>();
                indexMap.put(indexFunc.apply(target), indexList);
            }
            indexList.add(target);
        }

        return indexMap;
    }
}
