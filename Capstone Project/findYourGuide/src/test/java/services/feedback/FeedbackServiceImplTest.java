package services.feedback;

import entities.Feedback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import winter.findGuider.TestDataSourceConfig;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackServiceImplTest {

    @InjectMocks
    FeedbackServiceImpl feedbackService;

    private JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Before
    public void init() {
        TestDataSourceConfig config = new TestDataSourceConfig();
        jdbcTemplate.setDataSource(config.setupDatasource());
        feedbackService = new FeedbackServiceImpl(jdbcTemplate);
        config.cleanTestDb(jdbcTemplate);
        jdbcTemplate.update("insert into account (account_id, user_name, password, email ,role) " +
                "values (1,'Jacky','$2a$10$Tb3mK1p2pCuPvDJUgSOJr.Rupo9isjom9vmmzAppMjtvWfLn/vQcK','Jacky@gmail.com','GUIDER')");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findAllFeedback() throws Exception {
        jdbcTemplate.update("insert into feedback (feedback_id,account_id,time_sent,message)" +
                "values (1,1,'2019-11-28 05:00:00','test')");
        List<Feedback> result = feedbackService.findAllFeedback();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("Jacky", result.get(0).getSender_name());
    }

    @Test
    public void createFeedback() throws Exception {
        feedbackService.createFeedback(1, "test");
        Assert.assertEquals(1, feedbackService.findAllFeedback().size());
    }
}