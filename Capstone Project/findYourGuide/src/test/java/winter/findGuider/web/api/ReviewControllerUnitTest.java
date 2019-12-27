package winter.findGuider.web.api;

import entities.*;
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
import services.Review.ReviewService;
import services.contributionPoint.ContributionPointService;
import services.trip.TripService;

import java.util.List;

import static org.mockito.Mockito.when;

public class ReviewControllerUnitTest {
    @InjectMocks
    ReviewController reviewController;

    @Mock
    ReviewService reviewService;

    @Mock
    TripService tripService;

    @Mock
    ContributionPointService contributionPointService;

    @Rule
    public ExpectedException thrown= ExpectedException.none();
    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindReviewByGuiderId(){
        ResponseEntity<List<Review>>  result = reviewController.findReviewByGuiderId(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test(expected = AssertionError.class)
    public void testFindReviewByGuiderIdWithException() throws Exception {
        thrown.expect(AssertionError.class);
        when(reviewService.findReviewsByGuiderId(1)).thenThrow(Exception.class);
        ResponseEntity<List<Review>>  result = reviewController.findReviewByGuiderId(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindReviewByPostId(){
        ResponseEntity<List<Review>>  result = reviewController.findReviewByPostId(1, 0);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test(expected = AssertionError.class)
    public void testFindReviewByPostIdWithException() throws Exception {
        thrown.expect(AssertionError.class);
        when(reviewService.findReviewsByPostId(1, 0)).thenThrow(Exception.class);
        ResponseEntity<List<Review>>  result = reviewController.findReviewByPostId(1, 0);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testCreateReview() throws Exception{
        Review review = new Review();
        Order order = new Order();
        order.setPost_id(1);
        review.setTrip_id(1);
        when(reviewService.createReview(review)).thenReturn(true);

        when(tripService.findTripById((int) review.getTrip_id())).thenReturn(order);
        ResponseEntity<Boolean>  result = reviewController.createReview(review);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test(expected = AssertionError.class)
    public void testCreateReviewWithException() throws Exception {
        thrown.expect(AssertionError.class);
        Review review = Mockito.mock(Review.class);
        when(reviewService.createReview(review)).thenThrow(Exception.class);
        ResponseEntity<Boolean>  result = reviewController.createReview(review);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testCheckReviewExist() throws Exception {
        when(reviewService.checkReviewExist(1)).thenReturn(true);
        ResponseEntity<List<Review>>  result = reviewController.checkReviewExist(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test
    public void testCheckReviewExist2() throws Exception {
        when(reviewService.checkReviewExist(1)).thenReturn(false);
        ResponseEntity<List<Review>>  result = reviewController.checkReviewExist(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test(expected = AssertionError.class)
    public void testCheckREviewExistWithException() throws Exception {
        thrown.expect(AssertionError.class);
        when(reviewService.checkReviewExist(1)).thenThrow(Exception.class);
        ResponseEntity<List<Review>>  result = reviewController.checkReviewExist(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testShowHideReview(){
        ResponseEntity<Boolean> result = reviewController.showHideReview(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testShowHideReviewWithException() throws Exception {
        ReflectionTestUtils.setField(reviewController, "reviewService", null);

        ResponseEntity<Boolean> result = reviewController.showHideReview(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testCreateTravelerReview(){
        TravelerReview review = Mockito.mock(TravelerReview.class);
        ResponseEntity<TravelerReview> result = reviewController.createTravelerReview(review);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testCreateTravelerReviewWithException() throws Exception {
        ReflectionTestUtils.setField(reviewController, "reviewService", null);
        TravelerReview review = Mockito.mock(TravelerReview.class);
        ResponseEntity<TravelerReview> result = reviewController.createTravelerReview(review);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testShowTravelerReview(){

        ResponseEntity<List<TravelerReview>> result = reviewController.showTravelerReview(1,1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testShowTravelerReviewWithException() throws Exception {
        ReflectionTestUtils.setField(reviewController, "reviewService", null);
        ResponseEntity<List<TravelerReview>> result = reviewController.showTravelerReview(1,1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testfindReviewByAdmin(){

        ResponseEntity<List<Review>> result = reviewController.findReviewByPostIdAdmin(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testfindReviewByAdminwWithException() throws Exception {
        ReflectionTestUtils.setField(reviewController, "reviewService", null);
        ResponseEntity<List<Review>> result = reviewController.findReviewByPostIdAdmin(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
}
