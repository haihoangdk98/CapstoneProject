package services.Paypal;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
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
import winter.findGuider.TestDataSourceConfig;

import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.class)
public class PaypalServiceImplTest {

    @InjectMocks
    PaypalServiceImpl paypalService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Mock
    private APIContext apiContext;

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        paypalService = new PaypalServiceImpl(jdbcTemplate, apiContext);
        config.cleanTestDb(jdbcTemplate);
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPayment() throws PayPalRESTException {
        Assert.assertEquals(null, paypalService.createPayment(10.0, "USD", "a", "a", "a"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void executePayment() throws PayPalRESTException {
        Assert.assertEquals(null, paypalService.executePayment("1", "1"));
    }

    @Test
    public void createTransactionRecord() throws Exception {
        paypalService.createTransactionRecord("1", "1", "1", "a", true);
    }

    @Test
    public void getTransactionFee() throws Exception {
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (1,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (1,'Vietnam','Hanoi','Hoan Kiem')");
        jdbcTemplate.update("insert into category (category_id,name,image) values (1,'history','a.jpg')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (1,'John','Doe',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        jdbcTemplate.update("insert into contract_detail (contract_id,guider_id,name,nationality,date_of_birth,gender,hometown,address,identity_card_number,card_issued_date,card_issued_province,account_active_date)" +
                "values (1,1,'John Doe','Vietnamese','1993-06-05',1,'Hanoi','a','123456','2000-04-05','Hanoi','2016-10-15')");
        jdbcTemplate.update("insert into contract (guider_id,contract_id) values (1,1)");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (1,1,1,1,'test post','a','{a}',2,'a','{a,b}',true,10,5,'abc')");
        Order order = new Order();
        order.setPost_id(1);
        order.setAdult_quantity(1);
        order.setChildren_quantity(1);
        Assert.assertEquals(15, paypalService.getTransactionFee(order), 0D);
    }

    @Test
    public void getTransactionDescription() throws Exception {
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
        Order order = new Order();
        order.setAdult_quantity(1);
        order.setChildren_quantity(1);
        order.setBegin_date(LocalDateTime.parse("2019-11-27T00:00"));
        order.setFee_paid(10);
        order.setTraveler_id(2);
        order.setPost_id(1);
        Assert.assertEquals("On Nov 27, 2019\n. Include adult: 1, children: 1. Fee: 10.0. On tour test post of Megan Deo and John Doe", paypalService.getTransactionDescription(order));
    }

    @Test(expected = IllegalArgumentException.class)
    public void refundPayment() throws PayPalRESTException {
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
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        Assert.assertEquals(null, paypalService.refundPayment("abc"));
    }

    @Test
    public void createRefundRecord() throws Exception {
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) " +
                "values ('1','abc','abc','abc','2019-11-22T03:00',true)");
        paypalService.createRefundRecord("1", "a");
    }
}