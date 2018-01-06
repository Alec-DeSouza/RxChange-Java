package com.umbraltech.rxchange.message;

import com.umbraltech.rxchange.type.ChangeType;

/**
 * The core class used to construct the messages emitted by the change adapters
 *
 * @param <D> the type of data
 */
public class ChangeMessage<D> {
    private final D oldData;
    private final D newData;
    private final ChangeType changeType;

    /**
     * Creates a change message with the specified data and change type
     *
     * @param oldData    the original data
     * @param newData    the updated data
     * @param changeType the type of change that occurred
     */
    public ChangeMessage(final D oldData, final D newData, final ChangeType changeType) {
        this.oldData = oldData;
        this.newData = newData;
        this.changeType = changeType;
    }

    /**
     * Returns the original data
     *
     * @return the original data
     */
    public D getOldData() {
        return oldData;
    }

    /**
     * Returns the updated data
     *
     * @return the updated data
     */
    public D getNewData() {
        return newData;
    }

    /**
     * Returns the change type that occurred in the data
     *
     * @return the change type
     */
    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append('[')
                .append("oldData=")
                .append(oldData)
                .append(", ")
                .append("newData=")
                .append(newData)
                .append(", ")
                .append("changeType=")
                .append(changeType)
                .append(']');

        return stringBuilder.toString();
    }
}
