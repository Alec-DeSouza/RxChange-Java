package com.umbraltech.rxchange.filter;

import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.functions.Predicate;

/**
 * A utility class used for filtering the change type in observers
 * <p>
 * Only messages of the type specified by the filter will be passed to the observers
 */
public class ChangeTypeFilter implements Predicate<ChangeMessage> {
    private final ChangeType changeType;

    /**
     * Constructs the change type filter
     *
     * @param changeType the change type used for filtering
     */
    public ChangeTypeFilter(final ChangeType changeType) {
        this.changeType = changeType;
    }

    @Override
    public boolean test(ChangeMessage changeMessage) {
        return changeMessage.getChangeType() == changeType;
    }
}
