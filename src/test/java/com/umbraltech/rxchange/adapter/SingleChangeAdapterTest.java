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

import com.google.common.collect.Lists;
import com.umbraltech.rxchange.filter.ChangeTypeFilter;
import com.umbraltech.rxchange.type.ChangeType;
import com.umbraltech.rxchange.util.ChangePayloadTestObserver;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SingleChangeAdapterTest {
    private SingleChangeAdapter<Integer> changeAdapter;

    private final List<Integer> testList = Lists.newArrayList(1, 2, 3);

    @Before
    public void setUp() {
        changeAdapter = new SingleChangeAdapter<>(0);
    }

    @Test
    public void update() {
        final List<Integer> oldPayloadList = Lists.newArrayList(0, 1, 2);
        final List<Integer> newPayloadList = Lists.newArrayList(1, 2, 3);

        changeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.UPDATE))
                .subscribe(new ChangePayloadTestObserver<>(oldPayloadList, newPayloadList));

        // Perform sequence of updates
        for (final Integer i : testList) {
            assertTrue("Update", changeAdapter.update(i));
        }

        // Verify all payloads were tested
        assertEquals("Remaining old payload", 0, oldPayloadList.size());
        assertEquals("Remaining new payload", 0, newPayloadList.size());
    }

    @Test
    public void get() {
        changeAdapter.update(testList.get(0));
        assertEquals("Get", testList.get(0), changeAdapter.get());
    }
}