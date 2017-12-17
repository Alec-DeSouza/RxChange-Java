package message;

import type.ChangeType;

public class ChangeMessage<D> {
    private final D oldData;
    private final D newData;
    private final ChangeType changeType;

    public ChangeMessage(final D oldData, final D newData, final ChangeType changeType) {
        this.oldData = oldData;
        this.newData = newData;
        this.changeType = changeType;
    }

    public D getOldData() {
        return oldData;
    }

    public D getNewData() {
        return newData;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append('[')
                .append("oldData=")
                .append(oldData)
                .append(',')
                .append("newData=")
                .append(newData)
                .append(',')
                .append("changeType=")
                .append(changeType)
                .append(']');

        return stringBuilder.toString();
    }
}
