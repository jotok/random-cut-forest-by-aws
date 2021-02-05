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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amazon.randomcutforest.state.Mode;
import com.amazon.randomcutforest.store.SmallLeafStore;

public class SmallLeafStoreMapperTest {
    private SmallLeafStore store;
    private List<Integer> indexes;

    private SmallLeafStoreMapper mapper;

    @BeforeEach
    public void setUp() {

        store = new SmallLeafStore((short) 10);

        indexes = new ArrayList<>();
        indexes.add(store.addLeaf(1, 2, 1));
        indexes.add(store.addLeaf(1, 3, 2));
        indexes.add(store.addLeaf(4, 5, 1));
        indexes.add(store.addLeaf(4, 6, 3));

        mapper = new SmallLeafStoreMapper();
    }

    @Test
    public void testRoundTripWithCopy() {
        mapper.setMode(Mode.COPY);

        SmallLeafStore store2 = mapper.toModel(mapper.toState(store));
        assertEquals(store.getCapacity(), store2.getCapacity());
        assertEquals(store.size(), store2.size());
        assertEquals(store.getFreeIndexPointer(), store2.getFreeIndexPointer());

        assertNotSame(store.pointIndex, store2.pointIndex);
        assertNotSame(store.parentIndex, store2.parentIndex);
        assertNotSame(store.mass, store2.mass);
        assertNotSame(store.getFreeIndexes(), store2.getFreeIndexes());

        indexes.forEach(i -> {
            assertEquals(store.getParent(i), store2.getParent(i));
            assertEquals(store.getPointIndex(i), store2.getPointIndex(i));
            assertEquals(store.getMass(i), store2.getMass(i));
        });
    }

    @Test
    public void testRoundTripWithoutCopy() {
        mapper.setMode(Mode.REFERENCE);

        SmallLeafStore store2 = mapper.toModel(mapper.toState(store));
        assertEquals(store.getCapacity(), store2.getCapacity());
        assertEquals(store.size(), store2.size());
        assertEquals(store.getFreeIndexPointer(), store2.getFreeIndexPointer());

        assertSame(store.pointIndex, store2.pointIndex);
        assertSame(store.parentIndex, store2.parentIndex);
        assertSame(store.mass, store2.mass);
        assertSame(store.getFreeIndexes(), store2.getFreeIndexes());
    }
}
