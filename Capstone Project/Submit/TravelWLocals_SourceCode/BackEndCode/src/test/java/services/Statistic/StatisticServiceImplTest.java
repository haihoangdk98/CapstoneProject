package services.Statistic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import winter.findGuider.TestDataSourceConfig;

@RunWith(MockitoJUnitRunner.class)
public class StatisticServiceImplTest {

    @InjectMocks
    StatisticServiceImpl statisticService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        statisticService = new StatisticServiceImpl(jdbcTemplate);
        config.cleanTestDb(jdbcTemplate);
        jdbcTemplate.update("insert into account(account_id,user_name,password,email,role) values (1,'John','123','John@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account(account_id,user_name,password,email,role) values (2,'Jack','123','Jack@gmail.com','TRAVELER')");
        jdbcTemplate.update("insert into guider(guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (1,'John','Doe',now(),'12345678','abc',1000,'Hanoi','{vi,en}',true,5,'a','a')");
        jdbcTemplate.update("insert into traveler(traveler_id, first_name, last_name, phone, gender, date_of_birth, street, house_number, postal_code, slogan, about_me, language, country, city, avatar_link)" +
                "values (2,'Jack','Smith','123456',1,'1993-06-02','a','12','12','a','a','{vi}','vietnam','hanoi','a')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (1,'Vietnam','Hanoi','Hoan Kiem')");
        jdbcTemplate.update("insert into category (category_id,name,image) values (1,'history','a.jpg')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (1,1,1,1,'test post','a','{a}',2,'a','{a,b}',true,10,5,'abc')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-06-22T03:30',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (2,2,1,'2019-11-22T03:30','2019-07-22T03:30',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (3,2,1,'2019-11-22T03:30','2019-08-22T03:30',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (4,2,1,'2019-11-22T03:30','2019-08-22T03:30',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (5,2,1,'2019-11-22T03:30','2019-09-22T03:30',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (6,2,1,'2019-11-22T03:30','2019-10-22T03:30',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (7,2,1,'2019-11-22T03:30','2019-11-22T03:30',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (8,2,1,'2019-11-22T03:30','2019-11-22T03:30',1,1,150,'abc','FINISHED')");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getStatisticCompletedTrip() throws Exception {
        Assert.assertEquals(6, statisticService.getStatisticCompletedTrip().size());
    }

    @Test
    public void getStatisticTotalRevenue() throws Exception {
        Assert.assertEquals(6, statisticService.getStatisticTotalRevenue().size());
    }
}