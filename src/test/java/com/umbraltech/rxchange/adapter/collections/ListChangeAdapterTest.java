/*
 * Copyright 2018 - present, RxChange contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.umbraltech.rxchange.adapter.collections;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.umbraltech.rxchange.filter.ChangeTypeFilter;
import com.umbraltech.rxchange.type.ChangeType;
import com.umbraltech.rxchange.util.ChangePayloadTestObserver;
import com.umbraltech.rxchange.util.InvocationFailObserver;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ListChangeAdapterTest {
    private ListChangeAdapter<Integer> changeAdapter;

    private final List<Integer> testList = ImmutableList.of(0, 1, 2);

    @Before
    public void setUp() {
        changeAdapter = new ListChangeAdapter<>();
    }

    @Test
    public void add() {
        final List<List<Integer>> oldPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.<Integer>of(),
                ImmutableList.of(0),
                ImmutableList.of(0, 1)
        );

        final List<List<Integer>> newPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.of(0),
                ImmutableList.of(0, 1),
                ImmutableList.of(0, 1, 2)
        );

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (final Integer i : testList) {
            assertTrue("Add", changeAdapter.add(i));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void addAt() {
        final List<List<Integer>> oldPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.<Integer>of(),
                ImmutableList.of(0),
                ImmutableList.of(0, 1)
        );

        final List<List<Integer>> newPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.of(0),
                ImmutableList.of(0, 1),
                ImmutableList.of(0, 1, 2)
        );

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (int i = 0; i < testList.size(); i++) {
            assertTrue("Add at", changeAdapter.addAt(i, testList.get(i)));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void addAtNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new InvocationFailObserver<List<Integer>>("Add invoked for invalid index"));

        assertFalse("Add at nonexistent", changeAdapter.addAt(1, 0));
    }

    @Test
    public void addAll() {
        final List<List<Integer>> oldPayloadList = new ArrayList<>();
        oldPayloadList.add(ImmutableList.<Integer>of());

        final List<List<Integer>> newPayloadList = new ArrayList<>();
        newPayloadList.add(ImmutableList.of(0, 1, 2));

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform batch update
        assertTrue("Add all", changeAdapter.addAll(testList));

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void remove() {
        final List<List<Integer>> oldPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.of(0, 1, 2),
                ImmutableList.of(1, 2),
                ImmutableList.of(2)
        );

        final List<List<Integer>> newPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.of(1, 2),
                ImmutableList.of(2),
                ImmutableList.<Integer>of()
        );

        // Set up initial values
        changeAdapter.addAll(testList);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (final Integer i : testList) {
            assertTrue("Remove", changeAdapter.remove(i));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void removeNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new InvocationFailObserver<List<Integer>>("Remove invoked for nonexistent entry"));

        assertFalse("Remove nonexistent", changeAdapter.remove(0));
    }

    @Test
    public void removeAt() {
        final List<List<Integer>> oldPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.of(0, 1, 2),
                ImmutableList.of(1, 2),
                ImmutableList.of(2)
        );

        final List<List<Integer>> newPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.of(1, 2),
                ImmutableList.of(2),
                ImmutableList.<Integer>of()
        );

        // Set up initial values
        changeAdapter.addAll(testList);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (int i = 0; i < testList.size(); i++) {
            assertTrue("Remove at", changeAdapter.removeAt(0));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void removeAtNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new InvocationFailObserver<List<Integer>>("Remove invoked for invalid index"));

        assertFalse("Remove at nonexistent", changeAdapter.removeAt(0));
    }

    @Test
    public void removeAll() {
        final List<List<Integer>> oldPayloadList = new ArrayList<>();
        oldPayloadList.add(ImmutableList.of(0, 1, 2));

        final List<List<Integer>> newPayloadList = new ArrayList<>();
        newPayloadList.add(ImmutableList.<Integer>of());

        // Set up initial values
        changeAdapter.addAll(testList);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform batch update
        assertTrue("Remove all", changeAdapter.removeAll(testList));

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void removeAllNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new InvocationFailObserver<List<Integer>>("Remove invoked for nonexistent entries"));

        assertFalse("Remove all nonexistent", changeAdapter.removeAll(testList));
    }

    @Test
    public void update() {
        final List<Integer> finalTestList = Lists.newArrayList(1, 2, 3);

        final List<List<Integer>> oldPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.of(0, 1, 2),
                ImmutableList.of(1, 1, 2),
                ImmutableList.of(1, 2, 2)
        );

        final List<List<Integer>> newPayloadList = Lists.newArrayList(
                (List<Integer>) ImmutableList.of(1, 1, 2),
                ImmutableList.of(1, 2, 2),
                ImmutableList.of(1, 2, 3)
        );

        // Set up initial values
        changeAdapter.addAll(testList);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (int i = 0; i < finalTestList.size(); i++) {
            assertTrue("Update", changeAdapter.update(i, finalTestList.get(i)));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void updateNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new InvocationFailObserver<List<Integer>>("Update invoked for nonexistent entry"));

        assertFalse("Update nonexistent", changeAdapter.update(0, 1));
    }

    @Test
    public void get() {
        // Set up initial values
        changeAdapter.addAll(testList);

        for (int i = 0; i < testList.size(); i++) {
            assertEquals("Get", testList.get(i), changeAdapter.get(i));
        }
    }

    @Test
    public void getAll() {
        // Set up initial values
        changeAdapter.addAll(testList);

        assertEquals("Get all", testList, changeAdapter.getAll());
    }
}