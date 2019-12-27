package services.chatMessage;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import entities.Account;
import entities.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import services.account.AccountRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Repository
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCus {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(ChatMessage chatMessage) {
        mongoTemplate.save(chatMessage, "messageCollection");
    }

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<ChatMessage> get(String firstUser, String secondUser, int firstElement, int lastElement) {
        try{
            List<ChatMessage> allChatMessages = new ArrayList<>();
            Account account = accountRepository.findAccountByName(firstUser);
            System.out.println("role cua m la :"+ account.getRole());
            System.out.println(firstUser + " " + secondUser);
            if ( account.getRole().equals("GUIDER")){
                allChatMessages =mongoTemplate.find(new Query(Criteria.where("guider").is(firstUser)
                        .andOperator(Criteria.where("traveler").is(secondUser))).
                        with(new Sort(Sort.Direction.DESC, "dateReceived")), ChatMessage.class,"messageCollection");
            }else {
                allChatMessages=mongoTemplate.find(new Query(Criteria.where("guider").is(secondUser)
                        .andOperator(Criteria.where("traveler").is(firstUser))).
                        with(new Sort(Sort.Direction.DESC, "dateReceived")), ChatMessage.class,"messageCollection");
            }
             int count = allChatMessages.size();
            System.out.println("%@#" + count);
            if (count >= lastElement) {
                allChatMessages.subList(firstElement, lastElement);
            }
            if (count < lastElement) {
                if (count < firstElement) {
                    System.out.println("null roi");
                    return new ArrayList<>();
                } else {
                    allChatMessages.subList(firstElement, count);
                }
            }
            return allChatMessages;
        }catch(Exception e ){
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

    }

    @Override
    public void updateSeen(String firstUser, String secondUser) {
        try{
            Account account = accountRepository.findAccountByName(firstUser);
            Query query = new Query();
            Query query2 = new Query();
            if ( account.getRole().equals("GUIDER")){
                query.addCriteria(Criteria.where("guider").is(firstUser).andOperator(Criteria.where("traveler").is(secondUser).andOperator(Criteria.where("isSeen").is(false))));
                query2.addCriteria(Criteria.where("guider").is(firstUser).andOperator(Criteria.where("traveler").is(secondUser).andOperator(Criteria.where("isSeen").is(false))));
            }else{
                query.addCriteria(Criteria.where("guider").is(secondUser).andOperator(Criteria.where("traveler").is(firstUser).andOperator(Criteria.where("isSeen").is(false))));
                query2.addCriteria(Criteria.where("guider").is(secondUser).andOperator(Criteria.where("traveler").is(firstUser).andOperator(Criteria.where("isSeen").is(false))));
            }
            query2.limit(1);
            query2.with(new Sort(Sort.Direction.DESC, "dateOfBirth"));
            List<ChatMessage> allChatmessages = mongoTemplate.find(query2, ChatMessage.class,"messageCollection");
            for (ChatMessage chatMessage:allChatmessages){
                if(chatMessage.getSender() != firstUser){
                    Update update = new Update();
                    update.set("isSeen", true);
                    mongoTemplate.updateMulti(query, update, ChatMessage.class,"messageCollection");
                }
            }

        }catch(Exception e){

        }

    }

    @Override
    public List<ChatMessage> getReceiver(String user, int firstElement, int lastElement) {
        try {
            List<ChatMessage> allChatMessages = new ArrayList<>();
            Account account = accountRepository.findAccountByName(user);
            if ( account.getRole().equals("GUIDER") ){
                allChatMessages =mongoTemplate.findDistinct(new Query(Criteria.where("guider").is(user)).
                        with(new Sort(Sort.Direction.ASC, "dateReceived")),"traveler","messageCollection",ChatMessage.class);
            }else{
                allChatMessages =mongoTemplate.findDistinct(new Query(Criteria.where("traveler").is(user)).
                        with(new Sort(Sort.Direction.ASC, "dateReceived")),"guider","messageCollection",ChatMessage.class);

            }

            /*DBCollection colllection = (DBCollection) mongoTemplate.getCollection("messageCollection");
            List<DBObject> pipeline = Arrays.<DBObject>asList(
                    new BasicDBObject("$match",new BasicDBObject("guider",user)),
                    new BasicDBObject("$group",
                            new BasicDBObject("_id","$traveler")
                                    .append("content",new BasicDBObject("$first","$content"))
                                    .append("dateReceived", new BasicDBObject("$first","$dateReceived"))
                    )
            );
            AggregationOutput output =  colllection.aggregate(pipeline);
            Iterable<DBObject> result = output.results();
            while(result.iterator().hasNext()){
                DBObject obj = result.iterator().next();
                String content = String.valueOf(obj.get("content"));
                //int times = Integer.parseInt(obj.get("times").toString());

                System.out.println(content);
            }*/
            Collections.reverse(allChatMessages);
            int count = allChatMessages.size();
            if ( count >= lastElement){
                allChatMessages.subList(firstElement,lastElement);
            }
            if ( count <lastElement){
                if ( count<firstElement){
                    return new ArrayList<>();
                }else{
                    allChatMessages.subList(firstElement,count);
                }
            }
            //System.out.println(allChatMessages.get(0).toString());
            //System.out.println(allChatMessages.get(0).getClass());
            return allChatMessages;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Scheduled(cron = "0 59 23 1/1 * *")
    public void putDataFromMongoToPostgres() throws ParseException {
        /*DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00");
        LocalDateTime now = LocalDateTime.now();
        Date startDate = new Date();
        Date endDate = new Date();

        startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dtf.format(now.minusDays(1)));
        endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dtf.format(now));

        System.out.println(startDate);
        System.out.println(endDate);
        List<ChatMessage> chatMessages = mongoTemplate.find(new Query(Criteria.where("dateReceived").gt(startDate).lt(endDate)), ChatMessage.class);
        for (ChatMessage chatMessage1 : chatMessages) {
            System.out.println(chatMessage1.getReceiver());
            System.out.println(chatMessage1.getId());
            System.out.println(chatMessage1.getContent());
            System.out.println(chatMessage1.getDateReceived());
        }*/

    }
}
