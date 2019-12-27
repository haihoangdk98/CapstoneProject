package services.chatMessage;

import entities.Account;
import entities.ChatMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import services.account.AccountRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChatMessageRepositoryImplTest {

    @InjectMocks
    ChatMessageRepositoryImpl chatMessageRepository;

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    AccountRepository accountRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void save() {
        chatMessageRepository.save(new ChatMessage());
    }

    @Test
    public void get() throws Exception {
        Account account  = new Account();
        account.setRole("TRAVELER");
        when(accountRepository.findAccountByName("abc")).thenReturn(account);

        Assert.assertEquals(new ArrayList<>(), chatMessageRepository.get("abc", "def", 1, 1));
    }

    @Test
    public void get2()throws Exception{
        List<ChatMessage> list = new ArrayList<>();
        ChatMessage msg = new ChatMessage();
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        Account account  = new Account();
        account.setRole("GUIDER");
        when(accountRepository.findAccountByName("abc")).thenReturn(account);
        when(mongoTemplate.find(new Query(Criteria.where("guider").is("abc")
                .andOperator(Criteria.where("traveler").is("def"))).
                with(new Sort(Sort.Direction.DESC, "dateReceived")), ChatMessage.class,"messageCollection")).thenReturn(list);
        Assert.assertEquals(6, chatMessageRepository.get("abc", "def", 1, 1).size());
    }

    @Test
    public void get3()throws Exception {
        List<ChatMessage> list = new ArrayList<>();
        ChatMessage msg = new ChatMessage();
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        Account account  = new Account();
        account.setRole("TRAVELER");
        when(accountRepository.findAccountByName("abc")).thenReturn(account);
        when(mongoTemplate.find(new Query(Criteria.where("guider").is("def")
                .andOperator(Criteria.where("traveler").is("abc"))).
                with(new Sort(Sort.Direction.DESC, "dateReceived")), ChatMessage.class,"messageCollection")).thenReturn(list);
        Assert.assertEquals(6, chatMessageRepository.get("abc", "def", 1, 9).size());
    }

    @Test
    public void getWithException()throws Exception {
        List<ChatMessage> list = new ArrayList<>();
        ChatMessage msg = new ChatMessage();
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        Account account  = new Account();
        account.setRole("TRAVELER");
        when(accountRepository.findAccountByName("abc")).thenThrow(Exception.class);
        Assert.assertEquals(0, chatMessageRepository.get("abc", "def", 1, 9).size());
    }



    @Test
    public void testGetReceiver() throws Exception{
        List<ChatMessage> list = new ArrayList<>();
        ChatMessage msg = new ChatMessage();
        msg.setGuider("abc");
        msg.setTraveler("xyz");
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        Account account  = new Account();
        account.setRole("TRAVELER");
        when(accountRepository.findAccountByName("abc")).thenReturn(account);
        when(mongoTemplate.findDistinct(new Query(Criteria.where("traveler").is("abc")).
                with(new Sort(Sort.Direction.ASC, "dateReceived")),"guider","messageCollection",ChatMessage.class)).thenReturn(list);
        List<ChatMessage> result = chatMessageRepository.getReceiver("abc",0,9);
        Assert.assertEquals(6,result.size());
    }

    @Test
    public void testGetReceiverWithCountBiggerThanLastElement() throws Exception{
        List<ChatMessage> list = new ArrayList<>();
        ChatMessage msg = new ChatMessage();
        msg.setGuider("abc");
        msg.setTraveler("xyz");
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        Account account  = new Account();
        account.setRole("GUIDER");
        when(accountRepository.findAccountByName("abc")).thenReturn(account);

        when(mongoTemplate.findDistinct(new Query(Criteria.where("guider").is("abc")).
                with(new Sort(Sort.Direction.ASC, "dateReceived")),"traveler","messageCollection",ChatMessage.class)).thenReturn(list);
        List<ChatMessage> result = chatMessageRepository.getReceiver("abc",0,5);
        Assert.assertEquals(6,result.size());
    }

    @Test
    public void testGetReceiverWithCountEqualLastElement() throws Exception{
        List<ChatMessage> list = new ArrayList<>();
        Account account  = new Account();
        account.setRole("GUIDER");
        when(accountRepository.findAccountByName("abc")).thenReturn(account);

        when(mongoTemplate.findDistinct(new Query(Criteria.where("guider").is("abc")).
                with(new Sort(Sort.Direction.ASC, "dateReceived")),"traveler","messageCollection",ChatMessage.class)).thenReturn(list);
        List<ChatMessage> result = chatMessageRepository.getReceiver("abc",0,6);
        Assert.assertEquals(new ArrayList<>(),result);
    }

    @Test
    public void testGetReceiverWithException() throws Exception{
        List<ChatMessage> list = new ArrayList<>();
        Account account  = new Account();
        account.setRole("GUIDER");
        when(accountRepository.findAccountByName("abc")).thenThrow(Exception.class);

        List<ChatMessage> result = chatMessageRepository.getReceiver("abc",0,6);
        Assert.assertEquals(new ArrayList<>(),result);
    }

    @Test
    public void testUpdateSeenWithGuider() throws Exception{
        Account account  = new Account();
        account.setRole("GUIDER");
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("guider").is("abc").andOperator(Criteria.where("traveler").is("xyz").andOperator(Criteria.where("isSeen").is(false))));
        query2.limit(1);
        query2.with(new Sort(Sort.Direction.DESC, "dateOfBirth"));
        List<ChatMessage> chatMessages= new ArrayList<>();
        ChatMessage chatMessage = Mockito.mock(ChatMessage.class);
        chatMessages.add(chatMessage);
        when(mongoTemplate.find(query2, ChatMessage.class,"messageCollection")).thenReturn(chatMessages);
        when(accountRepository.findAccountByName("abc")).thenReturn(account);

        chatMessageRepository.updateSeen("abc","xyz");
    }
    @Test
    public void testUpdateSeenWithGuiderWithException() throws Exception{
        Account account  = new Account();
        account.setRole("GUIDER");
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("guider").is("abc").andOperator(Criteria.where("traveler").is("xyz").andOperator(Criteria.where("isSeen").is(false))));
        query2.limit(1);
        query2.with(new Sort(Sort.Direction.DESC, "dateOfBirth"));
        List<ChatMessage> chatMessages= new ArrayList<>();
        ChatMessage chatMessage = Mockito.mock(ChatMessage.class);
        chatMessages.add(chatMessage);
        when(accountRepository.findAccountByName("abc")).thenThrow(Exception.class);

        chatMessageRepository.updateSeen("abc","xyz");
    }
    @Test
    public void testUpdateSeenWithTravler() throws Exception{
        Account account  = new Account();
        account.setRole("TRAVELER");
        when(accountRepository.findAccountByName("abc")).thenReturn(account);

        chatMessageRepository.updateSeen("abc","xyz");
    }

    /*@Test
    public void putDataFromMongoToPostgres() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00");
        List<ChatMessage> list = new ArrayList<>();
        ChatMessage msg = new ChatMessage();
        msg.setGuider("abc");
        msg.setId("1");
        msg.setContent("content");
        msg.setDateReceived(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dtf.format(LocalDateTime.now())));
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);
        list.add(msg);

        Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dtf.format(LocalDateTime.now().minusDays(1)));
        Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dtf.format(LocalDateTime.now()));
        //when(mongoTemplate.find(new Query(Criteria.where("dateReceived").gt(startDate).lt(endDate)), ChatMessage.class)).thenReturn(list);
        chatMessageRepository.putDataFromMongoToPostgres();
    }*/
}