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

public class MapChangeAdapter<K, D> {
    private final PublishSubject<ChangeMessage<Map<K, D>>> publishSubject = PublishSubject.create();
    private final Map<K, D> dataMap = new HashMap<>();

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

    public boolean add(final Map<K, D> dataMap) {

        // Check if entries already exists
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

    public D get(final K key) {
        if (!dataMap.containsKey(key)) {
            return null;
        }

        return dataMap.get(key);
    }

    public Map<K, D> getMap() {
        return ImmutableMap.copyOf(dataMap);
    }

    public Observable<ChangeMessage<Map<K, D>>> getObservable() {
        return publishSubject;
    }
}
