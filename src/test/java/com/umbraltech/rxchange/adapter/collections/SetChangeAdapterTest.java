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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.umbraltech.rxchange.filter.ChangeTypeFilter;
import com.umbraltech.rxchange.type.ChangeType;
import com.umbraltech.rxchange.util.ChangePayloadTestObserver;
import com.umbraltech.rxchange.util.InvocationFailObserver;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class SetChangeAdapterTest {
    private SetChangeAdapter<Integer> changeAdapter;

    private final Set<Integer> testSet = ImmutableSet.of(0, 1, 2);

    @Before
    public void setUp() {
        changeAdapter = new SetChangeAdapter<>();
    }

    @Test
    public void add() {
        final List<Set<Integer>> oldPayloadList = Lists.newArrayList(
                (Set<Integer>) ImmutableSet.<Integer>of(),
                ImmutableSet.of(0),
                ImmutableSet.of(0, 1)
        );

        final List<Set<Integer>> newPayloadList = Lists.newArrayList(
                (Set<Integer>) ImmutableSet.of(0),
                ImmutableSet.of(0, 1),
                ImmutableSet.of(0, 1, 2)
        );

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (final Integer i : testSet) {
            assertTrue("Add", changeAdapter.add(i));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void addExisting() {
        // Set up initial values
        changeAdapter.addAll(testSet);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new InvocationFailObserver<Set<Integer>>("Add invoked for existing value"));

        for (final Integer i : testSet) {
            assertFalse("Add existing", changeAdapter.add(i));
        }
    }

    @Test
    public void addAll() {
        final List<Set<Integer>> oldPayloadList = new ArrayList<>();
        oldPayloadList.add(ImmutableSet.<Integer>of());

        final List<Set<Integer>> newPayloadList = new ArrayList<>();
        newPayloadList.add(ImmutableSet.of(0, 1, 2));

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform batch update
        assertTrue("Add all", changeAdapter.addAll(testSet));

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void addAllExisting() {
        // Set up initial values
        changeAdapter.addAll(testSet);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new InvocationFailObserver<Set<Integer>>("Add invoked for existing values"));

        assertFalse("Add all existing", changeAdapter.addAll(testSet));
    }

    @Test
    public void remove() {
        final List<Set<Integer>> oldPayloadList = Lists.newArrayList(
                (Set<Integer>) ImmutableSet.of(0, 1, 2),
                ImmutableSet.of(1, 2),
                ImmutableSet.of(2)
        );

        final List<Set<Integer>> newPayloadList = Lists.newArrayList(
                (Set<Integer>) ImmutableSet.of(1, 2),
                ImmutableSet.of(2),
                ImmutableSet.<Integer>of()
        );

        // Set up initial values
        changeAdapter.addAll(testSet);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (final Integer i : testSet) {
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
                .subscribe(new InvocationFailObserver<Set<Integer>>("Remove invoked for nonexistent value"));

        for (final Integer i : testSet) {
            assertFalse("Remove nonexistent", changeAdapter.remove(i));
        }
    }

    @Test
    public void removeAll() {
        final List<Set<Integer>> oldPayloadList = new ArrayList<>();
        oldPayloadList.add(ImmutableSet.of(0, 1, 2));

        final List<Set<Integer>> newPayloadList = new ArrayList<>();
        newPayloadList.add(ImmutableSet.<Integer>of());

        // Set up initial values
        changeAdapter.addAll(testSet);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform batch update
        assertTrue("Remove all", changeAdapter.removeAll(testSet));

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void removeAllNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new InvocationFailObserver<Set<Integer>>("Remove invoked for nonexistent values"));

        assertFalse("Remove all nonexistent", changeAdapter.removeAll(testSet));
    }

    @Test
    public void getAll() {
        // Set up initial values
        changeAdapter.addAll(testSet);

        assertEquals("Get all", testSet, changeAdapter.getAll());
    }
}