package com.umbraltech.rxchange.filter;

import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChangeTypeFilterTest {
    private ChangeMessage<Integer> changeMessage;

    @Before
    public void setUp() {
        changeMessage = new ChangeMessage<>(0, 1, ChangeType.UPDATE);
    }

    @Test
    public void test() {
        final ChangeTypeFilter addTypeFilter = new ChangeTypeFilter(ChangeType.ADD);
        final ChangeTypeFilter removeTypeFilter = new ChangeTypeFilter(ChangeType.REMOVE);
        final ChangeTypeFilter updateTypeFilter = new ChangeTypeFilter(ChangeType.UPDATE);

        assertEquals("Change type add", false, addTypeFilter.test(changeMessage));
        assertEquals("Change type remove", false, removeTypeFilter.test(changeMessage));
        assertEquals("Change type update", true, updateTypeFilter.test(changeMessage));
    }
}