/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazon.randomcutforest;

import java.util.function.BiFunction;

import com.amazon.randomcutforest.tree.ITree;

/**
 * This is the interface for a visitor factory the factory corresponds to
 * mapping a (tree,point) pair to a visitor and a mapping for the inverse result
 */
public class VisitorFactory<R> {
    public BiFunction<ITree<?, ?>, double[], Visitor<R>> create;
    public BiFunction<ITree<?, ?>, R, R> liftResult;

    public VisitorFactory(BiFunction<ITree<?, ?>, double[], Visitor<R>> create,
            BiFunction<ITree<?, ?>, R, R> liftResult) {
        this.create = create;
        this.liftResult = liftResult;
    }

    public VisitorFactory(BiFunction<ITree<?, ?>, double[], Visitor<R>> create) {
        this.create = create;
        this.liftResult = (tree, x) -> x;
    }
}
