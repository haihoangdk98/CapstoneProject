package winter.findGuider.web.api;

import entities.Traveler;
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
import services.traveler.TravelerService;

import static org.mockito.Mockito.when;

public class TravelerControllerUnitTest {
    @InjectMocks
    TravelerController travelerController;

    @Mock
    TravelerService travelerService;

    @Rule
    public ExpectedException thrown= ExpectedException.none();
    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetTravelWithId(){
        ResponseEntity<Traveler> result = travelerController.getTravelerWithId(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
    @Test
    public void testGetTravelWithId2() throws Exception {
        Traveler traveler = Mockito.mock(Traveler.class);
        when(travelerService.findTravelerWithId(1)).thenReturn(traveler);
        ResponseEntity<Traveler> result = travelerController.getTravelerWithId(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }


    @Test(expected = AssertionError.class)
    public void testGetTravelWithIdWithException() throws Exception{
        thrown.expect(AssertionError.class);
        //Traveler traveler = Mockito.mock(Traveler.class);
        when(travelerService.findTravelerWithId(1)).thenThrow(Exception.class);
        ResponseEntity<Traveler> result = travelerController.getTravelerWithId(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testCreateTraveler(){
        Traveler traveler = Mockito.mock(Traveler.class);
        ResponseEntity<Boolean> result = travelerController.createTraveler(traveler);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test(expected = AssertionError.class)
    public void testCreateTravelerWithException() throws Exception{
        thrown.expect(AssertionError.class);
        Traveler traveler = Mockito.mock(Traveler.class);
        when(travelerService.createTraveler(traveler)).thenThrow(Exception.class);
        ResponseEntity<Boolean> result = travelerController.createTraveler(traveler);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testUpdateTraveler(){
        Traveler traveler = Mockito.mock(Traveler.class);
        ResponseEntity<Traveler> result = travelerController.updateTraveler(traveler);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test(expected = AssertionError.class)
    public void testUpdateTravelerWithException() throws Exception{
        thrown.expect(AssertionError.class);
        Traveler traveler = Mockito.mock(Traveler.class);
        when(travelerService.findTravelerWithId(traveler.getTraveler_id())).thenThrow(Exception.class);
        ResponseEntity<Traveler> result = travelerController.updateTraveler(traveler);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFavoritePost(){
        ResponseEntity<Boolean> result = travelerController.favoritePost(1,1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testFavoritePostWithException() throws Exception {
        ReflectionTestUtils.setField(travelerController, "travelerService", null);

        ResponseEntity<Boolean> result = travelerController.favoritePost(1,1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testIsLackingProfileWithException() throws Exception {
        ReflectionTestUtils.setField(travelerController, "travelerService", null);

        ResponseEntity<Boolean> result = travelerController.isLackingProfile(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testUpdateLackingProfile(){
        Traveler traveler = Mockito.mock(Traveler.class);
        ResponseEntity<Boolean> result = travelerController.updateLackingProfile(traveler);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testUpdateLackingProfileWithException() throws Exception {
        ReflectionTestUtils.setField(travelerController, "travelerService", null);
        Traveler traveler = Mockito.mock(Traveler.class);
        ResponseEntity<Boolean> result = travelerController.updateLackingProfile(traveler);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
}
