package com.umbraltech.rxchange.adapter.collections;

import com.google.common.collect.ImmutableList;
import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;

public class ListChangeAdapter<D> {
    private final PublishSubject<ChangeMessage<List<D>>> publishSubject = PublishSubject.create();
    private final List<D> dataList = new ArrayList<>();

    public boolean add(final D data) {
        final List<D> oldListSnapshot = ImmutableList.copyOf(dataList);
        dataList.add(data);

        final List<D> newListSnapshot = ImmutableList.copyOf(dataList);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.ADD, data));

        return true;
    }

    public boolean add(final List<D> dataList) {
        final List<D> oldListSnapshot = ImmutableList.copyOf(this.dataList);
        this.dataList.addAll(dataList);

        final List<D> newListSnapshot = ImmutableList.copyOf(this.dataList);
        final List<D> changeSnapshot = ImmutableList.copyOf(dataList);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.ADD,
                changeSnapshot));

        return true;
    }

    public boolean remove(final int index) {
        final List<D> oldListSnapshot = ImmutableList.copyOf(dataList);

        // Validate index
        if (index >= dataList.size()) {
            return false;
        }

        final D data = dataList.remove(index);
        final List<D> newListSnapshot = ImmutableList.copyOf(dataList);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.REMOVE, data));

        return true;
    }

    public boolean remove(final List<Integer> indexList) {
        final List<D> oldListSnapshot = ImmutableList.copyOf(dataList);
        final List<D> removedList = new ArrayList<>();

        for (final Integer i : indexList) {

            // Validate index
            if (i >= dataList.size()) {
                return false;
            }

            removedList.add(dataList.get(i));
        }

        dataList.removeAll(removedList);
        final List<D> newListSnapshot = ImmutableList.copyOf(dataList);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.REMOVE,
                removedList));

        return true;
    }

    public boolean update(final int index, final D data) {
        final List<D> oldListSnapshot = ImmutableList.copyOf(dataList);

        // Validate index
        if (index >= dataList.size()) {
            return false;
        }

        final D resultData = dataList.set(index, data);
        final List<D> newListSnapshot = ImmutableList.copyOf(dataList);

        // Signal update
        publishSubject.onNext(new MetaChangeMessage<>(oldListSnapshot, newListSnapshot, ChangeType.UPDATE, resultData));

        return true;
    }

    public D get(final int index) {
        return dataList.get(index);
    }

    public List<D> getList() {
        return ImmutableList.copyOf(dataList);
    }

    public Observable<ChangeMessage<List<D>>> getObservable() {
        return publishSubject;
    }
}
