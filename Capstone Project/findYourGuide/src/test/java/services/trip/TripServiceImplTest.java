
package services.trip;

import entities.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import services.Paypal.PaypalService;
import winter.findGuider.TestDataSourceConfig;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class TripServiceImplTest {

    @InjectMocks
    TripServiceImpl tripService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Mock
    PaypalService paypalService;

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        tripService = new TripServiceImpl(jdbcTemplate);
        ReflectionTestUtils.setField(tripService, "bufferPercent", "30");
        config.cleanTestDb(jdbcTemplate);
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (1,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (2,'Megan','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','TRAVELER')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (1,'Vietnam','Hanoi','Hoan Kiem')");
        jdbcTemplate.update("insert into category (category_id,name,image) values (1,'history','a.jpg')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (1,'John','Doe',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        jdbcTemplate.update("insert into contract_detail (contract_id,guider_id,name,nationality,date_of_birth,gender,hometown,address,identity_card_number,card_issued_date,card_issued_province,account_active_date)" +
                "values (1,1,'John Doe','Vietnamese','1993-06-05',1,'Hanoi','a','123456','2000-04-05','Hanoi','2016-10-15')");
        jdbcTemplate.update("insert into contract (guider_id,contract_id) values (1,1)");
        jdbcTemplate.update("insert into traveler (traveler_id, first_name, last_name, phone, gender, date_of_birth, street, house_number, postal_code, slogan, about_me, language, country, city, avatar_link)" +
                "values (2,'Megan','Deo','123',2,'1996-02-13','a','12','12','a','a','{en,vi}','vietnam','hanoi','a')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (1,1,1,1,'test post','a','{a}',2,'a','{a,b}',true,10,5,'abc')");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createOrder() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        Order order = new Order(2, 1, LocalDateTime.now(), LocalDateTime.now(),
                1, 1, 120, "abc", null);
        tripService.createTrip(order);
        int result = jdbcTemplate.queryForObject("select count(trip_id) from trip", new Object[]{}, int.class);
        Assert.assertEquals(1, result);
    }

    @Test
    public void findOrderById() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        Assert.assertEquals("abc", tripService.findTripById(1).getTransaction_id());
    }

    @Test
    public void findTripByStatus() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','WAITING')");
        Assert.assertEquals(1, tripService.findTripByStatus("guider", 1, "WAITING", 0).get(0).gettrip_id());
    }

    @Test
    public void findTripByStatus2() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','WAITING')");
        Assert.assertEquals(1, tripService.findTripByStatus("traveler", 2, "WAITING", 0).get(0).gettrip_id());
    }

    @Test(expected = Exception.class)
    public void findTripByStatus3() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','WAITING')");
        Assert.assertTrue(tripService.findTripByStatus("none", 1, "WAITING", 0).isEmpty());
    }

    @Test
    public void acceptOrder() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','WAITING')");
        Assert.assertEquals(true, tripService.acceptTrip(1));
    }

    @Test
    public void acceptOrder2() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','WAITING')");
        Assert.assertEquals(false, tripService.acceptTrip(2));
    }

    @Test
    public void cancelOrder() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','WAITING')");
        Assert.assertEquals(true, tripService.cancelTrip(1));
    }

    @Test
    public void cancelOrder2() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','WAITING')");
        Assert.assertEquals(false, tripService.cancelTrip(2));
    }

    @Test
    public void finishOrder() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','ONGOING')");
        Assert.assertEquals(true, tripService.finishTrip(1));
    }

    @Test
    public void finishOrder2() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','WAITING')");
        Assert.assertEquals(false, tripService.finishTrip(1));
    }

    @Test
    public void getOrderGuiderId_FinishDate() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        Order order = new Order();
        order.setTraveler_id(2);
        order.setPost_id(1);
        order.setAdult_quantity(1);
        order.setChildren_quantity(1);
        order.setBegin_date(LocalDateTime.now());
        order.setFee_paid(150);
        tripService.getTripGuiderId_FinishDate(order);
        Assert.assertEquals(1, order.getGuider_id());
    }

    @Test
    public void checkOrderExist() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','ONGOING')");
        Assert.assertEquals(1, tripService.checkTripExist(1));
    }

    @Test
    public void checkAvailabilityOfOrder() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','ONGOING')");
        Order order = new Order();
        order.setGuider_id(1);
        order.setBegin_date(LocalDateTime.parse("2019-11-22T02:30"));
        order.setFinish_date(LocalDateTime.parse("2019-11-23T06:00"));
        Assert.assertEquals(1, tripService.checkAvailabilityOfTrip(order));
    }

    @Test
    public void getGuiderAvailableHours() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T05:30','2019-11-22T07:00',1,1,150,'abc','ONGOING')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('a1','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (2,2,1,'2019-11-22T23:30','2019-11-23T01:00',1,1,150,'a1','ONGOING')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('a3','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (5,2,1,'2019-11-23T08:00','2019-11-23T10:00',1,1,150,'a3','ONGOING')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('a4','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (6,2,1,'2019-11-23T10:30','2019-11-23T12:30',1,1,150,'a4','ONGOING')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('def','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (3,2,1,'2019-11-23T05:30','2019-11-23T07:00',1,1,150,'def','ONGOING')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('a2','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (4,2,1,'2019-11-23T23:30','2019-11-24T01:00',1,1,150,'a2','ONGOING')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('a5','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (7,2,1,'2019-11-23T01:30','2019-11-23T03:00',1,1,150,'a5','ONGOING')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('a6','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (8,2,1,'2019-11-23T03:30','2019-11-23T04:30',1,1,150,'a6','ONGOING')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('a7','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (9,2,1,'2019-11-23T19:30','2019-11-23T21:30',1,1,150,'a7','ONGOING')");
        Assert.assertEquals(10, tripService.getGuiderAvailableHours(LocalDate.parse("2019-11-23"), 1, 1).size());
    }

    @Test
    public void getGuiderAvailableHours2() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T05:30','2019-11-25T07:00',1,1,150,'abc','ONGOING')");
        Assert.assertEquals(0, tripService.getGuiderAvailableHours(LocalDate.parse("2019-11-23"), 1, 1).size());
    }

    @Test
    public void getClosestTourFinishDate() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T05:30','2019-11-22T07:00',1,1,150,'abc','FINISHED')");
        Assert.assertEquals("11/22/2019 07:00", tripService.getClosestTripFinishDate(LocalDate.parse("2019-11-24"), 1));
    }

    @Test
    public void getClosestTourFinishDate2() throws Exception {
        Assert.assertEquals("a very long time ago", tripService.getClosestTripFinishDate(LocalDate.parse("2019-11-24"), 1));
    }

    @Test
    public void checkOrderReach24Hours() throws Exception {
        Order order = new Order();
        order.setBegin_date(LocalDateTime.parse("2019-11-29T00:00"));
        LocalDateTime rightNow = LocalDateTime.parse("2019-11-27T00:30");
        Assert.assertEquals(false, tripService.checkTripReach24Hours(order, rightNow));
    }

    @Test
    public void checkOrderReach24Hours2() throws Exception {
        Order order = new Order();
        order.setBegin_date(LocalDateTime.parse("2019-11-29T00:00"));
        LocalDateTime rightNow = LocalDateTime.parse("2019-11-29T00:30");
        Assert.assertEquals(true, tripService.checkTripReach24Hours(order, rightNow));
    }

    @Test
    public void checkOrderReach24Hours3() throws Exception {
        Order order = new Order();
        order.setBegin_date(LocalDateTime.parse("2019-11-29T05:30"));
        LocalDateTime rightNow = LocalDateTime.parse("2019-11-28T04:30");
        Assert.assertEquals(false, tripService.checkTripReach24Hours(order, rightNow));
    }

    @Test
    public void checkOrderReach24Hours4() throws Exception {
        Order order = new Order();
        order.setBegin_date(LocalDateTime.parse("2019-11-29T05:30"));
        LocalDateTime rightNow = LocalDateTime.parse("2019-11-28T06:30");
        Assert.assertEquals(true, tripService.checkTripReach24Hours(order, rightNow));
    }

    @Test
    public void checkOrderReach24Hours5() throws Exception {
        Order order = new Order();
        order.setBegin_date(LocalDateTime.parse("2019-11-29T05:30"));
        LocalDateTime rightNow = LocalDateTime.parse("2019-11-28T05:15");
        Assert.assertEquals(false, tripService.checkTripReach24Hours(order, rightNow));
    }

    @Test
    public void checkOrderReach24Hours6() throws Exception {
        Order order = new Order();
        order.setBegin_date(LocalDateTime.parse("2019-11-29T05:30"));
        LocalDateTime rightNow = LocalDateTime.parse("2019-11-28T05:40");
        Assert.assertEquals(true, tripService.checkTripReach24Hours(order, rightNow));
    }

    @Test
    public void getExpectedEndTourTime() throws Exception {
        LocalDateTime rightNow = LocalDateTime.parse("2019-11-26T01:30");
        Assert.assertEquals("11/26/2019 04:00", tripService.getExpectedEndTripTime(1, rightNow));
    }

    @Test
    public void getOrderByWeek() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T05:30','2019-11-22T07:00',1,1,150,'abc','ONGOING')");
        Date start = new SimpleDateFormat("yyyy-MM-dd").parse("2019-11-20");
        Date end = new SimpleDateFormat("yyyy-MM-dd").parse("2019-11-30");
        Assert.assertEquals(1, tripService.getTripByWeek(1, start, end).size());
    }

    @Test
    public void cancelTripFilter() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-27T05:30','2019-11-22T07:00',1,1,150,'abc','WAITING')");
        tripService.cancelTripFilter();
        Assert.assertEquals("WAITING", tripService.findTripById(1).getStatus());
    }

    @Test
    public void finishTripFilter() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-27T05:30',now() - INTERVAL '10 HOURS',1,1,150,'abc','ONGOING')");
        tripService.finishTripFilter();
        Assert.assertEquals("FINISHED", tripService.findTripById(1).getStatus());
    }
}
