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

package com.amazon.randomcutforest.store;

import static com.amazon.randomcutforest.CommonUtils.checkArgument;

import java.util.Arrays;

/**
 * PointStore is a fixed size repository of points, where each point is a float
 * array of a specified length. A PointStore counts references to points that
 * are added, and frees space internally when a given point is no longer in use.
 * The primary use of this store is to enable compression since the points in
 * two different trees do not have to be stored separately.
 *
 * Stored points are referenced by index values which can be used to look up the
 * point values and increment and decrement reference counts. Valid index values
 * are between 0 (inclusive) and capacity (exclusive).
 */
public class PointStoreDouble extends PointStore<double[], double[]> {

    public PointStoreDouble(int dimensions, int shingleSize, int capacity, boolean overlapping, boolean directMap) {
        super(dimensions, shingleSize, capacity, overlapping, directMap);
        store = new double[capacity * dimensions];
    }

    public PointStoreDouble(boolean overlapping, int startOfFreeSegment, int dimensions, int shingleSize,
            double[] store, short[] refCount, int[] referenceList, int[] freeIndexes, int freeIndexPointer) {
        super(overlapping, dimensions, shingleSize, refCount, referenceList, freeIndexes, freeIndexPointer);
        checkArgument(dimensions > 0, "dimensions must be greater than 0");
        checkArgument(shingleSize == 1 || dimensions % shingleSize == 0, "incorrect use");
        checkArgument(refCount.length == capacity, "incorrect");
        this.store = store;
        this.startOfFreeSegment = startOfFreeSegment;
    }

    public PointStoreDouble(int dimensions, int capacity) {
        super(dimensions, 1, capacity, false, true);
        store = new double[capacity * dimensions];
    }

    @Override
    boolean checkShingleAlignment(int location, double[] point) {
        boolean test = true;
        for (int i = 0; i < dimensions - baseDimension && test; i++) {
            test = (point[i] == store[location - dimensions + baseDimension + i]);
        }
        return test;
    }

    @Override
    void copyPoint(double[] point, int src, int location, int length) {
        System.arraycopy(point, src, store, location, length);
    }

    /**
     * Test whether the given point is equal to the point stored at the given index.
     * This operation uses point-wise <code>==</code> to test for equality.
     *
     * @param index The index value of the point we are comparing to.
     * @param point The point we are comparing for equality.
     * @return true if the point stored at the index is equal to the given point,
     *         false otherwise.
     * @throws IllegalArgumentException if the index value is not valid.
     * @throws IllegalArgumentException if the current reference count for this
     *                                  index is nonpositive.
     * @throws IllegalArgumentException if the length of the point does not match
     *                                  the point store's dimensions.
     */

    @Override
    public boolean pointEquals(int index, double[] point) {
        checkValidIndex(index);
        checkArgument(point.length == dimensions, "point.length must be equal to dimensions");

        for (int j = 0; j < dimensions; j++) {
            if (point[j] != store[j + index * dimensions]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get a copy of the point at the given index.
     *
     * @param index An index value corresponding to a storage location in this point
     *              store.
     * @return a copy of the point stored at the given index.
     * @throws IllegalArgumentException if the index value is not valid.
     * @throws IllegalArgumentException if the current reference count for this
     *                                  index is nonpositive.
     */
    @Override
    public double[] get(int index) {
        checkValidIndex(index);
        int address = (directLocationMap) ? index * dimensions : locationList[index];
        return Arrays.copyOfRange(store, address, address + dimensions);
    }

    @Override
    public String toString(int index) {
        return Arrays.toString(get(index));
    }

    @Override
    void copyTo(int dest, int source, int length) {
        checkArgument(dest <= source, "error");
        for (int i = 0; i < length; i++) {
            store[dest + i] = store[source + i];
        }
    }

}
