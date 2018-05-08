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
        return String.format("%s(changeMessage=%s, metadata=%s)",
                MetaChangeMessage.class.getSimpleName(),
                super.toString(),
                metadata);
    }
}
