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
import com.amazon.randomcutforest.store.NodeStore;

public class NodeStoreMapperTest {
    private NodeStore store;
    private NodeStoreMapper mapper;
    private List<Integer> indexes;

    @BeforeEach
    public void setUp() {
        indexes = new ArrayList<>();

        store = new NodeStore(10);
        indexes.add(store.addNode(1, 2, 3, 1, 6.6, 1));
        indexes.add(store.addNode(1, 4, 5, 2, -14.8, 2));
        indexes.add(store.addNode(6, 7, 8, 1, 9.8, 4));
        indexes.add(store.addNode(6, 10, 11, 4, -1000.01, 1));

        mapper = new NodeStoreMapper();
    }

    @Test
    public void testRoundTripWithCopy() {
        mapper.setMode(Mode.COPY);

        NodeStore store2 = mapper.toModel(mapper.toState(store));
        assertEquals(store.getCapacity(), store2.getCapacity());
        assertEquals(store.size(), store2.size());
        assertEquals(store.getFreeIndexPointer(), store2.getFreeIndexPointer());

        assertNotSame(store.parentIndex, store2.parentIndex);
        assertNotSame(store.leftIndex, store2.leftIndex);
        assertNotSame(store.rightIndex, store2.rightIndex);
        assertNotSame(store.cutDimension, store2.cutDimension);
        assertNotSame(store.cutValue, store2.cutValue);
        assertNotSame(store.mass, store2.mass);

        indexes.forEach(i -> {
            assertEquals(store.getParent(i), store2.getParent(i));
            assertEquals(store.getLeftIndex(i), store2.getLeftIndex(i));
            assertEquals(store.getRightIndex(i), store2.getRightIndex(i));
            assertEquals(store.getCutDimension(i), store2.getCutDimension(i));
            assertEquals(store.getCutValue(i), store2.getCutValue(i));
            assertEquals(store.getMass(i), store2.getMass(i));
        });
    }

    @Test
    public void testRoundTripWithoutCopy() {
        mapper.setMode(Mode.REFERENCE);

        NodeStore store2 = mapper.toModel(mapper.toState(store));
        assertEquals(store.getCapacity(), store2.getCapacity());
        assertEquals(store.size(), store2.size());
        assertEquals(store.getFreeIndexPointer(), store2.getFreeIndexPointer());

        assertSame(store.parentIndex, store2.parentIndex);
        assertSame(store.leftIndex, store2.leftIndex);
        assertSame(store.rightIndex, store2.rightIndex);
        assertSame(store.cutDimension, store2.cutDimension);
        assertSame(store.cutValue, store2.cutValue);
        assertSame(store.mass, store2.mass);
    }
}
