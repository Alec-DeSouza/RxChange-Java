package com.umbraltech.rxchange.adapter;

import com.umbraltech.rxchange.filter.ChangeTypeFilter;
import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.observer.ChangeMessageObserver;
import com.umbraltech.rxchange.type.ChangeType;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.assertEquals;

public class SingleChangeAdapterTest {
    private SingleChangeAdapter<Integer> changeAdapter;

    @Before
    public void setUp() {
        changeAdapter = new SingleChangeAdapter<>(0);
    }

    @Test
    public void update() {
        final Queue<Integer> testQueue = new LinkedList<>();

        for (int i = 1; i < 4; i++) {
            testQueue.add(i);
        }

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangeMessageObserver<Integer>() {
                    @Override
                    public void onNext(ChangeMessage<Integer> changeMessage) {
                        //System.out.println(changeMessage.toString());

                        assertEquals("Old data", (Integer) (testQueue.peek() - 1), changeMessage.getOldData());
                        assertEquals("New data", testQueue.poll(), changeMessage.getNewData());
                    }
                });

        for (int i = 1; i < 4; i++) {
            assertEquals("Update", true, changeAdapter.update(i));
        }

        // Verify queue was emptied
        assertEquals("Test queue", 0, testQueue.size());
    }
}