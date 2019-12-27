package winter.findGuider.web.api;

import entities.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.Location.LocationService;

@RestController
@RequestMapping(path = "/location", produces = "application/json")
@CrossOrigin(origins = "*")
public class LocationController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @RequestMapping("/findAll")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Location> findAllLocation() {
        try {
            return new ResponseEntity(locationService.showAllLocation(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> createLocation(@RequestParam("country") String country, @RequestParam("city") String city,
                                                  @RequestParam("place") String place) {
        try {
            locationService.createLocation(country, city, place);
            return new ResponseEntity(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(false, HttpStatus.NOT_FOUND);
        }
    }
}
