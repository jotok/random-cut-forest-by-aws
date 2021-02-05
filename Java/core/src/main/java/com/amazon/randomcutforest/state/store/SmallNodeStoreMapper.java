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
import com.amazon.randomcutforest.store.SmallNodeStore;

@Getter
@Setter
public class SmallNodeStoreMapper implements IStateMapper<SmallNodeStore, NodeStoreState> {
    /**
     * The mode used when mapping between model and state objects. The mapper
     * guarantees that a state object created in a given mode can be converted to a
     * model by a mapper class in the same mode. Currently supported modes are
     * {@code COPY} and {@code REFERENCE}, and the default mode is {@code COPY}.
     */
    private Mode mode = Mode.COPY;

    @Override
    public SmallNodeStore toModel(NodeStoreState state, long seed) {
        int capacity = state.getSmallLeftIndex().length;
        short freeIndexPointer = (short) state.getFreeIndexPointer();

        short[] leftIndex;
        short[] rightIndex;
        short[] parentIndex;
        short[] mass;
        int[] cutDimension;
        double[] cutValue;
        short[] freeIndexes;

        if (mode == Mode.COPY) {
            leftIndex = new short[capacity];
            rightIndex = new short[capacity];
            parentIndex = new short[capacity];
            mass = new short[capacity];
            cutDimension = new int[capacity];
            cutValue = new double[capacity];
            freeIndexes = new short[capacity];

            System.arraycopy(state.getSmallLeftIndex(), 0, leftIndex, 0, state.getSmallLeftIndex().length);
            System.arraycopy(state.getSmallRightIndex(), 0, rightIndex, 0, state.getSmallRightIndex().length);
            System.arraycopy(state.getSmallParentIndex(), 0, parentIndex, 0, state.getSmallParentIndex().length);
            System.arraycopy(state.getSmallMass(), 0, mass, 0, state.getSmallMass().length);
            System.arraycopy(state.getCutDimension(), 0, cutDimension, 0, state.getCutDimension().length);
            System.arraycopy(state.getCutValue(), 0, cutValue, 0, state.getCutDimension().length);
            System.arraycopy(state.getSmallFreeIndexes(), 0, freeIndexes, 0, freeIndexPointer + 1);
        } else {
            leftIndex = state.getSmallLeftIndex();
            rightIndex = state.getSmallRightIndex();
            parentIndex = state.getSmallParentIndex();
            mass = state.getSmallMass();
            cutDimension = state.getCutDimension();
            cutValue = state.getCutValue();
            freeIndexes = state.getSmallFreeIndexes();
        }

        return new SmallNodeStore(parentIndex, leftIndex, rightIndex, cutDimension, cutValue, mass, freeIndexes,
                freeIndexPointer);
    }

    @Override
    public NodeStoreState toState(SmallNodeStore model) {
        NodeStoreState state = new NodeStoreState();
        state.setCapacity(model.getCapacity());
        state.setFreeIndexPointer(model.getFreeIndexPointer());

        if (mode == Mode.COPY) {
            state.setSmallLeftIndex(Arrays.copyOf(model.leftIndex, model.leftIndex.length));
            state.setSmallRightIndex(Arrays.copyOf(model.rightIndex, model.rightIndex.length));
            state.setSmallParentIndex(Arrays.copyOf(model.parentIndex, model.parentIndex.length));
            state.setSmallMass(Arrays.copyOf(model.mass, model.mass.length));
            state.setCutDimension(Arrays.copyOf(model.cutDimension, model.cutDimension.length));
            state.setCutValue(Arrays.copyOf(model.cutValue, model.cutValue.length));
            state.setSmallFreeIndexes(Arrays.copyOf(model.getFreeIndexes(), model.getFreeIndexPointer() + 1));
        } else {
            state.setSmallLeftIndex(model.leftIndex);
            state.setSmallRightIndex(model.rightIndex);
            state.setSmallParentIndex(model.parentIndex);
            state.setSmallMass(model.mass);
            state.setCutDimension(model.cutDimension);
            state.setCutValue(model.cutValue);
            state.setSmallFreeIndexes(model.getFreeIndexes());
        }

        return state;
    }
}
