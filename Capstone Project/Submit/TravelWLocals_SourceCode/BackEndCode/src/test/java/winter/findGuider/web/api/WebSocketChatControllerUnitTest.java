package winter.findGuider.web.api;

import entities.ChatMessage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import services.chatMessage.ChatMessageRepositoryImpl;

import java.security.Principal;

public class WebSocketChatControllerUnitTest {
    @InjectMocks
    WebSocketChatController webSocketChatController;

    @Mock
    ChatMessageRepositoryImpl chatMessageRepository;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @Rule
    public ExpectedException thrown= ExpectedException.none();
    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendMessage(){
        ChatMessage chatMessage = Mockito.mock(ChatMessage.class);

        webSocketChatController.sendMessage(chatMessage);
    }
    @Test
    public void testSendMessageWithException(){
        ChatMessage chatMessage = Mockito.mock(ChatMessage.class);
        ReflectionTestUtils.setField(webSocketChatController, "chatMessageRepository", null);

        webSocketChatController.sendMessage(chatMessage);
    }
    @Test
    public void testGetMessage(){

        webSocketChatController.getMessage("dung","ha",1,10);
    }

    @Test
    public void testSendSeenMessage(){
        ChatMessage chatMessage = Mockito.mock(ChatMessage.class);
        webSocketChatController.sendSeenMessage(chatMessage);
    }

    @Test
    public void testGetMessageWithoutReceiver(){
        webSocketChatController.getReceiver("ha",1,10);
    }
}
