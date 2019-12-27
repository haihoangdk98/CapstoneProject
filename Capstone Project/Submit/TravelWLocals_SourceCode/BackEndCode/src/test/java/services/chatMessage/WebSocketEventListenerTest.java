package services.chatMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketEventListenerTest {

    @InjectMocks
    WebSocketEventListener webSocketEventListener;

    @Mock
    SessionConnectedEvent connectedEvent;

    @Mock
    SessionDisconnectEvent disconnectEvent;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleWebSocketConnectListener() {
        webSocketEventListener.handleWebSocketConnectListener(connectedEvent);
    }

    /*@Test
    public void handleWebSocketDisconnectListener() {
        StompHeaderAccessor headerAccessor = null;
        when(StompHeaderAccessor.wrap(disconnectEvent.getMessage())).thenReturn(headerAccessor);
        when(headerAccessor.getSessionAttributes().get("user")).thenReturn("user_name");
        webSocketEventListener.handleWebSocketDisconnectListener(disconnectEvent);
    }*/

    @Test
    public void handleSessionConnectedEvent() {
        webSocketEventListener.handleSessionConnectedEvent(connectedEvent);
    }
}