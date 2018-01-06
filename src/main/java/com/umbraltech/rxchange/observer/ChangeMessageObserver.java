package com.umbraltech.rxchange.observer;

import com.umbraltech.rxchange.message.ChangeMessage;
import io.reactivex.observers.DefaultObserver;

/**
 * A utility class used by observers for overriding specific events
 *
 * @param <D> the type of data being observed
 */
public class ChangeMessageObserver<D> extends DefaultObserver<ChangeMessage<D>> {
    @Override
    public void onNext(ChangeMessage<D> changeMessage) {
        // Stub
    }

    @Override
    public void onComplete() {
        // Stub
    }

    @Override
    public void onError(Throwable throwable) {
        // Stub
    }
}
