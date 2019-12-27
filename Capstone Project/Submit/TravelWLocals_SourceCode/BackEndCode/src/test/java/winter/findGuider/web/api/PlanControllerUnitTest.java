package winter.findGuider.web.api;

import entities.Plan;
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
import services.plan.PlanService;

import static org.mockito.Mockito.when;

public class PlanControllerUnitTest {
    @InjectMocks
    PlanController planController;

    @Mock
    PlanService planService;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before
    public void init() {
         MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePlan(){
        Plan plan = new Plan();
        when(planService.createPlan(plan)).thenReturn(1);
        ResponseEntity<Integer> result = planController.createPlan(plan);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testCreatePlanWithException() throws Exception{

        Plan plan = new Plan();
        //when(planService.createPlan(plan)).thenThrow(Exception.class);
        ReflectionTestUtils.setField(planController, "planService", null);

        ResponseEntity<Integer> result = planController.createPlan(plan);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
    @Test
    public void testUpdatePlan(){
        Plan plan = Mockito.mock(Plan.class);
        //when(planService.createPlan(plan)).thenReturn(1);
        ResponseEntity<Plan> result = planController.updatePlan(plan);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test(expected = AssertionError.class)
    public void testUpdatePlanWithException() throws Exception{
        thrown.expect(AssertionError.class);
        Plan plan = Mockito.mock(Plan.class);
        ReflectionTestUtils.setField(planController, "planService", null);

        ResponseEntity<Plan> result = planController.updatePlan(plan);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindPlan(){
        Plan plan = Mockito.mock(Plan.class);
        //when(planService.createPlan(plan)).thenReturn(1);
        ResponseEntity<Plan> result = planController.findPlan(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testFindPlanWithException() throws Exception{

        Plan plan = Mockito.mock(Plan.class);
        when(planService.searchPlanByPostId(1)).thenThrow(Exception.class);
        ResponseEntity<Plan> result = planController.findPlan(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
}
