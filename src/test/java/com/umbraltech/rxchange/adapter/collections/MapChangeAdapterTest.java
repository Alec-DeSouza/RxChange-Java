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

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.umbraltech.rxchange.filter.ChangeTypeFilter;
import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.observer.ChangeMessageObserver;
import com.umbraltech.rxchange.type.ChangeType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MapChangeAdapterTest {
    private MapChangeAdapter<Integer, String> changeAdapter;

    @Before
    public void setUp() {
        changeAdapter = new MapChangeAdapter<>();
    }

    @Test
    public void add() {
        final Queue<Map.Entry<Integer, String>> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testQueue.add(Maps.immutableEntry(i, String.valueOf(i)));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        final MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>> metaChangeMessage =
                                (MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>>) changeMessage;

                        final MapDifference<Integer, String> dataDiff = Maps.difference(changeMessage.getOldData(),
                                changeMessage.getNewData());

                        assertEquals("Difference count (left)", 0, dataDiff.entriesOnlyOnLeft().size());
                        assertEquals("Difference count (right)", 1, dataDiff.entriesOnlyOnRight().size());

                        assertEquals("Difference (key)", true,
                                dataDiff.entriesOnlyOnRight().containsKey(testQueue.peek().getKey()));
                        assertEquals("Difference (value)", testQueue.peek().getValue(),
                                dataDiff.entriesOnlyOnRight().get(testQueue.peek().getKey()));

                        assertEquals("Metadata (key)", testQueue.peek().getKey(),
                                metaChangeMessage.getMetadata().getKey());
                        assertEquals("Metadata (value)", testQueue.poll().getValue(),
                                metaChangeMessage.getMetadata().getValue());
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", true, changeAdapter.add(i, String.valueOf(i)));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testQueue.size());
    }

    @Test
    public void addExisting() {
        for (int i = 0; i < 3; i++) {
            changeAdapter.add(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Add invoked for existing key");
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", false, changeAdapter.add(i, String.valueOf(i)));
        }
    }

    @Test
    public void addBatch() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        final MapDifference<Integer, String> dataDiffOld = Maps.difference(changeMessage.getOldData(),
                                testMap);
                        final MapDifference<Integer, String> dataDiffNew = Maps.difference(changeMessage.getNewData(),
                                testMap);

                        assertEquals("Difference count (old)", 3, dataDiffOld.entriesOnlyOnLeft().size()
                                + dataDiffOld.entriesOnlyOnRight().size()
                                + dataDiffOld.entriesDiffering().size());
                        assertEquals("Difference count (new)", 0, dataDiffNew.entriesOnlyOnLeft().size()
                                + dataDiffNew.entriesOnlyOnRight().size()
                                + dataDiffNew.entriesDiffering().size());
                    }
                });

        assertEquals("Data", true, changeAdapter.add(testMap));
    }

    @Test
    public void addBatchExisting() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i));
            changeAdapter.add(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Add invoked for duplicate keys");
                    }
                });

        assertEquals("Data", false, changeAdapter.add(testMap));
    }

    @Test
    public void remove() {
        final Queue<Map.Entry<Integer, String>> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testQueue.add(Maps.immutableEntry(i, String.valueOf(i)));
            changeAdapter.add(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        final MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>> metaChangeMessage =
                                (MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>>) changeMessage;

                        final MapDifference<Integer, String> dataDiff = Maps.difference(changeMessage.getOldData(),
                                changeMessage.getNewData());

                        assertEquals("Difference count (left)", 1, dataDiff.entriesOnlyOnLeft().size());
                        assertEquals("Difference count (right)", 0, dataDiff.entriesOnlyOnRight().size());

                        assertEquals("Difference (key)", true,
                                dataDiff.entriesOnlyOnLeft().containsKey(testQueue.peek().getKey()));
                        assertEquals("Difference (value)", testQueue.peek().getValue(),
                                dataDiff.entriesOnlyOnLeft().get(testQueue.peek().getKey()));

                        assertEquals("Metadata (key)", testQueue.peek().getKey(),
                                metaChangeMessage.getMetadata().getKey());
                        assertEquals("Metadata (value)", testQueue.poll().getValue(),
                                metaChangeMessage.getMetadata().getValue());
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", true, changeAdapter.remove(i));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testQueue.size());
    }

    @Test
    public void removeNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Remove invoked for nonexistent key");
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", false, changeAdapter.remove(i));
        }
    }

    @Test
    public void removeBatch() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i));
            changeAdapter.add(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        final MapDifference<Integer, String> dataDiffOld = Maps.difference(changeMessage.getOldData(),
                                testMap);
                        final MapDifference<Integer, String> dataDiffNew = Maps.difference(changeMessage.getNewData(),
                                testMap);

                        assertEquals("Difference count (old)", 0, dataDiffOld.entriesOnlyOnLeft().size()
                                + dataDiffOld.entriesOnlyOnRight().size()
                                + dataDiffOld.entriesDiffering().size());
                        assertEquals("Difference count (new)", 3, dataDiffNew.entriesOnlyOnLeft().size()
                                + dataDiffNew.entriesOnlyOnRight().size()
                                + dataDiffNew.entriesDiffering().size());
                    }
                });

        assertEquals("Data", true, changeAdapter.remove(testMap));
    }

    @Test
    public void removeBatchNonExistent() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Update invoked for nonexistent keys");
                    }
                });

        assertEquals("Data", false, changeAdapter.remove(testMap));
    }

    @Test
    public void update() {
        final Queue<Map.Entry<Integer, String>> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testQueue.add(Maps.immutableEntry(i, String.valueOf(i + 1)));
            changeAdapter.add(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        final MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>> metaChangeMessage =
                                (MetaChangeMessage<Map<Integer, String>, Map.Entry<Integer, String>>) changeMessage;

                        final MapDifference<Integer, String> dataDiff = Maps.difference(changeMessage.getOldData(),
                                changeMessage.getNewData());

                        assertEquals("Different keys", 0, dataDiff.entriesOnlyOnLeft().size()
                                + dataDiff.entriesOnlyOnRight().size());
                        assertEquals("Different values", 1, dataDiff.entriesDiffering().size());

                        assertEquals("Metadata key", testQueue.peek().getKey(),
                                metaChangeMessage.getMetadata().getKey());
                        assertEquals("Metadata value", testQueue.poll().getValue(),
                                metaChangeMessage.getMetadata().getValue());
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", true, changeAdapter.update(i, String.valueOf(i + 1)));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testQueue.size());
    }

    @Test
    public void updateNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Update invoked for nonexistent key");
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", false, changeAdapter.update(i, String.valueOf(i + 1)));
        }
    }

    @Test
    public void updateBatch() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i + 1));
            changeAdapter.add(i, String.valueOf(i));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        final MapDifference<Integer, String> dataDiffOld = Maps.difference(changeMessage.getOldData(),
                                testMap);
                        final MapDifference<Integer, String> dataDiffNew = Maps.difference(changeMessage.getNewData(),
                                testMap);

                        assertEquals("Difference count (old)", 3, dataDiffOld.entriesOnlyOnLeft().size()
                                + dataDiffOld.entriesOnlyOnRight().size()
                                + dataDiffOld.entriesDiffering().size());
                        assertEquals("Difference count (new)", 0, dataDiffNew.entriesOnlyOnLeft().size()
                                + dataDiffNew.entriesOnlyOnRight().size()
                                + dataDiffNew.entriesDiffering().size());
                    }
                });

        assertEquals("Data", true, changeAdapter.update(testMap));
    }

    @Test
    public void updateBatchNonExistent() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i + 1));
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<Map<Integer, String>>() {
                    @Override
                    public void onNext(ChangeMessage<Map<Integer, String>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Update invoked for nonexistent keys");
                    }
                });

        assertEquals("Data", false, changeAdapter.update(testMap));
    }

    @Test
    public void get() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i));
            changeAdapter.add(i, String.valueOf(i));
        }

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", testMap.get(i), changeAdapter.get(i));
        }
    }

    @Test
    public void getNonExistent() {
        for (int i = 0; i < 3; i++) {
            changeAdapter.add(i, String.valueOf(i));
        }

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", null, changeAdapter.get(i + 3));
        }
    }

    @Test
    public void getAll() {
        final Map<Integer, String> testMap = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            testMap.put(i, String.valueOf(i));
            changeAdapter.add(i, String.valueOf(i));
        }

        final MapDifference<Integer, String> dataDiff = Maps.difference(testMap, changeAdapter.getMap());

        assertEquals("Data (common)", 3, dataDiff.entriesInCommon().size());
        assertEquals("Data (different)", 0, dataDiff.entriesDiffering().size()
                + dataDiff.entriesOnlyOnLeft().size()
                + dataDiff.entriesOnlyOnRight().size());
    }
}