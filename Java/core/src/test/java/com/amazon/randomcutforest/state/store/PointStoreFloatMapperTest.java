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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amazon.randomcutforest.state.Mode;
import com.amazon.randomcutforest.store.PointStoreFloat;

public class PointStoreFloatMapperTest {
    private int dimensions;
    private int capacity;
    private PointStoreFloat store;
    private List<Integer> indexes;
    private PointStoreFloatMapper mapper;

    @BeforeEach
    public void setUp() {
        dimensions = 2;
        capacity = 4;
        store = new PointStoreFloat(dimensions, capacity);

        indexes = new ArrayList<>();
        double[] point1 = { 1.1, -22.2 };
        indexes.add(store.add(point1));
        double[] point2 = { 3.3, -4.4 };
        indexes.add(store.add(point2));
        double[] point3 = { 10.1, 100.1 };
        indexes.add(store.add(point3));

        mapper = new PointStoreFloatMapper();
    }

    @Test
    public void testRoundTripWithCopy() {
        mapper.setMode(Mode.COPY);

        PointStoreFloat store2 = mapper.toModel(mapper.toState(store));
        assertEquals(store.getCapacity(), store2.getCapacity());
        assertEquals(store.size(), store2.size());
        assertEquals(store.getDimensions(), store2.getDimensions());
        assertEquals(store.getFreeIndexPointer(), store2.getFreeIndexPointer());

        assertNotSame(store.getStore(), store2.getStore());
        assertNotSame(store.getFreeIndexes(), store2.getFreeIndexes());

        indexes.forEach(i -> {
            assertArrayEquals(store.get(i), store2.get(i));
        });
    }

    @Test
    public void testRoundTripWithoutCopy() {
        mapper.setMode(Mode.REFERENCE);

        PointStoreFloat store2 = mapper.toModel(mapper.toState(store));
        assertEquals(store.getCapacity(), store2.getCapacity());
        assertEquals(store.size(), store2.size());
        assertEquals(store.getDimensions(), store2.getDimensions());
        assertEquals(store.getFreeIndexPointer(), store2.getFreeIndexPointer());

        assertSame(store.getStore(), store2.getStore());
    }
}
