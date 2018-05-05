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

    @Test
    public void get() {
        changeAdapter.update(1);
        assertEquals("Data", (Integer) 1, changeAdapter.get());
    }
}