package com.umbraltech.rxchange.adapter.collections;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.umbraltech.rxchange.filter.ChangeTypeFilter;
import com.umbraltech.rxchange.filter.MetadataFilter;
import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.observer.ChangeMessageObserver;
import com.umbraltech.rxchange.type.ChangeType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.*;

public class ListChangeAdapterTest {
    private ListChangeAdapter<Integer> changeAdapter;

    @Before
    public void setUp() {
        changeAdapter = new ListChangeAdapter<>();
    }

    @Test
    public void add() {
        final List<Integer> testList = new ArrayList<>();
        final Queue<Integer> testIndexQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testList.add(i);
            testIndexQueue.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        assertArrayEquals("Old data", Ints.toArray(testList.subList(0, testIndexQueue.peek())),
                                Ints.toArray(changeMessage.getOldData()));
                        assertArrayEquals("New data", Ints.toArray(testList.subList(0, testIndexQueue.poll() + 1)),
                                Ints.toArray(changeMessage.getNewData()));
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Add", true, changeAdapter.add(i));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testIndexQueue.size());
    }

    @Test
    public void addBatch() {
        final List<Integer> testList = new ArrayList<>();
        final Queue<Integer> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testList.add(i);
            testQueue.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .filter(new MetadataFilter(Integer.class))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        fail("Filtered out type");
                    }
                });

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .filter(new MetadataFilter(List.class))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        assertArrayEquals("Old data", Ints.toArray(ImmutableList.<Number>of()),
                                Ints.toArray(changeMessage.getOldData()));
                        assertArrayEquals("New data", Ints.toArray(testList),
                                Ints.toArray(changeMessage.getNewData()));

                        testQueue.poll();
                    }
                });

        assertEquals("Add", true, changeAdapter.add(testList));

        // Verify only single entry was removed
        assertEquals("Test queue", 2, testQueue.size());
    }

    @Test
    public void remove() {
        final List<Integer> testList = new ArrayList<>();
        final Queue<Integer> testIndexQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testList.add(i);
            testIndexQueue.add(i);

            changeAdapter.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        assertArrayEquals("Old data", Ints.toArray(testList.subList(testIndexQueue.peek(), testList.size())),
                                Ints.toArray(changeMessage.getOldData()));
                        assertArrayEquals("New data", Ints.toArray(testList.subList(testIndexQueue.poll() + 1, testList.size())),
                                Ints.toArray(changeMessage.getNewData()));
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Remove", true, changeAdapter.remove(0));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testIndexQueue.size());
    }

    @Test
    public void removeNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Remove invoked for nonexistent entry");
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Remove", false, changeAdapter.remove(i));
        }
    }

    @Test
    public void removeBatch() {
        final List<Integer> testList = new ArrayList<>();
        final Queue<Integer> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testList.add(i);
            testQueue.add(i);

            changeAdapter.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .filter(new MetadataFilter(Integer.class))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        fail("Filtered out type");
                    }
                });

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .filter(new MetadataFilter(List.class))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        assertArrayEquals("Old data", Ints.toArray(testList),
                                Ints.toArray(changeMessage.getOldData()));
                        assertArrayEquals("New data", Ints.toArray(ImmutableList.<Number>of()),
                                Ints.toArray(changeMessage.getNewData()));

                        testQueue.poll();
                    }
                });

        assertEquals("Remove", true, changeAdapter.remove(testList));

        // Verify only single entry was removed
        assertEquals("Test queue", 2, testQueue.size());
    }

    @Test
    public void removeBatchNonExistent() {
        final List<Integer> testList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            testList.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Remove invoked for nonexistent entries");
                    }
                });

        assertEquals("Remove", false, changeAdapter.remove(testList));
    }

    @Test
    public void update() {
        final List<Integer> testList = new ArrayList<>();
        final Queue<Integer> testIndexQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testList.add(i);
            testIndexQueue.add(i);

            changeAdapter.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        assertEquals("Old data", testList.get(testIndexQueue.peek()),
                                changeMessage.getOldData().get(testIndexQueue.peek()));
                        assertEquals("New data", (Integer) (testList.get(testIndexQueue.peek()) + 1),
                                changeMessage.getNewData().get(testIndexQueue.poll()));
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Update", true, changeAdapter.update(i, i + 1));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testIndexQueue.size());
    }

    @Test
    public void updateNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<List<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<List<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Update invoked for nonexistent entry");
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Update", false, changeAdapter.update(i, i + 1));
        }
    }

    @Test
    public void get() {
        final List<Integer> testList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            testList.add(i);
            changeAdapter.add(i);
        }

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", testList.get(i), changeAdapter.get(i));
        }
    }

    @Test
    public void getAll() {
        final List<Integer> testList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            testList.add(i);
            changeAdapter.add(i);
        }

        assertArrayEquals("Data", Ints.toArray(testList), Ints.toArray(changeAdapter.getList()));
    }
}