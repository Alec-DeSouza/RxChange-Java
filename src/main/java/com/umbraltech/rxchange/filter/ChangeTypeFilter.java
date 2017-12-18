package com.umbraltech.rxchange.filter;

import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.functions.Predicate;

public class ChangeTypeFilter implements Predicate<ChangeMessage> {
    private final ChangeType changeType;

    public ChangeTypeFilter(final ChangeType changeType) {
        this.changeType = changeType;
    }

    @Override
    public boolean test(ChangeMessage changeMessage) {
        return changeMessage.getChangeType() == changeType;
    }
}
