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