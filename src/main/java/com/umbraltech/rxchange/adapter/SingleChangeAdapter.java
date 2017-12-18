package com.umbraltech.rxchange.adapter;

import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.message.MetaChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SingleChangeAdapter<D> {
    private final PublishSubject<ChangeMessage<D>> publishSubject = PublishSubject.create();
    private D data;

    public SingleChangeAdapter(final D data) {
        this.data = data;
    }

    public boolean update(final D data) {
        final D oldData = this.data;
        this.data = data;

        // Signal update
        publishSubject.onNext(new MetaChangeMessage<>(oldData, this.data, ChangeType.UPDATE, null));

        return true;
    }

    public Observable<ChangeMessage<D>> getObservable() {
        return publishSubject;
    }
}
