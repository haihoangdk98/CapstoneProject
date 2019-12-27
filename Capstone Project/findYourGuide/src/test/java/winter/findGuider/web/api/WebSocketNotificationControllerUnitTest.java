package winter.findGuider.web.api;

import entities.ChatMessage;
import entities.Notification;
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
import services.notification.NotificationRepositoryImpl;

import java.security.Principal;

public class WebSocketNotificationControllerUnitTest {
    @InjectMocks
    WebSocketNotificationController webSocketNotificationController;

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    NotificationRepositoryImpl notificationRepository;

    @Rule
    public ExpectedException thrown= ExpectedException.none();
    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendMessage(){
        Notification notification = Mockito.mock(Notification.class);

        webSocketNotificationController.sendMessage(notification);
    }

    @Test
    public void testGetMessage(){
        /*Principal principal= Mockito.mock(Principal.class);
        ChatMessage chatMessage = Mockito.mock(ChatMessage.class);
        SimpMessageHeaderAccessor headerAccessor = Mockito.mock(SimpMessageHeaderAccessor.class);
*/
        webSocketNotificationController.getMessage("ha",1,10);
    }
}
