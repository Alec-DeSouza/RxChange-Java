package message;

import org.junit.Before;
import org.junit.Test;
import type.ChangeType;

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
}