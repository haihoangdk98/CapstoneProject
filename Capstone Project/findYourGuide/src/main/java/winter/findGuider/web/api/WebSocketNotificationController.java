package winter.findGuider.web.api;

import entities.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import services.notification.NotificationRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
@RestController
public class WebSocketNotificationController {
    @Autowired
    private NotificationRepositoryImpl notificationRepositoryImpl;

    private  Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;



    @MessageMapping("/exchange.sendNotification")
    public void sendMessage( @Payload Notification notification) {
        notificationRepositoryImpl.save(notification);
        this.simpMessagingTemplate.convertAndSendToUser(notification.getReceiver(), "/queue/reply", notification);
    }

    @RequestMapping(value = "/notification/{receiver}/{firstElement}/{lastElement}", method = RequestMethod.POST)
    public HttpEntity getMessage( @PathVariable("receiver") String receiver, @PathVariable("firstElement") int firstElement, @PathVariable("lastElement") int lastElement){
        List<Notification> notifications = new ArrayList<>();

        notificationRepositoryImpl.updateSeen(receiver);
        notifications = notificationRepositoryImpl.get(receiver,firstElement,lastElement);

        return new ResponseEntity(notifications, HttpStatus.OK);
    }
}
