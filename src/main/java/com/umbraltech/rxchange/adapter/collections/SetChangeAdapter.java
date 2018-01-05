package com.umbraltech.rxchange.adapter.collections;

import com.google.common.collect.ImmutableSet;
import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.HashSet;
import java.util.Set;

public class SetChangeAdapter<D> {
    private final PublishSubject<ChangeMessage<Set<D>>> publishSubject = PublishSubject.create();
    private final Set<D> dataSet = new HashSet<>();

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

    public boolean add(final Set<D> dataSet) {

        // Check if entries already exist
        for (final D data : dataSet) {
            if (this.dataSet.contains(data)) {
                return false;
            }
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

    public boolean remove(final Set<D> dataSet) {

        // Check if entries do not exist
        for (final D data : dataSet) {
            if (!this.dataSet.contains(data)) {
                return false;
            }
        }

        final Set<D> oldSetSnapshot = ImmutableSet.copyOf(this.dataSet);
        this.dataSet.removeAll(dataSet);

        final Set<D> newSetSnapshot = ImmutableSet.copyOf(this.dataSet);
        final Set<D> changeSnapshot = ImmutableSet.copyOf(dataSet);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldSetSnapshot, newSetSnapshot, ChangeType.REMOVE,
                changeSnapshot));

        return true;
    }

    public Set<D> getSet() {
        return ImmutableSet.copyOf(dataSet);
    }

    public Observable<ChangeMessage<Set<D>>> getObservable() {
        return publishSubject;
    }
}
