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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.umbraltech.rxchange.filter.ChangeTypeFilter;
import com.umbraltech.rxchange.type.ChangeType;
import com.umbraltech.rxchange.util.ChangePayloadTestObserver;
import com.umbraltech.rxchange.util.InvocationFailObserver;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MapChangeAdapterTest {
    private MapChangeAdapter<Integer, String> changeAdapter;

    private final Map<Integer, String> testMap = ImmutableMap.of(0, "0", 1, "1", 2, "2");

    @Before
    public void setUp() {
        changeAdapter = new MapChangeAdapter<>();
    }

    @Test
    public void add() {
        final List<Map<Integer, String>> oldPayloadList = Lists.newArrayList(
                (Map<Integer, String>) ImmutableMap.<Integer, String>of(),
                ImmutableMap.of(0, "0"),
                ImmutableMap.of(0, "0", 1, "1")
        );

        final List<Map<Integer, String>> newPayloadList = Lists.newArrayList(
                (Map<Integer, String>) ImmutableMap.of(0, "0"),
                ImmutableMap.of(0, "0", 1, "1"),
                ImmutableMap.of(0, "0", 1, "1", 2, "2")
        );

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (final Map.Entry<Integer, String> entry : testMap.entrySet()) {
            assertTrue("Add", changeAdapter.add(entry.getKey(), entry.getValue()));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void addExisting() {
        // Set up initial values
        changeAdapter.addAll(testMap);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new InvocationFailObserver<Map<Integer, String>>("Add invoked for existing entry"));

        for (final Map.Entry<Integer, String> entry : testMap.entrySet()) {
            assertFalse("Add existing", changeAdapter.add(entry.getKey(), entry.getValue()));
        }
    }

    @Test
    public void addAll() {
        final List<Map<Integer, String>> oldPayloadList = new ArrayList<>();
        oldPayloadList.add(ImmutableMap.<Integer, String>of());

        final List<Map<Integer, String>> newPayloadList = new ArrayList<>();
        newPayloadList.add(ImmutableMap.of(0, "0", 1, "1", 2, "2"));

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform batch update
        assertTrue("Add all", changeAdapter.addAll(testMap));

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void addAllExisting() {
        // Set up initial values
        changeAdapter.addAll(testMap);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new InvocationFailObserver<Map<Integer, String>>("Add invoked for existing entries"));

        assertFalse("Add all existing", changeAdapter.addAll(testMap));
    }

    @Test
    public void remove() {
        final List<Map<Integer, String>> oldPayloadList = Lists.newArrayList(
                (Map<Integer, String>) ImmutableMap.of(0, "0", 1, "1", 2, "2"),
                ImmutableMap.of(1, "1", 2, "2"),
                ImmutableMap.of(2, "2")
        );

        final List<Map<Integer, String>> newPayloadList = Lists.newArrayList(
                (Map<Integer, String>) ImmutableMap.of(1, "1", 2, "2"),
                ImmutableMap.of(2, "2"),
                ImmutableMap.<Integer, String>of()
        );

        // Set up initial values
        changeAdapter.addAll(testMap);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (final Map.Entry<Integer, String> entry : testMap.entrySet()) {
            assertTrue("Remove", changeAdapter.remove(entry.getKey()));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void removeNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new InvocationFailObserver<Map<Integer, String>>("Remove invoked for nonexistent entry"));

        assertFalse("Remove nonexistent", changeAdapter.remove(0));
    }

    @Test
    public void removeAll() {
        final List<Map<Integer, String>> oldPayloadList = new ArrayList<>();
        oldPayloadList.add(ImmutableMap.of(0, "0", 1, "1", 2, "2"));

        final List<Map<Integer, String>> newPayloadList = new ArrayList<>();
        newPayloadList.add(ImmutableMap.<Integer, String>of());

        // Set up initial values
        changeAdapter.addAll(testMap);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform batch update
        assertTrue("Remove all", changeAdapter.removeAll(testMap.keySet()));

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void removeAllNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new InvocationFailObserver<Map<Integer, String>>("Remove invoked for nonexistent entries"));

        assertFalse("Remove all nonexistent", changeAdapter.removeAll(testMap.keySet()));
    }

    @Test
    public void update() {
        final Map<Integer, String> finalTestMap = ImmutableMap.of(0, "1", 1, "2", 2, "3");

        final List<Map<Integer, String>> oldPayloadList = Lists.newArrayList(
                (Map<Integer, String>) ImmutableMap.of(0, "0", 1, "1", 2, "2"),
                ImmutableMap.of(0, "1", 1, "1", 2, "2"),
                ImmutableMap.of(0, "1", 1, "2", 2, "2")
        );

        final List<Map<Integer, String>> newPayloadList = Lists.newArrayList(
                (Map<Integer, String>) ImmutableMap.of(0, "1", 1, "1", 2, "2"),
                ImmutableMap.of(0, "1", 1, "2", 2, "2"),
                ImmutableMap.of(0, "1", 1, "2", 2, "3")
        );

        // Set up initial values
        changeAdapter.addAll(testMap);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (final Map.Entry<Integer, String> entry : finalTestMap.entrySet()) {
            assertTrue("Update", changeAdapter.update(entry.getKey(), entry.getValue()));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void updateNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new InvocationFailObserver<Map<Integer, String>>("Update invoked for nonexistent entry"));

        assertFalse("Update nonexistent", changeAdapter.update(0, "0"));
    }

    @Test
    public void updateAll() {
        final Map<Integer, String> finalTestMap = ImmutableMap.of(0, "1", 1, "2", 2, "3");

        final List<Map<Integer, String>> oldPayloadList = new ArrayList<>();
        oldPayloadList.add(ImmutableMap.of(0, "0", 1, "1", 2, "2"));

        final List<Map<Integer, String>> newPayloadList = new ArrayList<>();
        newPayloadList.add(ImmutableMap.of(0, "1", 1, "2", 2, "3"));

        // Set up initial values
        changeAdapter.addAll(testMap);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform batch update
        assertTrue("Update all", changeAdapter.updateAll(finalTestMap));

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void updateAllNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new InvocationFailObserver<Map<Integer, String>>("Update invoked for nonexistent entries"));

        assertFalse("Update all nonexistent", changeAdapter.updateAll(testMap));
    }

    @Test
    public void get() {
        // Set up initial values
        changeAdapter.addAll(testMap);

        for (final Integer key : testMap.keySet()) {
            assertEquals("Get", testMap.get(key), changeAdapter.get(key));
        }
    }

    @Test
    public void getNonExistent() {
        assertNull("Get nonexistent", changeAdapter.get(0));
    }

    @Test
    public void getAll() {
        // Set up initial values
        changeAdapter.addAll(testMap);

        assertEquals("Get all", testMap, changeAdapter.getAll());
    }
}