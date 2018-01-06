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

package com.umbraltech.rxchange.adapter;

import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * An adapter that implements the reactive change model for a single element
 *
 * @param <D> the type of data for the element
 */
public class SingleChangeAdapter<D> {
    private final PublishSubject<ChangeMessage<D>> publishSubject = PublishSubject.create();
    private D data;

    /**
     * Initializes the adapter with a value, without emitting a change message
     *
     * @param data the initial data
     */
    public SingleChangeAdapter(final D data) {
        this.data = data;
    }

    /**
     * Updates the value of the element and emits a change message to surrounding observers
     * <p>
     * No metadata is provided with the change message
     *
     * @param data the new value of the element
     * @return {@code true} if the element was updated, {@code false} otherwise
     */
    public boolean update(final D data) {
        final D oldData = this.data;
        this.data = data;

        // Signal update
        publishSubject.onNext(new MetaChangeMessage<>(oldData, this.data, ChangeType.UPDATE, null));

        return true;
    }

    /**
     * Returns a reference to the observable used for listening to change messages
     *
     * @return the observable reference
     */
    public Observable<ChangeMessage<D>> getObservable() {
        return publishSubject;
    }
}
