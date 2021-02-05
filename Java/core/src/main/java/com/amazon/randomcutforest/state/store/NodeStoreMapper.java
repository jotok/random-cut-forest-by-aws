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

import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

import com.amazon.randomcutforest.state.IStateMapper;
import com.amazon.randomcutforest.state.Mode;
import com.amazon.randomcutforest.store.NodeStore;

@Getter
@Setter
public class NodeStoreMapper implements IStateMapper<NodeStore, NodeStoreState> {
    /**
     * The mode used when mapping between model and state objects. The mapper
     * guarantees that a state object created in a given mode can be converted to a
     * model by a mapper class in the same mode. Currently supported modes are
     * {@code COPY} and {@code REFERENCE}, and the default mode is {@code COPY}.
     */
    private Mode mode = Mode.COPY;

    @Override
    public NodeStore toModel(NodeStoreState state, long seed) {
        int capacity = state.getLeftIndex().length;
        int freeIndexPointer = state.getFreeIndexPointer();

        int[] leftIndex;
        int[] rightIndex;
        int[] parentIndex;
        int[] mass;
        int[] cutDimension;
        double[] cutValue;
        int[] freeIndexes;

        if (mode == Mode.COPY) {
            leftIndex = new int[capacity];
            rightIndex = new int[capacity];
            parentIndex = new int[capacity];
            mass = new int[capacity];
            cutDimension = new int[capacity];
            cutValue = new double[capacity];
            freeIndexes = new int[capacity];

            System.arraycopy(state.getLeftIndex(), 0, leftIndex, 0, state.getLeftIndex().length);
            System.arraycopy(state.getRightIndex(), 0, rightIndex, 0, state.getRightIndex().length);
            System.arraycopy(state.getParentIndex(), 0, parentIndex, 0, state.getParentIndex().length);
            System.arraycopy(state.getMass(), 0, mass, 0, state.getMass().length);
            System.arraycopy(state.getCutDimension(), 0, cutDimension, 0, state.getCutDimension().length);
            System.arraycopy(state.getCutValue(), 0, cutValue, 0, state.getCutDimension().length);
            System.arraycopy(state.getFreeIndexes(), 0, freeIndexes, 0, freeIndexPointer + 1);
        } else {
            leftIndex = state.getLeftIndex();
            rightIndex = state.getRightIndex();
            parentIndex = state.getParentIndex();
            mass = state.getMass();
            cutDimension = state.getCutDimension();
            cutValue = state.getCutValue();
            freeIndexes = state.getFreeIndexes();
        }

        return new NodeStore(parentIndex, leftIndex, rightIndex, cutDimension, cutValue, mass, freeIndexes,
                freeIndexPointer);
    }

    @Override
    public NodeStoreState toState(NodeStore model) {
        NodeStoreState state = new NodeStoreState();
        state.setCapacity(model.getCapacity());
        state.setFreeIndexPointer(model.getFreeIndexPointer());

        if (mode == Mode.COPY) {
            state.setLeftIndex(Arrays.copyOf(model.leftIndex, model.leftIndex.length));
            state.setRightIndex(Arrays.copyOf(model.rightIndex, model.rightIndex.length));
            state.setParentIndex(Arrays.copyOf(model.parentIndex, model.parentIndex.length));
            state.setMass(Arrays.copyOf(model.mass, model.mass.length));
            state.setCutDimension(Arrays.copyOf(model.cutDimension, model.cutDimension.length));
            state.setCutValue(Arrays.copyOf(model.cutValue, model.cutValue.length));
            state.setFreeIndexes(Arrays.copyOf(model.getFreeIndexes(), model.getFreeIndexPointer() + 1));
        } else {
            state.setLeftIndex(model.leftIndex);
            state.setRightIndex(model.rightIndex);
            state.setParentIndex(model.parentIndex);
            state.setMass(model.mass);
            state.setCutDimension(model.cutDimension);
            state.setCutValue(model.cutValue);
            state.setFreeIndexes(model.getFreeIndexes());
        }

        return state;
    }
}
