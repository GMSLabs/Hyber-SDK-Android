package com.hyber;

import java.io.Closeable;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;

/**
 * Class for receiving and storing received messages.
 * <p>
 * A repository is a potentially expensive resource to have in memory, so should be closed when no longer needed/used.
 *
 * @see <a href="http://martinfowler.com/eaaCatalog/repository.html">Repository pattern</a>
 */
class ReceivedMessageRepository implements Closeable {

    private final Realm mRealm;

    public ReceivedMessageRepository() {
        this.mRealm = Realm.getDefaultInstance();
    }

    public Observable<ReceivedMessage> saveMessage(final ReceivedMessage message) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Start saving message " + message.getId());
        return Observable.create(new Observable.OnSubscribe<ReceivedMessage>() {
            @Override
            public void call(final Subscriber<? super ReceivedMessage> subscriber) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            subscriber.onNext(realm.copyToRealm(message));
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void close() {
        // Remove the listeners
        //receivedMessages.removeChangeListener(receivedMessageChangeListener);
        // Close the Realm instance.
        mRealm.close();
    }

}
