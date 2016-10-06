package com.hyber;

import rx.Observable;

/**
 * Class for the business rules of the received messages.
 */
final class MessageBusinessModel {

    private static MessageBusinessModel mInstance;

    private final MessageRepository repository;

    private MessageBusinessModel(MessageRepository repository) {
        this.repository = repository;
    }

    static synchronized MessageBusinessModel getInstance() {
        if (mInstance == null) {
            mInstance = new MessageBusinessModel(new MessageRepository());
        }
        return mInstance;
    }

    static synchronized MessageBusinessModel newInstance() {
        return new MessageBusinessModel(new MessageRepository());
    }

    Observable<Message> saveMessage(Message message) {
        HyberLogger.i("Save new message %s", message.getId());
        return repository.saveReceivedMessage(message);
    }

    protected void close() {
        repository.close();
    }

}
