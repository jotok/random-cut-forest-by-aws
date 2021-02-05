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

package com.amazon.randomcutforest.state.sampler;

import java.util.Arrays;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

import com.amazon.randomcutforest.sampler.CompactSampler;
import com.amazon.randomcutforest.state.IStateMapper;
import com.amazon.randomcutforest.state.Mode;

@Getter
@Setter
public class CompactSamplerMapper implements IStateMapper<CompactSampler, CompactSamplerState> {

    /**
     * The mode used when mapping between model and state objects. The mapper
     * guarantees that a state object created in a given mode can be converted to a
     * model by a mapper class in the same mode. Currently supported modes are
     * {@code COPY} and {@code REFERENCE}, and the default mode is {@code COPY}.
     */
    private Mode mode = Mode.COPY;

    private boolean validateHeap = false;

    @Override
    public CompactSampler toModel(CompactSamplerState state, long seed) {
        float[] weight;
        int[] pointIndex;
        long[] sequenceIndex;

        if (mode == Mode.COPY) {
            weight = new float[state.getCapacity()];
            pointIndex = new int[state.getCapacity()];

            int size = state.getSize();
            System.arraycopy(state.getWeight(), 0, weight, 0, size);
            System.arraycopy(state.getPointIndex(), 0, pointIndex, 0, size);
            if (state.getSequenceIndex() != null) {
                sequenceIndex = new long[state.getCapacity()];
                System.arraycopy(state.getSequenceIndex(), 0, sequenceIndex, 0, size);
            } else {
                sequenceIndex = null;
            }
        } else {
            weight = state.getWeight();
            pointIndex = state.getPointIndex();
            sequenceIndex = state.getSequenceIndex();
        }

        return new CompactSampler(state.getCapacity(), state.getSize(), state.getLambda(), new Random(seed), weight,
                pointIndex, sequenceIndex, validateHeap, state.getMaxSequenceIndex(),
                state.getSequenceIndexOfMostRecentLambdaUpdate());
    }

    @Override
    public CompactSamplerState toState(CompactSampler model) {
        CompactSamplerState state = new CompactSamplerState();
        state.setSize(model.size());
        state.setCapacity(model.getCapacity());
        state.setLambda(model.getTimeDecay());
        state.setSequenceIndexOfMostRecentLambdaUpdate(model.getSequenceIndexOfMostRecentLambdaUpdate());
        state.setMaxSequenceIndex(model.getMaxSequenceIndex());

        if (mode == Mode.COPY) {
            state.setWeight(Arrays.copyOf(model.getWeightArray(), model.size()));
            state.setPointIndex(Arrays.copyOf(model.getPointIndexArray(), model.size()));
            if (model.isStoreSequenceIndexesEnabled()) {
                state.setSequenceIndex(Arrays.copyOf(model.getSequenceIndexArray(), model.size()));
            }
        } else {
            state.setWeight(model.getWeightArray());
            state.setPointIndex(model.getPointIndexArray());
            if (model.isStoreSequenceIndexesEnabled()) {
                state.setSequenceIndex(model.getSequenceIndexArray());
            }
        }
        return state;
    }
}
