package com.hyber.handler;

public interface HyberCallback<S, E> {

    void onSuccess(S result);

    void onFailure(E error);

}
