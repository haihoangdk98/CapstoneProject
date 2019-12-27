package winter.findGuider.web.api;

import entities.Statistic;
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
import services.Statistic.StatisticService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class StatisticControllerUnitTest {
    @InjectMocks
    StatisticController statisticController;

    @Mock
    StatisticService statisticService;

    @Rule
    public ExpectedException thrown= ExpectedException.none();
    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetStatisticCompletedTrip(){
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        ResponseEntity<List<Statistic>> result = statisticController.getStatisticCompletedTrip(httpServletResponse);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test
    public void testGetStatisticCompletedTripWithException() throws Exception {
        ReflectionTestUtils.setField(statisticController, "statisticService", null);
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        ResponseEntity<List<Statistic>> result = statisticController.getStatisticCompletedTrip(httpServletResponse);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testGetStatisticTotalRevenue(){
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        ResponseEntity<List<Statistic>> result = statisticController.getStatisticTotalRevenue(httpServletResponse);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test
    public void testGetStatisticTotalRevenueWithException() throws Exception {
        ReflectionTestUtils.setField(statisticController, "statisticService", null);
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        ResponseEntity<List<Statistic>> result = statisticController.getStatisticTotalRevenue(httpServletResponse);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
}
