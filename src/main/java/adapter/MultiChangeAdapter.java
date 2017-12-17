package adapter;

import io.reactivex.Observable;
import message.ChangeMessage;

public interface MultiChangeAdapter<C, K, D> {
    K add(D data);

    D remove(K key);

    D update(K key, D data);

    D get(K key);

    C getAll();

    Observable<ChangeMessage<C>> getObservable();
}
