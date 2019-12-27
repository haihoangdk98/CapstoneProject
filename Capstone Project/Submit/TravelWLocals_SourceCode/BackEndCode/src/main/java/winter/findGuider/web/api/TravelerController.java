package winter.findGuider.web.api;

import entities.Traveler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.traveler.TravelerService;

@RestController
@RequestMapping(path = "/Traveler", produces = "application/json")
@CrossOrigin(origins = "*")
public class TravelerController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private TravelerService travelerService;

    @Autowired
    public TravelerController(TravelerService ts) {
        this.travelerService = ts;
    }

    @RequestMapping("/GetTraveler")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Traveler> getTravelerWithId(@RequestParam("traveler_id") int traveler_id) {
        try {
            Traveler searchTraveler = travelerService.findTravelerWithId(traveler_id);
            if (searchTraveler == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(searchTraveler, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/Create")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> createTraveler(@RequestBody Traveler newTraveler) {
        try {
            boolean success = travelerService.createTraveler(newTraveler);
            return new ResponseEntity<>(success, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/Update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Traveler> updateTraveler(@RequestBody Traveler travelerNeedUpdate) {
        try {
            travelerService.updateTraveler(travelerNeedUpdate);
            return new ResponseEntity<>(travelerService.findTravelerWithId(travelerNeedUpdate.getTraveler_id()), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/favorite")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> favoritePost(@RequestParam("traveler_id") int traveler_id, @RequestParam("post_id") int post_id) {
        try {
            travelerService.favoritePost(traveler_id, post_id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/isLackingProfile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> isLackingProfile(@RequestParam("traveler_id") long traveler_id) {
        try {
            return new ResponseEntity<>(travelerService.isLackingProfile(traveler_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/updateLackingProfile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> updateLackingProfile(@RequestBody Traveler traveler) {
        try {
            travelerService.updateLackingProfile(traveler);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @RequestMapping("/unlike")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> unlikePost(@RequestParam("traveler_id") int traveler_id, @RequestParam("post_id") int post_id) {
        try {
            travelerService.unlikePost(traveler_id, post_id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
