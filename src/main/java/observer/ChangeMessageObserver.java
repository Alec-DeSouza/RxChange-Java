package observer;

import io.reactivex.observers.DefaultObserver;
import message.ChangeMessage;

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
