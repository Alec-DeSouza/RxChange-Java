package com.umbraltech.rxchange.adapter.collections;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.HashMap;
import java.util.Map;

/**
 * An adapter that implements the reactive change model for maps
 *
 * @param <K> the type of data used for the keys
 * @param <D> the type of data used for the values
 */
public class MapChangeAdapter<K, D> {
    private final PublishSubject<ChangeMessage<Map<K, D>>> publishSubject = PublishSubject.create();
    private final Map<K, D> dataMap = new HashMap<>();

    /**
     * Adds a key-value pair to the map and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain a snapshot
     * of the entry that was just added
     *
     * @param key  the key used for accessing the data
     * @param data the value associated with the key
     * @return {@code true} if the entry was added to the map, {@code false} otherwise
     */
    public boolean add(final K key, final D data) {

        // Check if entry already exists
        if (dataMap.containsKey(key)) {
            return false;
        }

        final Map<K, D> oldMapSnapshot = ImmutableMap.copyOf(dataMap);
        dataMap.put(key, data);

        final Map<K, D> newMapSnapshot = ImmutableMap.copyOf(dataMap);
        final Map.Entry<K, D> changeSnapshot = Maps.immutableEntry(key, data);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldMapSnapshot, newMapSnapshot, ChangeType.ADD,
                changeSnapshot));

        return true;
    }

    /**
     * Adds a collection of key-value pairs to the map and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain a snapshot
     * of the entries that were just added
     *
     * @param dataMap the map containing the entries to be added
     * @return {@code true} if all of the entries were added, {@code false} otherwise
     */
    public boolean add(final Map<K, D> dataMap) {

        // Check if entries already exist
        for (final K key : dataMap.keySet()) {
            if (this.dataMap.containsKey(key)) {
                return false;
            }
        }

        final Map<K, D> oldMapSnapshot = ImmutableMap.copyOf(this.dataMap);
        this.dataMap.putAll(dataMap);

        final Map<K, D> newMapSnapshot = ImmutableMap.copyOf(this.dataMap);
        final Map<K, D> changeSnapshot = ImmutableMap.copyOf(dataMap);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldMapSnapshot, newMapSnapshot, ChangeType.ADD,
                changeSnapshot));

        return true;
    }

    /**
     * Removes an entry specified by the key and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain a snapshot
     * of the entry that was just removed
     *
     * @param key the key associated with the entry to be removed
     * @return {@code true} if the entry was removed, {@code false} otherwise
     */
    public boolean remove(final K key) {

        // Check if no entry to remove
        if (!dataMap.containsKey(key)) {
            return false;
        }

        final Map<K, D> oldMapSnapshot = ImmutableMap.copyOf(dataMap);
        final D resultData = dataMap.remove(key);

        final Map<K, D> newMapSnapshot = ImmutableMap.copyOf(dataMap);
        final Map.Entry<K, D> changeSnapshot = Maps.immutableEntry(key, resultData);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldMapSnapshot, newMapSnapshot, ChangeType.REMOVE,
                changeSnapshot));

        return true;
    }

    /**
     * Removes a collection of entries from the map and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain a snapshot
     * of the entries that were just removed
     *
     * @param dataMap the map of the entries to be removed
     * @return {@code true} if all of the entries were removed, {@code false} otherwise
     */
    public boolean remove(final Map<K, D> dataMap) {

        // Check if no entries to remove
        for (final K key : dataMap.keySet()) {
            if (!this.dataMap.containsKey(key)) {
                return false;
            }
        }

        final Map<K, D> oldMapSnapshot = ImmutableMap.copyOf(this.dataMap);
        this.dataMap.entrySet().removeAll(dataMap.entrySet());

        final Map<K, D> newMapSnapshot = ImmutableMap.copyOf(this.dataMap);
        final Map<K, D> changeSnapshot = ImmutableMap.copyOf(dataMap);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldMapSnapshot, newMapSnapshot, ChangeType.REMOVE,
                changeSnapshot));

        return true;
    }

    /**
     * Updates the value of an entry in the map and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain a snapshot
     * of the entry that was just updated
     *
     * @param key  the key associated with the old data
     * @param data the new value stored with the key
     * @return {@code true} if the entry was updated, {@code false} otherwise
     */
    public boolean update(final K key, final D data) {

        // Check if entry does not exist
        if (!dataMap.containsKey(key)) {
            return false;
        }

        final Map<K, D> oldMapSnapshot = ImmutableMap.copyOf(dataMap);
        dataMap.put(key, data);

        final Map<K, D> newMapSnapshot = ImmutableMap.copyOf(dataMap);
        final Map.Entry<K, D> changeSnapshot = Maps.immutableEntry(key, data);

        // Signal update
        publishSubject.onNext(new MetaChangeMessage<>(oldMapSnapshot, newMapSnapshot, ChangeType.UPDATE,
                changeSnapshot));

        return true;
    }

    /**
     * Updates multiple entries in the map and emits a change message to surrounding observers
     * <p>
     * The metadata in the emitted change message will contain a snapshot
     * of the entries that were just updated
     *
     * @param dataMap the map containing the entries to be updated
     * @return {@code true} if all of the entries were updated, {@code false} otherwise
     */
    public boolean update(final Map<K, D> dataMap) {

        // Check if entries do not exist
        for (final K key : dataMap.keySet()) {
            if (!this.dataMap.containsKey(key)) {
                return false;
            }
        }

        final Map<K, D> oldMapSnapshot = ImmutableMap.copyOf(this.dataMap);
        this.dataMap.putAll(dataMap);

        final Map<K, D> newMapSnapshot = ImmutableMap.copyOf(this.dataMap);
        final Map<K, D> changeSnapshot = ImmutableMap.copyOf(dataMap);

        // Signal update
        publishSubject.onNext(new MetaChangeMessage<>(oldMapSnapshot, newMapSnapshot, ChangeType.UPDATE,
                changeSnapshot));

        return true;
    }

    /**
     * Returns the data of the entry specified by the key
     *
     * @param key the key used for the retrieval
     * @return the data associated with the key, null if not found
     */
    public D get(final K key) {
        if (!dataMap.containsKey(key)) {
            return null;
        }

        return dataMap.get(key);
    }

    /**
     * Returns an immutable snapshot of the current map
     *
     * @return the map of elements
     */
    public Map<K, D> getMap() {
        return ImmutableMap.copyOf(dataMap);
    }

    /**
     * Returns a reference to the observable used for listening to change messages
     *
     * @return the observable reference
     */
    public Observable<ChangeMessage<Map<K, D>>> getObservable() {
        return publishSubject;
    }
}
