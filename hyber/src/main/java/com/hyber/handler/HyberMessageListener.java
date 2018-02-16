package com.hyber.handler;

import com.hyber.HyberMessage;

public interface HyberMessageListener {

    void onMessageReceived(HyberMessage message);

}
