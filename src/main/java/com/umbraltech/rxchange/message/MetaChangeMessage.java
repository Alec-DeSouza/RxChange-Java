package com.umbraltech.rxchange.message;

import com.umbraltech.rxchange.type.ChangeType;

public class MetaChangeMessage<D, M> extends ChangeMessage<D> {
    private final M metadata;

    public MetaChangeMessage(final D oldData, final D newData, final ChangeType changeType, final M metadata) {
        super(oldData, newData, changeType);
        this.metadata = metadata;
    }

    public M getMetadata() {
        return metadata;
    }
}
