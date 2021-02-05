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

package com.amazon.randomcutforest.state.store;

import lombok.Data;

@Data
public class NodeStoreState {
    private int capacity;
    private int freeIndexPointer;
    private int[] cutDimension;
    private double[] cutValue;

    /*
     * the following are for SmallNodeStore
     */
    private short[] smallParentIndex;
    private short[] smallLeftIndex;
    private short[] smallRightIndex;
    private short[] smallMass;
    private short[] smallFreeIndexes;

    /*
     * the following are for NodeStore
     */
    private int[] parentIndex;
    private int[] leftIndex;
    private int[] rightIndex;
    private int[] mass;
    private int[] freeIndexes;
}
