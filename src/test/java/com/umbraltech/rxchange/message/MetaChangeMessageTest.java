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
}