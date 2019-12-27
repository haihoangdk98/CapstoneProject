package services.notification;

import entities.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCus {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Notification notification) {
        mongoTemplate.save(notification, "notiCollection");
    }

    @Override
    public List<Notification> get(String receiver, int firstElement, int lastElement) {
        List<Notification> allNotifications = mongoTemplate.find(new Query(Criteria.where("receiver").is(receiver)).
                with(new Sort(Sort.Direction.DESC, "dateReceived")), Notification.class,"notiCollection");

        int count = allNotifications.size();
        if (count >= lastElement) {
            allNotifications.subList(firstElement, lastElement);
        }
        if (count < lastElement) {
            if (count <= firstElement) {
                return null;
            } else {
                allNotifications.subList(firstElement, count);
            }
        }
        return allNotifications;
    }

    @Override
    public void updateSeen(String user) {
        Query query = new Query();
        query.addCriteria(Criteria.where("receiver").is(user).andOperator(Criteria.where("isSeen").is("false")));
        Update update = new Update();
        update.set("isSeen", true);
        mongoTemplate.updateMulti(query, update, Notification.class,"notiCollection");
    }
}
