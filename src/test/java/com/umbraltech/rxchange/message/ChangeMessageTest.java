package com.umbraltech.rxchange.message;

import com.umbraltech.rxchange.type.ChangeType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChangeMessageTest {

    private ChangeMessage<Integer> changeMessage;

    @Before
    public void setUp() {
        changeMessage = new ChangeMessage<>(1, 2, ChangeType.UPDATE);
    }

    @Test
    public void getOldData() {
        assertEquals("Old data", (Integer) 1, changeMessage.getOldData());
    }

    @Test
    public void getNewData() {
        assertEquals("New data", (Integer) 2, changeMessage.getNewData());
    }

    @Test
    public void getChangeType() {
        assertEquals("Change type", ChangeType.UPDATE, changeMessage.getChangeType());
    }

    @Test
    public void getString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append('[')
                .append("oldData=")
                .append(changeMessage.getOldData())
                .append(", ")
                .append("newData=")
                .append(changeMessage.getNewData())
                .append(", ")
                .append("changeType=")
                .append(changeMessage.getChangeType())
                .append(']');

        assertEquals("toString", stringBuilder.toString(), changeMessage.toString());
    }
}