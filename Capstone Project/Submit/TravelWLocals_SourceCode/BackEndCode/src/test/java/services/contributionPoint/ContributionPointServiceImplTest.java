package services.contributionPoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import winter.findGuider.TestDataSourceConfig;

@RunWith(MockitoJUnitRunner.class)
public class ContributionPointServiceImplTest {

    @InjectMocks
    ContributionPointServiceImpl contributionPointService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        contributionPointService = new ContributionPointServiceImpl(jdbcTemplate);
        config.cleanTestDb(jdbcTemplate);
        ReflectionTestUtils.setField(contributionPointService, "corMoney", "10");
        ReflectionTestUtils.setField(contributionPointService, "corRated", "100");
        ReflectionTestUtils.setField(contributionPointService, "corTurn", "200");
        ReflectionTestUtils.setField(contributionPointService, "minus", "5000");
        ReflectionTestUtils.setField(contributionPointService, "bonus20", "1.2");
        ReflectionTestUtils.setField(contributionPointService, "bonus15", "1.15");
        ReflectionTestUtils.setField(contributionPointService, "bonus10", "1.1");
        ReflectionTestUtils.setField(contributionPointService, "bonus5", "1.05");

        jdbcTemplate.update("insert into account(account_id,user_name,password,email,role) values (1,'John','123','John@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account(account_id,user_name,password,email,role) values (2,'Jack','123','Jack@gmail.com','TRAVELER')");
        jdbcTemplate.update("insert into account(account_id,user_name,password,email,role) values (3,'Jack','123','Jack@gmail.com','TRAVELER')");
        jdbcTemplate.update("insert into guider(guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (1,'John','Doe',now(),'12345678','abc',1000,'Hanoi','{vi,en}',true,5,'a','a')");
        jdbcTemplate.update("insert into guider(guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (2,'John','Doe',now(),'12345678','abc',1000,'Hanoi','{vi,en}',true,5,'a','a')");
        jdbcTemplate.update("insert into guider(guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (3,'John','Doe',now(),'12345678','abc',6000,'Hanoi','{vi,en}',true,5,'a','a')");
        jdbcTemplate.update("insert into traveler(traveler_id, first_name, last_name, phone, gender, date_of_birth, street, house_number, postal_code, slogan, about_me, language, country, city, avatar_link)" +
                "values (2,'Jack','Smith','123456',1,'1993-06-02','a','12','12','a','a','{vi}','vietnam','hanoi','a')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (1,'Vietnam','Hanoi','Hoan Kiem')");
        jdbcTemplate.update("insert into category (category_id,name,image) values (1,'history','a.jpg')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (1,1,1,1,'test post','a','{a}',2,'a','{a,b}',true,10,5,'abc')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30',now() - INTERVAL '30 HOURS',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into review (trip_id,rated,post_date,review,visible) values (1,5,'2019-11-25','abc',true)");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void calculateContributionPerTour() {
        Assert.assertEquals(2100, contributionPointService.calculateContributionPerTour(10, 5, 10));
    }

    @Test
    public void updateContributionByDay() {
        contributionPointService.updateContributionbyDay();
        String check = "select contribution from guider where guider_id = ?";
        int contribution = jdbcTemplate.queryForObject(check, new Object[]{1}, int.class);
        Assert.assertEquals(2700, contribution);
    }

    @Test
    public void updateContributionByMonth() {
        contributionPointService.updateContributionbyMonth();
        String check = "select contribution from guider where guider_id = ?";
        int contribution = jdbcTemplate.queryForObject(check, new Object[]{1}, int.class);
        Assert.assertEquals(1150, contribution);
        String check2 = "select contribution from guider where guider_id = ?";
        int contribution2 = jdbcTemplate.queryForObject(check2, new Object[]{2}, int.class);
        Assert.assertEquals(0, contribution2);
    }

