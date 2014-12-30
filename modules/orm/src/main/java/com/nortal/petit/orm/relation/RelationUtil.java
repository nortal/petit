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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

/**
 * @author Aleksei Lissitsin
 * 
 */
public class RelationUtil {
    public static <T, R> void loadRelations(Collection<T> targets, final RelationInfo<T, R> info,
            final RelationLoader<R> loader) {
        // Filter out objects where targetId is NULL
        ImmutableList<T> actualTargets = FluentIterable.<T> from(targets).filter(new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                return info.targetId().apply(input) != null;
            }
        }).toList();
        if (actualTargets == null || actualTargets.isEmpty()) {
            return;
        }
        ImmutableListMultimap<Object, T> targetIndex = Multimaps.index(actualTargets, info.targetId());

        List<R> relations = loader.loadRelations(targetIndex.keySet());

        ImmutableListMultimap<Object, R> relIndex = Multimaps.index(relations, info.relationId());

        for (Map.Entry<Object, List<R>> e : Multimaps.asMap(relIndex).entrySet()) {
            for (T target : targetIndex.get(e.getKey())) {
                info.associate(target, e.getValue());
            }
        }
    }
}
