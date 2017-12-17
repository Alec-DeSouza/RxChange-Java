package adapter.collections;

import com.google.common.primitives.Ints;
import filter.ChangeTypeFilter;
import message.ChangeMessage;
import observer.ChangeMessageObserver;
import org.junit.Before;
import org.junit.Test;
import type.ChangeType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
                        System.out.println(changeMessage.toString());

                        assertArrayEquals("Old data", Ints.toArray(testList.subList(0, testIndexQueue.peek())),
                                Ints.toArray(changeMessage.getOldData()));
                        assertArrayEquals("New data", Ints.toArray(testList.subList(0, testIndexQueue.poll() + 1)),
                                Ints.toArray(changeMessage.getNewData()));
                    }
                });

        for (int i = 0; i < 3; i++) {
            changeAdapter.add(i);
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testIndexQueue.size());
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
                        System.out.println(changeMessage.toString());

                        assertArrayEquals("Old data", Ints.toArray(testList.subList(testIndexQueue.peek(), testList.size())),
                                Ints.toArray(changeMessage.getOldData()));
                        assertArrayEquals("New data", Ints.toArray(testList.subList(testIndexQueue.poll() + 1, testList.size())),
                                Ints.toArray(changeMessage.getNewData()));
                    }
                });

        for (int i = 0; i < 3; i++) {
            changeAdapter.remove(0);
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testIndexQueue.size());
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
                        System.out.println(changeMessage.toString());

                        assertEquals("Old data", testList.get(testIndexQueue.peek()),
                                changeMessage.getOldData().get(testIndexQueue.peek()));
                        assertEquals("New data", (Integer) (testList.get(testIndexQueue.peek()) + 1),
                                changeMessage.getNewData().get(testIndexQueue.poll()));
                    }
                });

        for (int i = 0; i < 3; i++) {
            changeAdapter.update(i, i + 1);
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testIndexQueue.size());
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

        assertArrayEquals("Data", Ints.toArray(testList), Ints.toArray(changeAdapter.getAll()));
    }
}