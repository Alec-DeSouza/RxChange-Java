package com.umbraltech.rxchange.adapter.collections;

import com.google.common.collect.Sets;
import com.umbraltech.rxchange.filter.ChangeTypeFilter;
import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.observer.ChangeMessageObserver;
import com.umbraltech.rxchange.type.ChangeType;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SetChangeAdapterTest {

    private SetChangeAdapter<Integer> changeAdapter;

    @Before
    public void setUp() {
        changeAdapter = new SetChangeAdapter<>();
    }

    @Test
    public void add() {
        final Queue<Integer> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testQueue.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangeMessageObserver<Set<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<Set<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        final MetaChangeMessage<Set<Integer>, Integer> metaChangeMessage =
                                (MetaChangeMessage<Set<Integer>, Integer>) changeMessage;

                        final Sets.SetView<Integer> dataDiff = Sets.difference(changeMessage.getNewData(),
                                changeMessage.getOldData());

                        assertEquals("Difference (count)", 1, dataDiff.size());
                        assertEquals("Difference (value)", testQueue.peek(), dataDiff.iterator().next());

                        assertEquals("Metadata", testQueue.poll(), metaChangeMessage.getMetadata());
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", true, changeAdapter.add(i));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testQueue.size());
    }

    @Test
    public void addExisting() {
        for (int i = 0; i < 3; i++) {
            changeAdapter.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(new ChangeMessageObserver<Set<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<Set<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Add invoked for existing value");
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", false, changeAdapter.add(i));
        }
    }

    @Test
    public void remove() {
        final Queue<Integer> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testQueue.add(i);
            changeAdapter.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.REMOVE))
                .subscribe(new ChangeMessageObserver<Set<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<Set<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        final MetaChangeMessage<Set<Integer>, Integer> metaChangeMessage =
                                (MetaChangeMessage<Set<Integer>, Integer>) changeMessage;

                        final Sets.SetView<Integer> dataDiff = Sets.difference(changeMessage.getOldData(),
                                changeMessage.getNewData());

                        assertEquals("Difference (count)", 1, dataDiff.size());
                        assertEquals("Difference (value)", testQueue.peek(), dataDiff.iterator().next());

                        assertEquals("Metadata", testQueue.poll(), metaChangeMessage.getMetadata());
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
                .subscribe(new ChangeMessageObserver<Set<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<Set<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Remove invoked for nonexistent value");
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", false, changeAdapter.remove(i));
        }
    }

    @Test
    public void update() {
        final Queue<Integer> testQueue = new LinkedList<>();

        for (int i = 0; i < 3; i++) {
            testQueue.add(i);
            changeAdapter.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<Set<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<Set<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        final MetaChangeMessage<Set<Integer>, List<Integer>> metaChangeMessage =
                                (MetaChangeMessage<Set<Integer>, List<Integer>>) changeMessage;

                        final Sets.SetView<Integer> dataDiff = Sets.difference(changeMessage.getNewData(),
                                changeMessage.getOldData());

                        assertEquals("Difference (count)", 1, dataDiff.size());
                        assertEquals("Difference (value)", (Integer) (testQueue.peek() + 3),
                                dataDiff.iterator().next());

                        assertEquals("Metadata (old)", testQueue.peek(),
                                metaChangeMessage.getMetadata().get(0));
                        assertEquals("Metadata (new)", (Integer) (testQueue.poll() + 3),
                                metaChangeMessage.getMetadata().get(1));
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", true, changeAdapter.update(i, i + 3));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testQueue.size());
    }

    @Test
    public void updateOldNonExistent() {
        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<Set<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<Set<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Update invoked for nonexistent old value");
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", false, changeAdapter.update(i, i + 3));
        }
    }

    @Test
    public void updateNewExisting() {
        for (int i = 0; i < 3; i++) {
            changeAdapter.add(i);
            changeAdapter.add(i + 3);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<Set<Integer>>() {
                    @Override
                    public void onNext(ChangeMessage<Set<Integer>> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        fail("Update invoked for existing new value");
                    }
                });

        for (int i = 0; i < 3; i++) {
            assertEquals("Data", false, changeAdapter.update(i, i + 3));
        }
    }

    @Test
    public void getSet() {
        final Set<Integer> testSet = new HashSet<>();

        for (int i = 0; i < 3; i++) {
            testSet.add(i);
            changeAdapter.add(i);
        }

        final Sets.SetView<Integer> leftDifference = Sets.difference(testSet, changeAdapter.getSet());
        final Sets.SetView<Integer> rightDifference = Sets.difference(changeAdapter.getSet(), testSet);

        assertEquals("Difference", 0, leftDifference.size() + rightDifference.size());
    }
}