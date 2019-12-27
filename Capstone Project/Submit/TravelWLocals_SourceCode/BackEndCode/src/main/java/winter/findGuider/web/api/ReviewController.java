package winter.findGuider.web.api;

import entities.Review;
import entities.TravelerReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.Review.ReviewService;
import services.contributionPoint.ContributionPointService;
import services.trip.TripService;

import java.util.List;

@RestController
@RequestMapping(path = "/review", produces = "application/json")
@CrossOrigin(origins = "*")
public class ReviewController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ReviewService reviewService;
    private ContributionPointService contributionPointService;
    private TripService tripService;

    @Autowired
    public ReviewController(ReviewService rs, ContributionPointService cps, TripService ts) {
        this.reviewService = rs;
        this.contributionPointService = cps;
        this.tripService = ts;
    }

    @RequestMapping("/reviewByGuiderId")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Review>> findReviewByGuiderId(@RequestParam("guider_id") long guider_id) {
        try {
            return new ResponseEntity<>(reviewService.findReviewsByGuiderId(guider_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/reviewByPostId")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Review>> findReviewByPostId(@RequestParam("post_id") long post_id,
                                                           @RequestParam("page") long page) {
        try {
            return new ResponseEntity<>(reviewService.findReviewsByPostId(post_id, page), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/reviewByPostIdAdmin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Review>> findReviewByPostIdAdmin(@RequestParam("post_id") long post_id) {
        try {
            return new ResponseEntity<>(reviewService.findReviewsByPostIdAdmin(post_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> createReview(@RequestBody Review newReview) {
        try {
            boolean result = reviewService.createReview(newReview);
            if (result) {
                long post_id = tripService.findTripById((int) newReview.getTrip_id()).getPost_id();
                contributionPointService.calculatePostAndGuiderRating(post_id);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/checkExist")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Review>> checkReviewExist(@RequestParam("trip_id") long trip_id) {
        try {
            boolean isExist = reviewService.checkReviewExist(trip_id);
            if (isExist) {
                return new ResponseEntity<>(reviewService.findReviewByOrderId(trip_id), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/showHideReview")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> showHideReview(@RequestParam("trip_id") long trip_id) {
        try {
            reviewService.showHideReview(trip_id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/createTravelerReview")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TravelerReview> createTravelerReview(@RequestBody TravelerReview review) {
        try {
            long id = reviewService.createTravelerReview(review);
            return new ResponseEntity<>(reviewService.findTravelerReviewById(id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/showTravelerReview")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TravelerReview>> showTravelerReview(@RequestParam("traveler_id") long traveler_id,
                                                                   @RequestParam("page") long page) {
        try {
            return new ResponseEntity<>(reviewService.findReviewOfATraveler(traveler_id, page), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
