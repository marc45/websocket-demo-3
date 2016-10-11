package com.websocket.demo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@RunWith(SpringRunner.class)
public class BatchTest {

	@Test
	public void should_receiveAndSendManyMessagesToBroker_when_manyClientsSendMessages() throws Exception {
		List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		WebSocketClient webSocketClient = new SockJsClient(transports);
		WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		
		ListenableFuture<StompSession> stompSessionFuture2 = stompClient.connect("ws://localhost:8080/gs-guide-websocket", new MyWebSocketHandler());
		StompSession stompSession2 = stompSessionFuture2.get();
        
        ListenableFuture<StompSession> stompSessionFuture3 = stompClient.connect("ws://localhost:8080/gs-guide-websocket", new MyWebSocketHandler());
        StompSession stompSession3 = stompSessionFuture3.get();
        
        for(int i = 1; i <= 5; i++) {
        	stompSession2.send("/app/hello", "lol" + i);
        	stompSession3.send("/app/hello", "yolo" + i);
        }
	}
	
}
