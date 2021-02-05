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
import com.amazon.randomcutforest.store.SmallLeafStore;

@Getter
@Setter
public class SmallLeafStoreMapper implements IStateMapper<SmallLeafStore, LeafStoreState> {
    /**
     * The mode used when mapping between model and state objects. The mapper
     * guarantees that a state object created in a given mode can be converted to a
     * model by a mapper class in the same mode. Currently supported modes are
     * {@code COPY} and {@code REFERENCE}, and the default mode is {@code COPY}.
     */
    private Mode mode = Mode.COPY;

    @Override
    public SmallLeafStore toModel(LeafStoreState state, long seed) {
        int capacity = state.getPointIndex().length;
        short freeIndexPointer = (short) state.getFreeIndexPointer();

        int[] pointIndex;
        short[] parentIndex;
        short[] mass;
        short[] freeIndexes;

        if (mode == Mode.COPY) {
            pointIndex = new int[capacity];
            parentIndex = new short[capacity];
            mass = new short[capacity];
            freeIndexes = new short[capacity];

            System.arraycopy(state.getPointIndex(), 0, pointIndex, 0, state.getPointIndex().length);
            System.arraycopy(state.getSmallParentIndex(), 0, parentIndex, 0, state.getSmallParentIndex().length);
            System.arraycopy(state.getSmallMass(), 0, mass, 0, state.getSmallMass().length);
            System.arraycopy(state.getSmallFreeIndexes(), 0, freeIndexes, 0, freeIndexPointer + 1);
        } else {
            pointIndex = state.getPointIndex();
            parentIndex = state.getSmallParentIndex();
            mass = state.getSmallMass();
            freeIndexes = state.getSmallFreeIndexes();
        }

        return new SmallLeafStore(pointIndex, parentIndex, mass, freeIndexes, freeIndexPointer);
    }

    @Override
    public LeafStoreState toState(SmallLeafStore model) {
        LeafStoreState state = new LeafStoreState();
        state.setCapacity(model.getCapacity());
        state.setFreeIndexPointer(model.getFreeIndexPointer());

        if (mode == Mode.COPY) {
            state.setPointIndex(Arrays.copyOf(model.pointIndex, model.pointIndex.length));
            state.setSmallParentIndex(Arrays.copyOf(model.parentIndex, model.parentIndex.length));
            state.setSmallMass(Arrays.copyOf(model.mass, model.mass.length));
            state.setSmallFreeIndexes(Arrays.copyOf(model.getFreeIndexes(), model.getFreeIndexPointer() + 1));
        } else {
            state.setPointIndex(model.pointIndex);
            state.setSmallParentIndex(model.parentIndex);
            state.setSmallMass(model.mass);
            state.setSmallFreeIndexes(model.getFreeIndexes());
        }

        return state;
    }

}
