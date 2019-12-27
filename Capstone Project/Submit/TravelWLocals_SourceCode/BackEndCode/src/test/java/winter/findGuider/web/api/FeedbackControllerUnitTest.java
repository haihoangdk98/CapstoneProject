package winter.findGuider.web.api;

import entities.Feedback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import services.feedback.FeedbackService;

import java.util.List;

import static org.mockito.Mockito.when;

public class FeedbackControllerUnitTest {
    @InjectMocks
    FeedbackController feedbackController;

    @Mock
    FeedbackService feedbackService;

    @Rule
    public ExpectedException thrown= ExpectedException.none();
    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateCategory(){
        ResponseEntity<Boolean> result = feedbackController.createFeedback(1,"this is good");
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testCreateFeedbackWithException() throws Exception {
        ReflectionTestUtils.setField(feedbackController, "feedbackService", null);

        ResponseEntity<Boolean> result = feedbackController.createFeedback(1,"this is good");
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindAllFeedbackWithException() throws Exception {
        //ReflectionTestUtils.setField(feedbackController, "feedbackService", null);
        when(feedbackService.findAllFeedback()).thenThrow(Exception.class);
        ResponseEntity<List<Feedback>> result = feedbackController.findAllFeedback();
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
}
