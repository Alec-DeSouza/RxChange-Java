package adapter;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import message.ChangeMessage;
import message.MetaChangeMessage;
import type.ChangeType;

public class SingleChangeAdapter<D> {
    private final PublishSubject<ChangeMessage<D>> publishSubject = PublishSubject.create();
    private D data;

    public SingleChangeAdapter(final D data) {
        this.data = data;
    }

    public void update(final D data) {
        final D oldData = this.data;
        this.data = data;

        // Signal update
        publishSubject.onNext(new MetaChangeMessage<>(oldData, this.data, ChangeType.UPDATE, null));
    }

    public Observable<ChangeMessage<D>> getObservable() {
        return publishSubject;
    }
}
