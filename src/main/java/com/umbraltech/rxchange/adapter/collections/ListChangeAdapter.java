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

import com.google.common.collect.ImmutableList;
import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter that implements the reactive change model for lists
 *
 * @param <D> the type of data held by the list
 */
public class ListChangeAdapter<D> {
    private final PublishSubject<ChangeMessage<List<D>>> publishSubject = PublishSubject.create();
    private final List<D> dataList = new ArrayList<>();

    /**
     * Default constructor
     */
    public ListChangeAdapter() {
        // Stub
    }

    /**
     * Initializes the adapter with a list of elements, without emitting a change message
     *
     * @param initialDataList the initial list of elements
     */
    public ListChangeAdapter(final List<D> initialDataList) {
        dataList.addAll(initialDataList);
    }

    /**
     * Adds an element to the list and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain the element that was just added
     *
     * @param data the data to be added to the list
     * @return {@code true} always
     */
    public boolean add(final D data) {
        final List<D> oldListSnapshot = ImmutableList.copyOf(dataList);
        dataList.add(data);

        final List<D> newListSnapshot = ImmutableList.copyOf(dataList);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.ADD, data));

        return true;
    }

    /**
     * Adds an element to the list at the specified index and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain the element that was just added
     *
     * @param index the index at which the element is added
     * @param data  the data to be added to the list
     * @return {@code true} if the data was added to the list, {@code false} otherwise
     */
    public boolean addAt(final int index, final D data) {

        // Validate index
        if (index < 0 || index > dataList.size()) {
            return false;
        }

        final List<D> oldListSnapshot = ImmutableList.copyOf(dataList);
        dataList.add(index, data);

        final List<D> newListSnapshot = ImmutableList.copyOf(dataList);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.ADD, data));

        return true;
    }

    /**
     * Adds a list of elements and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain a snapshot of the list
     * of elements that were just added
     *
     * @param dataList the list of elements to be added
     * @return {@code true} always
     */
    public boolean addAll(final List<D> dataList) {
        final List<D> oldListSnapshot = ImmutableList.copyOf(this.dataList);
        this.dataList.addAll(dataList);

        final List<D> newListSnapshot = ImmutableList.copyOf(this.dataList);
        final List<D> changeSnapshot = ImmutableList.copyOf(dataList);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.ADD,
                changeSnapshot));

        return true;
    }

    /**
     * Removes the specified element and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain the element that was just removed
     *
     * @param data the element to be removed
     * @return {@code true} if the element was removed, {@code false} otherwise
     */
    public boolean remove(final D data) {

        // Validate item
        if (!dataList.contains(data)) {
            return false;
        }

        final List<D> oldListSnapshot = ImmutableList.copyOf(dataList);
        dataList.remove(data);

        final List<D> newListSnapshot = ImmutableList.copyOf(dataList);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.REMOVE, data));

        return true;
    }

    /**
     * Removes the element at a specified index and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain the element that was just removed
     *
     * @param index the index of the element to be removed
     * @return {@code true} if the element was removed, {@code false} otherwise
     */
    public boolean removeAt(final int index) {

        // Validate index
        if ((index < 0) || (index >= dataList.size())) {
            return false;
        }

        final List<D> oldListSnapshot = ImmutableList.copyOf(dataList);
        final D data = dataList.remove(index);

        final List<D> newListSnapshot = ImmutableList.copyOf(dataList);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.REMOVE, data));

        return true;
    }

    /**
     * Removes the specified list of elements and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain the collection that was just removed
     *
     * @param dataList the list of elements to be removed
     * @return {@code true} if all of the elements removed, {@code false} otherwise
     */
    public boolean removeAll(final List<D> dataList) {

        // Validate items
        if (!this.dataList.containsAll(dataList)) {
            return false;
        }

        final List<D> oldListSnapshot = ImmutableList.copyOf(this.dataList);
        this.dataList.removeAll(dataList);

        final List<D> newListSnapshot = ImmutableList.copyOf(this.dataList);
        final List<D> changeSnapshot = ImmutableList.copyOf(dataList);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.REMOVE,
                changeSnapshot));

        return true;
    }

    /**
     * Updates the element at the specified index with new data
     * <p>
     * The metadata in the emitted change message will contain the value
     * of the element that was just updated
     *
     * @param index the index of the element within the list
     * @param data  the new data for the element
     * @return {@code true} if the element was updated, {@code false} otherwise
     */
    public boolean update(final int index, final D data) {

        // Validate index
        if ((index < 0) || (index >= dataList.size())) {
            return false;
        }

        final List<D> oldListSnapshot = ImmutableList.copyOf(dataList);
        dataList.set(index, data);

        final List<D> newListSnapshot = ImmutableList.copyOf(dataList);

        // Signal update
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.UPDATE, data));

        return true;
    }

    /**
     * Returns the value of the element within the list
     *
     * @param index the index of the element
     * @return the element at the specified index
     */
    public D get(final int index) {
        return dataList.get(index);
    }

    /**
     * Returns an immutable snapshot of the current list
     *
     * @return the list of elements
     */
    public List<D> getAll() {
        return ImmutableList.copyOf(dataList);
    }

    /**
     * Returns a reference to the observable used for listening to change messages
     *
     * @return the observable reference
     */
    public Observable<ChangeMessage<List<D>>> getObservable() {
        return publishSubject;
    }
}