    @Test
    public void updateContributionByMonth2() {
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (2,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (3,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (4,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (5,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (6,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        contributionPointService.updateContributionbyMonth();
        String check = "select contribution from guider where guider_id = ?";
        int contribution = jdbcTemplate.queryForObject(check, new Object[]{1}, int.class);
        Assert.assertEquals(1945, contribution);
        String check2 = "select contribution from guider where guider_id = ?";
        int contribution2 = jdbcTemplate.queryForObject(check2, new Object[]{2}, int.class);
        Assert.assertEquals(0, contribution2);
    }

    @Test
    public void updateContributionByMonth3() {
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (2,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (3,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (4,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (5,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (6,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (7,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (8,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (9,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (10,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (11,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        contributionPointService.updateContributionbyMonth();
        String check = "select contribution from guider where guider_id = ?";
        int contribution = jdbcTemplate.queryForObject(check, new Object[]{1}, int.class);
        Assert.assertEquals(2815, contribution);
        String check2 = "select contribution from guider where guider_id = ?";
        int contribution2 = jdbcTemplate.queryForObject(check2, new Object[]{2}, int.class);
        Assert.assertEquals(0, contribution2);
    }

    @Test
    public void updateContributionByMonth4() {
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (2,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (3,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (4,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (5,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (6,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (7,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (8,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (9,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (10,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (11,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (12,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (13,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (14,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (15,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (16,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        contributionPointService.updateContributionbyMonth();
        String check = "select contribution from guider where guider_id = ?";
        int contribution = jdbcTemplate.queryForObject(check, new Object[]{1}, int.class);
        Assert.assertEquals(3760, contribution);
        String check2 = "select contribution from guider where guider_id = ?";
        int contribution2 = jdbcTemplate.queryForObject(check2, new Object[]{2}, int.class);
        Assert.assertEquals(0, contribution2);
    }

    @Test
    public void updateContributionByMonth5() {
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (2,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (3,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (4,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (5,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (6,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (7,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (8,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (9,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (10,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (11,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (12,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (13,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (14,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (15,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (16,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (17,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (18,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (19,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (20,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (21,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        contributionPointService.updateContributionbyMonth();
        String check = "select contribution from guider where guider_id = ?";
        int contribution = jdbcTemplate.queryForObject(check, new Object[]{1}, int.class);
        Assert.assertEquals(4780, contribution);
        String check2 = "select contribution from guider where guider_id = ?";
        int contribution2 = jdbcTemplate.queryForObject(check2, new Object[]{2}, int.class);
        Assert.assertEquals(0, contribution2);
    }

    @Test
    public void penaltyGuiderForCancel() throws Exception {
        contributionPointService.penaltyGuiderForCancel(1);
        int result = jdbcTemplate.queryForObject("select contribution from guider where guider_id = ?", new Object[]{1}, int.class);
        Assert.assertEquals(500, result);
    }

    @Test
    public void penaltyGuiderForCancel2() throws Exception {
        jdbcTemplate.update("insert into account(account_id,user_name,password,email,role) values (4,'Jack','123','Jack@gmail.com','TRAVELER')");
        jdbcTemplate.update("insert into guider(guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (4,'John','Doe',now(),'12345678','abc',10,'Hanoi','{vi,en}',true,5,'a','a')");
        contributionPointService.penaltyGuiderForCancel(4);
        int result = jdbcTemplate.queryForObject("select contribution from guider where guider_id = ?", new Object[]{4}, int.class);
        Assert.assertEquals(0, result);
    }

    @Test
    public void penaltyGuiderForCancel3() throws Exception {
        jdbcTemplate.update("insert into account(account_id,user_name,password,email,role) values (4,'Jack','123','Jack@gmail.com','TRAVELER')");
        jdbcTemplate.update("insert into guider(guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (4,'John','Doe',now(),'12345678','abc',500,'Hanoi','{vi,en}',true,5,'a','a')");
        contributionPointService.penaltyGuiderForCancel(4);
        int result = jdbcTemplate.queryForObject("select contribution from guider where guider_id = ?", new Object[]{4}, int.class);
        Assert.assertEquals(0, result);
    }

    @Test
    public void calculatePostAndGuiderRating() throws Exception {
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (2,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (3,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (4,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (5,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into review (trip_id,rated,post_date,review,visible) values (2,5,'2019-11-25','abc',true)");
        jdbcTemplate.update("insert into review (trip_id,rated,post_date,review,visible) values (3,4,'2019-11-25','abc',true)");
        jdbcTemplate.update("insert into review (trip_id,rated,post_date,review,visible) values (4,4,'2019-11-25','abc',true)");
        jdbcTemplate.update("insert into review (trip_id,rated,post_date,review,visible) values (5,3,'2019-11-25','abc',true)");
        contributionPointService.calculatePostAndGuiderRating(1);
        float postRate = jdbcTemplate.queryForObject("select round(rated,1) from post where post_id = ?", new Object[]{1}, float.class);
        float guiderRate = jdbcTemplate.queryForObject("select round(rated,1) from guider where guider_id = ?", new Object[]{1}, float.class);
        Assert.assertEquals(4.2, postRate, 0.0002);
        Assert.assertEquals(4.2, guiderRate, 0.0002);
    }
}