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

import static com.amazon.randomcutforest.CommonUtils.checkNotNull;

import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

import com.amazon.randomcutforest.state.IStateMapper;
import com.amazon.randomcutforest.state.Mode;
import com.amazon.randomcutforest.store.PointStoreFloat;

@Getter
@Setter
public class PointStoreFloatMapper implements IStateMapper<PointStoreFloat, PointStoreState> {
    /**
     * The mode used when mapping between model and state objects. The mapper
     * guarantees that a state object created in a given mode can be converted to a
     * model by a mapper class in the same mode. Currently supported modes are
     * {@code COPY} and {@code REFERENCE}, and the default mode is {@code COPY}.
     */
    private Mode mode = Mode.COPY;

    @Override
    public PointStoreFloat toModel(PointStoreState state, long seed) {
        checkNotNull(state.getRefCount(), "refCount must not be null");
        checkNotNull(state.getFloatData(), "floatdata must not be null");

        int capacity = state.getCapacity();
        int freeIndexPointer = state.getFreeIndexPointer();

        short[] refCount;
        float[] store;
        int[] freeIndexes;

        if (mode == Mode.COPY) {
            refCount = new short[capacity];
            store = new float[capacity * state.getDimensions()];
            freeIndexes = new int[capacity];

            System.arraycopy(state.getRefCount(), 0, refCount, 0, state.getRefCount().length);
            System.arraycopy(state.getFloatData(), 0, store, 0, state.getFloatData().length);
            System.arraycopy(state.getFreeIndexes(), 0, freeIndexes, 0, state.getFreeIndexes().length);
        } else {
            refCount = state.getRefCount();
            store = state.getFloatData();
            freeIndexes = state.getFreeIndexes();
        }

        return new PointStoreFloat(store, refCount, freeIndexes, freeIndexPointer);
    }

    @Override
    public PointStoreState toState(PointStoreFloat model) {
        PointStoreState state = new PointStoreState();
        state.setCapacity(model.getCapacity());
        state.setDimensions(model.getDimensions());
        state.setFreeIndexPointer(model.getFreeIndexPointer());

        if (mode == Mode.COPY) {
            state.setFloatData(Arrays.copyOf(model.getStore(), model.getStore().length));
            state.setRefCount(Arrays.copyOf(model.getRefCount(), model.getRefCount().length));
            state.setFreeIndexes(Arrays.copyOf(model.getFreeIndexes(), model.getFreeIndexPointer() + 1));
        } else {
            state.setFloatData(model.getStore());
            state.setRefCount(model.getRefCount());
            state.setFreeIndexes(model.getFreeIndexes());
        }

        return state;
    }
}
