package filter;

import io.reactivex.functions.Predicate;
import message.ChangeMessage;
import type.ChangeType;

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
