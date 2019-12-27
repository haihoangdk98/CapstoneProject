package services.notification;

import entities.ChatMessage;
import entities.Notification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class NotificationRepositoryImplTest {
    @InjectMocks
    NotificationRepositoryImpl notificationRepository;
    @Mock
    MongoTemplate mongoTemplate;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetNoti(){
        List<Notification> list = new ArrayList<>();
        Notification noti = new Notification();
        list.add(noti);
        list.add(noti);
        list.add(noti);
        list.add(noti);
        list.add(noti);
        list.add(noti);

        when(mongoTemplate.find(new Query(Criteria.where("receiver").is("abc")).
                with(new Sort(Sort.Direction.DESC, "dateReceived")), Notification.class,"notiCollection")).thenReturn(list);
        List<Notification> result = notificationRepository.get("abc",0,9);

        Assert.assertEquals(6, notificationRepository.get("abc", 0, 9 ).size());

    }

    @Test
    public void testGetNotiWithALlNotiGreterThanLastElement(){
        List<Notification> list = new ArrayList<>();
        Notification noti = new Notification();
        list.add(noti);
        list.add(noti);
        list.add(noti);
        list.add(noti);
        list.add(noti);
        list.add(noti);

        when(mongoTemplate.find(new Query(Criteria.where("receiver").is("abc")).
                with(new Sort(Sort.Direction.DESC, "dateReceived")), Notification.class,"notiCollection")).thenReturn(list);
        List<Notification> result = notificationRepository.get("abc",0,5);
        Assert.assertEquals(6, result.size());

    }

    @Test
    public void testGetNotiWithALlNotiSmallerThanLastElement(){
        List<Notification> list = new ArrayList<>();
        Notification noti = new Notification();
        list.add(noti);
        list.add(noti);
        list.add(noti);
        list.add(noti);
        list.add(noti);
        list.add(noti);

        when(mongoTemplate.find(new Query(Criteria.where("receiver").is("abc")).
                with(new Sort(Sort.Direction.DESC, "dateReceived")), Notification.class,"notiCollection")).thenReturn(list);
        List<Notification> result = notificationRepository.get("abc",6,9);

        Assert.assertEquals(null, result);

    }

    @Test
    public void testUpdateSeen(){
        notificationRepository.updateSeen("abc");
    }

    @Test
    public void testSave(){
        Notification notification = new Notification();
        notificationRepository.save(notification);
    }
}
