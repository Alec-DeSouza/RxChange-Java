package adapter.collections;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import message.ChangeMessage;
import message.MetaChangeMessage;
import type.ChangeType;

import java.util.ArrayList;
import java.util.List;

public class ListChangeAdapter<D> {
    private final PublishSubject<ChangeMessage<List<D>>> publishSubject = PublishSubject.create();
    private final List<D> dataList = new ArrayList<>();

    public Integer add(final D data) {
        final List<D> oldDataList = new ArrayList<>(dataList);
        dataList.add(data);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldDataList, dataList, ChangeType.ADD, data));

        return oldDataList.size();
    }

    public D remove(final Integer index) {
        final List<D> oldDataList = new ArrayList<>(dataList);
        final D data = dataList.remove(index.intValue());

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldDataList, dataList, ChangeType.REMOVE, data));

        return data;
    }

    public D update(final Integer index, final D data) {
        final List<D> oldDataList = new ArrayList<>(dataList);
        final D resultData = dataList.set(index, data);

        // Signal update
        publishSubject.onNext(new MetaChangeMessage<>(oldDataList, dataList, ChangeType.UPDATE, resultData));

        return resultData;
    }

    public D get(final Integer index) {
        return dataList.get(index);
    }

    public List<D> getAll() {
        return dataList;
    }

    public Observable<ChangeMessage<List<D>>> getObservable() {
        return publishSubject;
    }
}
