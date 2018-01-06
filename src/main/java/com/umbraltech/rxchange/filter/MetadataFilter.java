package com.umbraltech.rxchange.filter;

import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import io.reactivex.functions.Predicate;

/**
 * A utility class used for filtering the metadata type in observers
 * <p>
 * Only messages whose metadata is an instance of the specified type
 * will be passed to observers
 */
public class MetadataFilter implements Predicate<ChangeMessage> {
    private final Class<?> metadataClass;

    /**
     * Constructs the metadata filter
     *
     * @param metadataClass the class type used for filtering the metadata
     */
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
