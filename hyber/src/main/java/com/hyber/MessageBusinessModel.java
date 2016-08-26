package com.hyber;

import rx.Observable;

/**
 * Class for the business rules of the received messages.
 */
public class MessageBusinessModel {

    private static MessageBusinessModel mInstance;

    private final MessageRepository repository;

    static synchronized MessageBusinessModel getInstance() {
        if (mInstance == null) {
            mInstance = new MessageBusinessModel(new MessageRepository());
        }
        return mInstance;
    }

    static synchronized MessageBusinessModel newInstance() {
        return new MessageBusinessModel(new MessageRepository());
    }

    private MessageBusinessModel(MessageRepository repository) {
        this.repository = repository;
    }

    public Observable<Message> saveMessage(Message message) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Save new message " + message.getId());
        return repository.saveReceivedMessage(message);
    }

    protected void close() {
        repository.close();
    }

}
