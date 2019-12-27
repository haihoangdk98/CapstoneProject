package services.Post;

import entities.Post;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import services.GeneralService;
import winter.findGuider.TestDataSourceConfig;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTest {

    @InjectMocks
    PostServiceImpl postService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Mock
    private GeneralService generalService;

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        postService = new PostServiceImpl(jdbcTemplate, generalService);
        config.cleanTestDb(jdbcTemplate);
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (1,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (3,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (4,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (5,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        jdbcTemplate.update("insert into account (account_id,user_name, password, email ,role) " +
                "values (2,'Megan','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','TRAVELER')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (1,'Vietnam','Hanoi','Hoan Kiem')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (2,'Vietnam','Hanoi','Trang Tien')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (3,'Vietnam','Hanoi','Ho Tay')");
        jdbcTemplate.update("insert into locations (location_id,country,city,place) values (4,'Vietnam','Hanoi','Pho Co')");
        jdbcTemplate.update("insert into category (category_id,name,image) values (1,'history','a.jpg')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (1,'John','Doe',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (3,'Midrian','Lomi',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (4,'Jack','Mike',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        jdbcTemplate.update("insert into guider (guider_id,first_name,last_name,date_of_birth,phone,about_me,contribution,city,languages,active,rated,avatar,passion)" +
                "values (5,'Larry','Teddy',now(),'123456','abc',150,'hanoi','{en,vi}',true,5,'a','a')");
        jdbcTemplate.update("insert into contract_detail (contract_id,guider_id,name,nationality,date_of_birth,gender,hometown,address,identity_card_number,card_issued_date,card_issued_province,account_active_date)" +
                "values (1,1,'John Doe','Vietnamese','1993-06-05',1,'Hanoi','a','123456','2000-04-05','Hanoi','2016-10-15')");
        jdbcTemplate.update("insert into contract (guider_id,contract_id) values (1,1)");
        jdbcTemplate.update("insert into traveler (traveler_id, first_name, last_name, phone, gender, date_of_birth, street, house_number, postal_code, slogan, about_me, language, country, city, avatar_link)" +
                "values (2,'Megan','Deo','123',2,'1996-02-13','a','12','12','a','a','{en,vi}','vietnam','hanoi','a')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (1,1,1,1,'test post','a','{a}',2,'a','{a,b}',true,10,5,'abc')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (2,3,2,1,'Midria','a','{a}',2,'a','{a,b}',true,10,7,'abc')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (3,4,3,1,'Milo','a','{a}',2,'a','{a,b}',true,10,16,'abc')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (4,5,4,1,'Minute','a','{a}',2,'a','{a,b}',true,10,11,'abc')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (5,1,1,1,'Khala','a','{a}',2,'a','{a,b}',true,10,19,'abc')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (6,1,2,1,'Temre','a','{a}',2,'a','{a,b}',true,10,1,'abc')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (7,5,3,1,'Fooffy','a','{a}',2,'a','{a,b}',true,10,3,'abc')");
        jdbcTemplate.update("INSERT INTO post(post_id,guider_id, location_id,category_id, title, video_link, picture_link, total_hour, description, including_service, active,price,rated,reasons) " +
                "VALUES (8,5,4,1,'Fuuffy','a','{a}',2,'a','{a,b}',false,10,20,'abc')");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findAllPostOfOneGuider() throws Exception {
        Assert.assertEquals(3, postService.findAllPostOfOneGuider(1, 0).size());
    }

    @Test
    public void findAllPostByCategoryId() throws Exception {
        Assert.assertEquals(7, postService.findAllPostByCategoryId(1, 0).size());
    }

    @Test
    public void findSpecificPost() throws Exception {
        Assert.assertEquals("test post", postService.findSpecificPost(1).getTitle());
    }

    @Test
    public void updatePost() throws Exception {
        Post post = new Post("test", "a", new String[]{"a"}, 2, "a", new String[]{"a", "b"}, true, "1", 10, 5, "a", "1");
        postService.updatePost(1, post);
        Assert.assertEquals("test", postService.findSpecificPost(1).getTitle());
    }

    @Test
    public void insertNewPost() throws Exception {
        jdbcTemplate.update("delete from post");
        Post post = new Post("test new", "a", new String[]{"a"}, 2, "a", new String[]{"a", "b"}, true, "1", 10, 5, "a", "1");
        int id = postService.insertNewPost(1, post);
        Assert.assertEquals("test new", postService.findSpecificPost(id).getTitle());
    }

    @Test
    public void getTopTour() throws Exception {
        List<Post> result = postService.getTopTour();
        Assert.assertEquals(6, result.size());
        Assert.assertEquals(5, result.get(0).getPost_id());
        Assert.assertEquals(3, result.get(1).getPost_id());
        Assert.assertEquals(4, result.get(2).getPost_id());
        Assert.assertEquals(2, result.get(3).getPost_id());
        Assert.assertEquals(1, result.get(4).getPost_id());
        Assert.assertEquals(7, result.get(5).getPost_id());
    }

    @Test
    public void findAllPostWithGuiderName() throws Exception {
        Assert.assertEquals(2, postService.findAllPostWithGuiderName("mi", 0).size());
    }

    @Test
    public void activeDeactivePost() throws Exception {
        postService.activeDeactivePostForGuider(1);
        boolean active = jdbcTemplate.queryForObject("select active from post where post_id = ?", new Object[]{1}, boolean.class);
        Assert.assertEquals(false, active);
    }

    @Test
    public void activeDeactivePost2() throws Exception {
        postService.activeDeactivePostForGuider(1);
        postService.activeDeactivePostForGuider(1);
        boolean active = jdbcTemplate.queryForObject("select active from post where post_id = ?", new Object[]{1}, boolean.class);
        Assert.assertEquals(true, active);
    }

    @Test
    public void findAllPostWithLocationName() throws Exception {
        Assert.assertEquals(5, postService.findAllPostWithLocationName("ho", 0).size());
    }

    @Test
    public void authorizePost() throws Exception {
        postService.authorizePost(1);
        boolean active = jdbcTemplate.queryForObject("select active from post where post_id = ?", new Object[]{1}, boolean.class);
        boolean authorized = jdbcTemplate.queryForObject("select authorized from post where post_id = ?", new Object[]{1}, boolean.class);
        Assert.assertEquals(false, active);
        Assert.assertEquals(false, authorized);
    }

    @Test
    public void authorizePost2() throws Exception {
        postService.authorizePost(1);
        postService.authorizePost(1);
        boolean active = jdbcTemplate.queryForObject("select active from post where post_id = ?", new Object[]{1}, boolean.class);
        boolean authorized = jdbcTemplate.queryForObject("select authorized from post where post_id = ?", new Object[]{1}, boolean.class);
        Assert.assertEquals(false, active);
        Assert.assertEquals(true, authorized);
    }

    @Test
    public void findAllPostOfOneGuiderAdmin() throws Exception {
        Assert.assertEquals(3, postService.findAllPostOfOneGuiderAdmin(5).size());
    }

    @Test
    public void findAllPostOfOneGuiderPageCount() throws Exception {
        Assert.assertEquals(1, postService.findAllPostOfOneGuiderPageCount(1));
    }

    @Test
    public void findAllPostByCategoryIdPageCount() throws Exception {
        Assert.assertEquals(1, postService.findAllPostByCategoryIdPageCount(1));
    }

    @Test
    public void findAllPostWithGuiderNamePageCount() throws Exception {
        Assert.assertEquals(1, postService.findAllPostWithGuiderNamePageCount("Jo"));
    }

    @Test
    public void findAllPostWithLocationNamePageCount() throws Exception {
        Assert.assertEquals(1, postService.findAllPostWithLocationNamePageCount("h"));
    }
}