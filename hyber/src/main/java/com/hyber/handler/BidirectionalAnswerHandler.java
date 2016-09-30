package com.hyber.handler;

public interface BidirectionalAnswerHandler {

    void onSuccess();

    void onFailure(String message);

}
