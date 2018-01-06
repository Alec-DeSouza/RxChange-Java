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

package com.umbraltech.rxchange.message;

import com.umbraltech.rxchange.type.ChangeType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetaChangeMessageTest {
    private MetaChangeMessage<Integer, Integer> changeMessage;

    @Before
    public void setUp() {
        changeMessage = new MetaChangeMessage<>(2, 1, ChangeType.UPDATE, -1);
    }

    @Test
    public void getMetadata() {
        assertEquals("Metadata", (Integer) (-1), changeMessage.getMetadata());
    }

    @Test
    public void getString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append('[')
                .append("changeMessage=")
                .append('[')
                .append("oldData=")
                .append(changeMessage.getOldData())
                .append(", ")
                .append("newData=")
                .append(changeMessage.getNewData())
                .append(", ")
                .append("changeType=")
                .append(changeMessage.getChangeType())
                .append(']')
                .append(", ")
                .append("metadata=")
                .append(changeMessage.getMetadata())
                .append(']');

        assertEquals("toString", stringBuilder.toString(), changeMessage.toString());
    }
}