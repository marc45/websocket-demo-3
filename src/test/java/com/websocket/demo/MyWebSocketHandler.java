package com.websocket.demo;

import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class MyWebSocketHandler extends StompSessionHandlerAdapter {

    @Override
    public void afterConnected(StompSession stompSession, StompHeaders connectedHeaders) {
		System.out.println("Connected to socket with stomp session: " + stompSession.getSessionId());
    }
}