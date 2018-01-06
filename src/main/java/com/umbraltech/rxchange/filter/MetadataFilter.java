/*
 * Copyright 2018 - present, RxChange contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
