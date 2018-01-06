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
