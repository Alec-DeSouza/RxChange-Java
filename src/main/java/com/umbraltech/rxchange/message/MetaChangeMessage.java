package com.umbraltech.rxchange.message;

import com.umbraltech.rxchange.type.ChangeType;

/**
 * A core class used to provide metadata values with change messages
 * <p>
 * The metadata can be used to provide details on which elements were changed
 *
 * @param <D> the type of data
 * @param <M> the type of metadata
 */
public class MetaChangeMessage<D, M> extends ChangeMessage<D> {
    private final M metadata;

    /**
     * @param oldData    the original data
     * @param newData    the updated data
     * @param changeType the type of change that occurred
     * @param metadata   the metadata
     */
    public MetaChangeMessage(final D oldData, final D newData, final ChangeType changeType, final M metadata) {
        super(oldData, newData, changeType);
        this.metadata = metadata;
    }

    /**
     * Returns the metadata
     *
     * @return the metadata
     */
    public M getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append('[')
                .append("changeMessage=")
                .append(super.toString())
                .append(", ")
                .append("metadata=")
                .append(metadata)
                .append(']');

        return stringBuilder.toString();
    }
}
