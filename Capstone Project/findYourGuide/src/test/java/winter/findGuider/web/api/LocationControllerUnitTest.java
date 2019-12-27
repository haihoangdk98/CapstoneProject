package winter.findGuider.web.api;

import entities.Location;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import services.Location.LocationServiceImpl;
import services.guider.GuiderService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

public class LocationControllerUnitTest {
    @InjectMocks
    LocationController locationController;

    @Mock
    LocationServiceImpl locationService;

    @Rule
    public ExpectedException thrown= ExpectedException.none();
    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllLocation(){
        List<Location> listLocation = new ArrayList<>();
        //when(locationService.showAllLocation()).then()

        ResponseEntity<Location> result =locationController.findAllLocation();
        Assert.assertEquals(200, result.getStatusCodeValue());
        //Assert.assertEquals(true, result.equals());
    }

    @Test
    public void testLocationWithException() throws Exception{

        when(locationService.showAllLocation()).thenThrow(Exception.class);
        ResponseEntity<Location> result =locationController.findAllLocation();


        Assert.assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    public void testCreateLocation(){
        ResponseEntity<Boolean> result = locationController.createLocation("Vietnam","Hanoi","Ho Tay");
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test
    public void testAcceptContractWithException() throws Exception{
        //when(guiderService.linkGuiderWithContract(1,1)).thenThrow(Exception.class);
        ReflectionTestUtils.setField(locationController, "locationService", null);

        ResponseEntity<Boolean> result = locationController.createLocation("Vietnam","Hanoi","Ho Tay");
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
}
