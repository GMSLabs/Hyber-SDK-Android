package com.hyber;

import rx.Observable;

/**
 * Class for the business rules of the received messages.
 */
public class ReceivedMessageBusinessModel {

    private static ReceivedMessageBusinessModel mInstance;

    private final ReceivedMessageRepository repository;

    static synchronized ReceivedMessageBusinessModel getInstance() {
        if (mInstance == null) {
            mInstance = new ReceivedMessageBusinessModel(new ReceivedMessageRepository());
        }
        return mInstance;
    }

    static synchronized ReceivedMessageBusinessModel newInstance() {
        return new ReceivedMessageBusinessModel(new ReceivedMessageRepository());
    }

    private ReceivedMessageBusinessModel(ReceivedMessageRepository repository) {
        this.repository = repository;
    }

    public Observable<ReceivedMessage> saveMessage(ReceivedMessage message) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Save new message " + message.getId());
        return repository.saveMessage(message);
    }

    protected void close() {
        repository.close();
    }

}
