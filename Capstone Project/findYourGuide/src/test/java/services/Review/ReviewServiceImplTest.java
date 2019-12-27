package services.Review;

import entities.Review;
import entities.TravelerReview;
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
public class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        reviewService = new ReviewServiceImpl(jdbcTemplate);
        config.cleanTestDb(jdbcTemplate);
        jdbcTemplate.update("insert into account(account_id,user_name,password,email,role) values (1,'John','123','John@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account(account_id,user_name,password,email,role) values (2,'Jack','123','Jack@gmail.com','TRAVELER')");
        jdbcTemplate.update("insert into guider(guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (1,'John','Doe',now(),'12345678','abc',150,'Hanoi','{vi,en}',true,5,'a','a')");
        jdbcTemplate.update("insert into traveler(traveler_id, first_name, last_name, phone, gender, date_of_birth, street, house_number, postal_code, slogan, about_me, language, country, city, avatar_link)" +
                "values (2,'Jack','Smith','123456',1,'1993-06-02','a','12','12','a','a','{vi}','vietnam','hanoi','a')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (1,'Vietnam','Hanoi','Hoan Kiem')");
        jdbcTemplate.update("insert into category (category_id,name,image) values (1,'history','a.jpg')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (1,1,1,1,'test post','a','{a}',2,'a','{a,b}',true,10,5,'abc')");
        jdbcTemplate.update("insert into transaction (transaction_id,payment_id,payer_id,description,date_of_transaction,success) values ('abc','abc','abc','abc','2019-11-22T03:00',true)");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (1,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (2,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        jdbcTemplate.update("insert into review (trip_id,rated,post_date,review,visible) values (1,5,'2019-11-25','abc',true)");
        jdbcTemplate.update("insert into review (trip_id,rated,post_date,review,visible) values (2,5,'2019-11-25','abc',false)");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findReviewByOrderId() throws Exception {
        Assert.assertEquals("abc", reviewService.findReviewByOrderId(1).get(0).getReview());
    }

    @Test
    public void findReviewsByGuiderId() throws Exception {
        Assert.assertEquals("abc", reviewService.findReviewsByGuiderId(1).get(0).getReview());
    }

    @Test
    public void findReviewsByPostId() throws Exception {
        Assert.assertEquals(1, reviewService.findReviewsByPostId(1, 0).size());
    }

    @Test
    public void findReviewsByPostIdAdmin() throws Exception {
        Assert.assertEquals(2, reviewService.findReviewsByPostIdAdmin(1).size());
    }

    @Test
    public void createReview() throws Exception {
        jdbcTemplate.update("insert into trip (trip_id,traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)" +
                "values (3,2,1,'2019-11-22T03:30','2019-11-23T10:00',1,1,150,'abc','FINISHED')");
        Review review = new Review(3, 5, "abc");
        Assert.assertEquals(true, reviewService.createReview(review));
    }

    @Test
    public void checkReviewExist() throws Exception {
        Assert.assertEquals(true, reviewService.checkReviewExist(1));
    }

    @Test
    public void checkReviewExist2() throws Exception {
        Assert.assertEquals(false, reviewService.checkReviewExist(3));
    }

    @Test
    public void showHideReview() throws Exception {
        reviewService.showHideReview(1);
        Assert.assertEquals(false, reviewService.findReviewByOrderId(1).get(0).isVisible());
    }

    @Test
    public void showHideReview2() throws Exception {
        reviewService.showHideReview(1);
        reviewService.showHideReview(1);
        Assert.assertEquals(true, reviewService.findReviewByOrderId(1).get(0).isVisible());
    }

    @Test
    public void createTravelerReview() throws Exception {
        TravelerReview review = new TravelerReview(2, 1, "abc");
        reviewService.createTravelerReview(review);
        int count = jdbcTemplate.queryForObject("select count(review_id) from travelerreviews where traveler_id = ?", new Object[]{2}, int.class);
        Assert.assertEquals(1, count);
    }

    @Test
    public void findReviewOfATraveler() throws Exception {
        jdbcTemplate.update("insert into travelerreviews values (1,2,1,now(),'abc',true)");
        jdbcTemplate.update("insert into travelerreviews values (2,2,1,now(),'abc',true)");
        Assert.assertEquals(2, reviewService.findReviewOfATraveler(2, 0).size());
    }

    @Test
    public void findTravelerReviewById() throws Exception {
        jdbcTemplate.update("insert into travelerreviews values (1,2,1,now(),'abc',true)");
        jdbcTemplate.update("insert into travelerreviews values (2,2,1,now(),'abc',true)");
        Assert.assertEquals("abc", reviewService.findTravelerReviewById(1).getReview());
    }
}