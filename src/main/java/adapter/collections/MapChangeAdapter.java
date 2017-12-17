package adapter.collections;

import com.google.common.collect.Maps;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import message.ChangeMessage;
import message.MetaChangeMessage;
import type.ChangeType;

import java.util.HashMap;
import java.util.Map;

public class MapChangeAdapter<K, D> {
    private final PublishSubject<ChangeMessage<Map<K, D>>> publishSubject = PublishSubject.create();
    private final Map<K, D> dataMap = new HashMap<>();

    public D add(K key, D data) {

        // Check if updating existing entry
        if (dataMap.containsKey(key)) {
            return update(key, data);
        }

        final Map<K, D> oldDataMap = new HashMap<>(dataMap);
        final D resultData = dataMap.put(key, data);

        // Signal addition
        publishSubject.onNext(new MetaChangeMessage<>(oldDataMap, dataMap, ChangeType.ADD,
                Maps.immutableEntry(key, data)));

        return resultData;
    }

    public D remove(K key) {

        // Check if no entry to remove
        if (!dataMap.containsKey(key)) {
            return null;
        }

        final Map<K, D> oldDataMap = new HashMap<>(dataMap);
        final D resultData = dataMap.remove(key);

        // Signal removal
        publishSubject.onNext(new MetaChangeMessage<>(oldDataMap, dataMap, ChangeType.REMOVE,
                Maps.immutableEntry(key, resultData)));

        return resultData;
    }

    public D update(K key, D data) {

        // Check if adding new entry
        if (!dataMap.containsKey(key)) {
            return add(key, data);
        }

        final Map<K, D> oldDataMap = new HashMap<>(dataMap);
        final D resultData = dataMap.put(key, data);

        // Signal update
        publishSubject.onNext(new MetaChangeMessage<>(oldDataMap, dataMap, ChangeType.UPDATE,
                Maps.immutableEntry(key, data)));

        return resultData;
    }

    public D get(K key) {
        if (!dataMap.containsKey(key)) {
            return null;
        }

        return dataMap.get(key);
    }

    public Map<K, D> getAll() {
        return dataMap;
    }

    public Observable<ChangeMessage<Map<K, D>>> getObservable() {
        return publishSubject;
    }
}
