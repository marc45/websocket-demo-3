package com.websocket.demo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
public class WebsocketDemoAcceptanceTests {
	StompSession stompSession1;
	List<Greeting> greetingsList = new ArrayList<>();
	
	@Before
	public void setUp() throws Exception {
		List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		
		WebSocketClient webSocketClient = new SockJsClient(transports);
		WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		ListenableFuture<StompSession> stompSessionFuture = stompClient.connect("ws://localhost:8080/gs-guide-websocket", new MyWebSocketHandler());
		stompSession1 = stompSessionFuture.get();
        stompSession1.subscribe("/topic/greetings", new StompFrameHandler() {
        	
		    @Override
		    public Type getPayloadType(StompHeaders headers) {
		        return Greeting.class;
		    }

		    @Override
		    public void handleFrame(StompHeaders headers, Object payload) {
				greetingsList.add((Greeting) payload);
		    }
		    
		});
	}

	@Test
	public void should_receiveMessageFromBroker_when_messageIsConsumedFromASubscribedTopic() throws InterruptedException {
		stompSession1.send("/app/hello", "Shoabe");
		Thread.sleep(2000);
		
		assertThatMessageReceivedFromBroker("Shoabe");	
	}
	
	private boolean assertThatMessageReceivedFromBroker(String name) {
		for(Greeting greeting : greetingsList) {
			if (greeting.getContent().contains(name)) {
				return true;
			}
		}
		return false;
	}

}
