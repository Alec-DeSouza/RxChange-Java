package com.umbraltech.rxchange.filter;

import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import io.reactivex.functions.Predicate;

public class MetadataFilter implements Predicate<ChangeMessage> {
    private final Class<?> metadataClass;

    public MetadataFilter(final Class<?> metadataClass) {
        this.metadataClass = metadataClass;
    }

    @Override
    public boolean test(ChangeMessage changeMessage) {

        // Verify message supports metadata
        if (!(changeMessage instanceof MetaChangeMessage)) {
            return false;
        }

        final MetaChangeMessage metaChangeMessage = (MetaChangeMessage) changeMessage;
        final Object metadata = metaChangeMessage.getMetadata();

        // Verify metadata is provided
        if (metadata == null) {
            return false;
        }

        // Verify type match
        return metadataClass.isInstance(metadata);
    }
}
