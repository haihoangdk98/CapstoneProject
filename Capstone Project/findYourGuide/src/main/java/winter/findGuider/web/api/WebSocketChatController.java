    package winter.findGuider.web.api;

import entities.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import services.chatMessage.ChatMessageRepositoryImpl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
@Controller
public class WebSocketChatController {

    @Autowired
    private ChatMessageRepositoryImpl chatMessageRepository;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/chat.sendMessage")
    public void sendMessage( @Payload ChatMessage chatMessage) {
        try {
            chatMessageRepository.save(chatMessage);
            this.simpMessagingTemplate.convertAndSendToUser(chatMessage.getGuider(),"/queue/reply", chatMessage);
            this.simpMessagingTemplate.convertAndSendToUser(chatMessage.getTraveler(),"/queue/reply", chatMessage);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @MessageMapping("chat.sendSeen")
    public void sendSeenMessage( @Payload ChatMessage chatMessage){
        chatMessageRepository.updateSeen(chatMessage.getGuider(),chatMessage.getTraveler());
        //this.simpMessagingTemplate.convertAndSendToUser(chatMessage.get(),"/queue/reply", chatMessage);
    }

    @RequestMapping(value = "/messages/{firstuser}/{seconduser}/{firstElement}/{lastElement}", method = RequestMethod.GET)
    public HttpEntity getMessage(@PathVariable("firstuser") String firstUser,@PathVariable("seconduser") String secondUser,@PathVariable("firstElement") int firstElement,@PathVariable("lastElement") int lastElement){
        chatMessageRepository.updateSeen(firstUser,secondUser);
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages = chatMessageRepository.get(firstUser,secondUser,firstElement,lastElement);
        return new ResponseEntity(chatMessages, HttpStatus.OK);
    }

    @RequestMapping(value = "/messages/{user}/{firstElement}/{lastElement}", method = RequestMethod.GET)
    public HttpEntity getReceiver(@PathVariable("user") String user,@PathVariable("firstElement") int firstElement,@PathVariable("lastElement") int lastElement){
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages = chatMessageRepository.getReceiver(user,firstElement,lastElement);
        return new ResponseEntity(chatMessages, HttpStatus.OK);
    }
}