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

package com.umbraltech.rxchange.adapter.collections;

import com.google.common.collect.ImmutableSet;
import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.HashSet;
import java.util.Set;

/**
 * An adapter that implements the reactive change model for sets
 *
 * @param <D> the type of data held by the set
 */
public class SetChangeAdapter<D> {
    private final PublishSubject<ChangeMessage<Set<D>>> publishSubject = PublishSubject.create();
    private final Set<D> dataSet = new HashSet<>();

    /**
     * Default constructor
     */
    public SetChangeAdapter() {
        // Stub
    }

    /**
     * Initializes the adapter with set of elements, without emitting a change message
     *
     * @param initialDataSet the initial set of elements
     */
    public SetChangeAdapter(final Set<D> initialDataSet) {
        dataSet.addAll(initialDataSet);
    }

    /**
     * Adds an element to the set and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain the element that was just added
     *
     * @param data the data to be added to the set
     * @return {@code true} if the element was added, {@code false} otherwise
     */
    public boolean add(final D data) {

        // Check if entry already exists
        if (dataSet.contains(data)) {
            return false;
        }

        final Set<D> oldSetSnapshot = ImmutableSet.copyOf(dataSet);
        dataSet.add(data);

        final Set<D> newSetSnapshot = ImmutableSet.copyOf(dataSet);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldSetSnapshot, newSetSnapshot, ChangeType.ADD, data));

        return true;
    }

    /**
     * Adds a collection of elements to the set and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain a snapshot
     * of the elements that were just added
     *
     * @param dataSet the set containing the data to be added
     * @return {@code true} if all of the elements were added, {@code false} otherwise
     */
    public boolean addAll(final Set<D> dataSet) {

        // Check if entries already exist
        if (this.dataSet.containsAll(dataSet)) {
            return false;
        }

        final Set<D> oldSetSnapshot = ImmutableSet.copyOf(this.dataSet);
        this.dataSet.addAll(dataSet);

        final Set<D> newSetSnapshot = ImmutableSet.copyOf(this.dataSet);
        final Set<D> changeSnapshot = ImmutableSet.copyOf(dataSet);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldSetSnapshot, newSetSnapshot, ChangeType.ADD,
                changeSnapshot));

        return true;
    }

    /**
     * Removes an element from the set and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain the element that was just removed
     *
     * @param data the data to be added to the set
     * @return {@code true} if the element was added, {@code false} otherwise
     */
    public boolean remove(final D data) {

        // Check if no entry to remove
        if (!dataSet.contains(data)) {
            return false;
        }

        final Set<D> oldSetSnapshot = ImmutableSet.copyOf(dataSet);
        dataSet.remove(data);

        final Set<D> newSetSnapshot = ImmutableSet.copyOf(dataSet);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldSetSnapshot, newSetSnapshot, ChangeType.REMOVE, data));

        return true;
    }

    /**
     * Removes a collection of elements from the set and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain a snapshot
     * of the elements that were just removed
     *
     * @param dataSet the set containing the data to be removed
     * @return {@code true} if all of the elements were removed, {@code false} otherwise
     */
    public boolean removeAll(final Set<D> dataSet) {

        // Check if entries do not exist
        if (!this.dataSet.containsAll(dataSet)) {
            return false;
        }

        final Set<D> oldSetSnapshot = ImmutableSet.copyOf(this.dataSet);
        this.dataSet.removeAll(dataSet);

        final Set<D> newSetSnapshot = ImmutableSet.copyOf(this.dataSet);
        final Set<D> changeSnapshot = ImmutableSet.copyOf(dataSet);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldSetSnapshot, newSetSnapshot, ChangeType.REMOVE,
                changeSnapshot));

        return true;
    }

    /**
     * Returns an immutable snapshot of the current set
     *
     * @return the set of elements
     */
    public Set<D> getAll() {
        return ImmutableSet.copyOf(dataSet);
    }

    /**
     * Returns a reference to the observable used for listening to change messages
     *
     * @return the observable reference
     */
    public Observable<ChangeMessage<Set<D>>> getObservable() {
        return publishSubject;
    }
}
