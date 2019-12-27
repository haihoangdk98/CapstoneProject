package services.Mail;

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
import org.springframework.mail.javamail.JavaMailSender;
import services.GeneralService;
import services.Post.PostService;
import services.Post.PostServiceImpl;
import services.guider.GuiderService;
import services.guider.GuiderServiceImpl;
import services.traveler.TravelerService;
import services.traveler.TravelerServiceImpl;
import services.trip.TripService;
import services.trip.TripServiceImpl;
import winter.findGuider.TestDataSourceConfig;

import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.class)
public class MailServiceImplTest {

    @InjectMocks
    MailServiceImpl mailService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private GeneralService generalService;

    private TravelerService travelerService;
    private GuiderService guiderService;
    private PostService postService;
    private TripService tripService;

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        travelerService = new TravelerServiceImpl(jdbcTemplate, generalService);
        guiderService = new GuiderServiceImpl(jdbcTemplate, generalService);
        postService = new PostServiceImpl(jdbcTemplate, generalService);
        tripService = new TripServiceImpl(jdbcTemplate);
        mailService = new MailServiceImpl(emailSender, travelerService, guiderService, postService, tripService);
        config.cleanTestDb(jdbcTemplate);
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (1,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (3,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (4,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (2,'Megan','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','TRAVELER')");

        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (1,'Vietnam','Hanoi','Hoan Kiem')");
        jdbcTemplate.update("insert into category (category_id,name,image) values (1,'history','a.jpg')");

        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (1,'John','Doe',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (3,'John','Doe',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (4,'John','Doe',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");

        jdbcTemplate.update("insert into contract_detail (contract_id,guider_id,name,nationality,date_of_birth,gender,hometown,address,identity_card_number,card_issued_date,card_issued_province,account_active_date)" +
                "values (1,1,'John Doe','Vietnamese','1993-06-05',1,'Hanoi','a','123456','2000-04-05','Hanoi','2016-10-15')");
        jdbcTemplate.update("insert into contract (guider_id,contract_id) values (1,1)");

        jdbcTemplate.update("insert into contract_detail (contract_id,guider_id,name,nationality,date_of_birth,gender,hometown,address,identity_card_number,card_issued_date,card_issued_province,account_active_date)" +
                "values (2,3,'John Doe','Vietnamese','1993-06-05',2,'Hanoi','a','123456','2000-04-05','Hanoi','2016-10-15')");
        jdbcTemplate.update("insert into contract (guider_id,contract_id) values (3,2)");

        jdbcTemplate.update("insert into contract_detail (contract_id,guider_id,name,nationality,date_of_birth,gender,hometown,address,identity_card_number,card_issued_date,card_issued_province,account_active_date)" +
                "values (3,4,'John Doe','Vietnamese','1993-06-05',0,'Hanoi','a','123456','2000-04-05','Hanoi','2016-10-15')");
        jdbcTemplate.update("insert into contract (guider_id,contract_id) values (4,3)");

        jdbcTemplate.update("insert into traveler (traveler_id, first_name, last_name, phone, gender, date_of_birth, street, house_number, postal_code, slogan, about_me, language, country, city, avatar_link)" +
                "values (2,'Megan','Deo','123',2,'1996-02-13','a','12','12','a','a','{en,vi}','vietnam','hanoi','a')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (1,1,1,1,'test post','a','{a}',2,'a','{a,b}',true,10,5,'abc')");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendMail() throws Exception {
        Assert.assertEquals(true, mailService.sendMail("travelwithlocalsysadm@gmail.com", "test method", "test"));
    }

    @Test
    public void getMailContent() throws Exception {
        Order order = new Order(1, 2, 1, 1, LocalDateTime.parse("2019-11-12T00:00"), LocalDateTime.parse("2019-11-12T00:00"), 2, 1, 150, "ABCD", null);
        Assert.assertEquals("Dear Mr/Ms Deo\n" +
                "\n" +
                "Your tour has been booked successfully.\n" +
                "Below is the information of your tour:\n" +
                "Tour: test post\n" +
                "Your guider: John Doe\n" +
                "Begin on: 11/12/2019 00:00 - Expected end on: 11/12/2019 00:00\n" +
                "The tour has 2 adults and 1 children.\n" +
                "Total: 150.0$\n" +
                "\n" +
                "Status: Waiting for confirmation\n" +
                "\n" +
                "Thank your for using our service. We wish you a great trip and happy experience.\n" +
                "\n" +
                "Sincerely,\n" +
                "TravelWLocal", mailService.getMailContent(order, "WAITING"));
    }

    @Test
    public void getMailContent2() throws Exception {
        Order order = new Order(1, 2, 1, 1, LocalDateTime.parse("2019-11-12T00:00"), LocalDateTime.parse("2019-11-12T00:00"), 2, 1, 150, "ABCD", "ONGOING");
        Assert.assertEquals("Dear Mr/Ms Deo\n" +
                "\n" +
                "Your tour has been accepted by John Doe.\n" +
                "Below is the information of your tour:\n" +
                "Tour: test post\n" +
                "Your guider: John Doe\n" +
                "Begin on: 11/12/2019 00:00 - Expected end on: 11/12/2019 00:00\n" +
                "The tour has 2 adults and 1 children.\n" +
                "Total: 150.0$\n" +
                "\n" +
                "Status: Ongoing\n" +
                "\n" +
                "Thank your for using our service. We wish you a great trip and happy experience.\n" +
                "\n" +
                "Sincerely,\n" +
                "TravelWLocal", mailService.getMailContent(order, "ONGOING"));
    }

    @Test
    public void getMailContent3() throws Exception {
        Order order = new Order(1, 2, 1, 1, LocalDateTime.parse("2019-11-12T00:00"), LocalDateTime.parse("2019-11-12T00:00"), 2, 1, 150, "ABCD", "CANCELLED");
        Assert.assertEquals("Dear Mr/Ms Deo\n" +
                "\n" +
                "Your tour has been cancelled by John Doe.\n" +
                "Below is the information of your tour:\n" +
                "Tour: test post\n" +
                "Your guider: John Doe\n" +
                "Begin on: 11/12/2019 00:00 - Expected end on: 11/12/2019 00:00\n" +
                "The tour has 2 adults and 1 children.\n" +
                "Total: 150.0$\n" +
                "\n" +
                "Status: Cancelled\n" +
                "\n" +
                "Thank your for using our service. We wish you a great trip and happy experience.\n" +
                "\n" +
                "Sincerely,\n" +
                "TravelWLocal", mailService.getMailContent(order, "CANCELLED"));
    }

    @Test
    public void getMailContent4() throws Exception {
        Order order = new Order(1, 2, 1, 1, LocalDateTime.parse("2019-11-12T00:00"), LocalDateTime.parse("2019-11-12T00:00"), 2, 1, 150, "ABCD", "FINISHED");
        Assert.assertEquals("Dear Mr/Ms Deo\n" +
                "\n" +
                "Your tour has finished.\n" +
                "Below is the information of your tour:\n" +
                "Tour: test post\n" +
                "Your guider: John Doe\n" +
                "Begin on: 11/12/2019 00:00 - Expected end on: 11/12/2019 00:00\n" +
                "The tour has 2 adults and 1 children.\n" +
                "Total: 150.0$\n" +
                "\n" +
                "Status: Finished\n" +
                "\n" +
                "Thank your for using our service. We wish you a great trip and happy experience.\n" +
                "\n" +
                "Sincerely,\n" +
                "TravelWLocal", mailService.getMailContent(order, "FINISHED"));
    }

    @Test
    public void acceptContractMailContent() throws Exception {
        Assert.assertEquals("Dear Mr/Ms John Doe\n" +
                "\n" +
                "Your contract to become a guider has been accepted !\n" +
                "\n" +
                "Here is the information you have given us upon setting up the contract\n" +
                "Name: John Doe\n" +
                "Nationality: Vietnamese\n" +
                "Date of birth (MM/dd/yyyy): 06/05/1993\n" +
                "Gender: Male\n" +
                "Hometown: Hanoi\n" +
                "Address: a\n" +
                "Identity Card Number: 123456\n" +
                "Identity Card Issued Date: 04/05/2000\n" +
                "Identity Card Issued Province: Hanoi\n" +
                "If any information were mistakenly given, please contact us immediately.\n" +
                "We also recommend to update your profile if you have not done it, so the customers can have a better understanding about you !\n" +
                "\n" +
                "Thank your for using our service.\n" +
                "\n" +
                "Sincerely,\n" +
                "TravelWLocal", mailService.acceptContractMailContent(1));
    }

    @Test
    public void acceptContractMailContent2() throws Exception {
        Assert.assertEquals("Dear Mr/Ms John Doe\n" +
                "\n" +
                "Your contract to become a guider has been accepted !\n" +
                "\n" +
                "Here is the information you have given us upon setting up the contract\n" +
                "Name: John Doe\n" +
                "Nationality: Vietnamese\n" +
                "Date of birth (MM/dd/yyyy): 06/05/1993\n" +
                "Gender: Female\n" +
                "Hometown: Hanoi\n" +
                "Address: a\n" +
                "Identity Card Number: 123456\n" +
                "Identity Card Issued Date: 04/05/2000\n" +
                "Identity Card Issued Province: Hanoi\n" +
                "If any information were mistakenly given, please contact us immediately.\n" +
                "We also recommend to update your profile if you have not done it, so the customers can have a better understanding about you !\n" +
                "\n" +
                "Thank your for using our service.\n" +
                "\n" +
                "Sincerely,\n" +
                "TravelWLocal", mailService.acceptContractMailContent(3));
    }

    @Test
    public void acceptContractMailContent3() throws Exception {
        Assert.assertEquals("Dear Mr/Ms John Doe\n" +
                "\n" +
                "Your contract to become a guider has been accepted !\n" +
                "\n" +
                "Here is the information you have given us upon setting up the contract\n" +
                "Name: John Doe\n" +
                "Nationality: Vietnamese\n" +
                "Date of birth (MM/dd/yyyy): 06/05/1993\n" +
                "Gender: Other\n" +
                "Hometown: Hanoi\n" +
                "Address: a\n" +
                "Identity Card Number: 123456\n" +
                "Identity Card Issued Date: 04/05/2000\n" +
                "Identity Card Issued Province: Hanoi\n" +
                "If any information were mistakenly given, please contact us immediately.\n" +
                "We also recommend to update your profile if you have not done it, so the customers can have a better understanding about you !\n" +
                "\n" +
                "Thank your for using our service.\n" +
                "\n" +
                "Sincerely,\n" +
                "TravelWLocal", mailService.acceptContractMailContent(4));
    }

    @Test
    public void rejectContractMailContent() throws Exception {
        Assert.assertEquals("Dear Mr/Ms John Doe\n" +
                "\n" +
                "Thank you for your application for the position of tour guider at our TravelWLocal website. As you can imagine, we received a large number of applications. I am sorry to inform you that you have not been selected for this position.\n" +
                "\n" +
                "We at TravelWLocal thanks you for the time you invested in applying for the position. We encourage you to apply for future openings for which you qualify.\n" +
                "\n" +
                "Best wishes for successful in life. Thank you, again, for your interest in our website.\n" +
                "\n" +
                "Thank your for using our service.\n" +
                "\n" +
                "Sincerely,\n" +
                "TravelWLocal", mailService.rejectContractMailContent(1));
    }
}