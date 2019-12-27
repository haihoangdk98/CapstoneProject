package winter.findGuider.web.api;

import entities.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.Statistic.StatisticService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(path = "/statistic", produces = "application/json")
@CrossOrigin(origins = "*")
public class StatisticController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private StatisticService statisticService;

    @Value("${order.client.root.url}")
    private String URL_ROOT_CLIENT;

    @Autowired
    public StatisticController(StatisticService ss) {
        this.statisticService = ss;
    }

    @RequestMapping("/completedTrip")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Statistic>> getStatisticCompletedTrip(HttpServletResponse response,
                                                                     @RequestBody Statistic sta) {
        try {
            response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return new ResponseEntity<>(statisticService.getStatisticCompletedTrip(sta.getFrom(), sta.getTo()), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/totalRevenue")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Statistic>> getStatisticTotalRevenue(HttpServletResponse response,
                                                                    @RequestBody Statistic sta) {
        try {
            response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return new ResponseEntity<>(statisticService.getStatisticTotalRevenue(sta.getFrom(), sta.getTo()), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/GuiderRevenue/{guider_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Statistic>> GuiderRevenue(HttpServletResponse response,
                                                         @RequestBody Statistic sta,
                                                         @PathVariable long guider_id) {
        try {
            response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return new ResponseEntity<>(statisticService.getStatisticGuiderRevenue(sta.getFrom(), sta.getTo(), guider_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
