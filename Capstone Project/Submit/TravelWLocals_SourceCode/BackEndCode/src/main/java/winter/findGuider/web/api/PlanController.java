package winter.findGuider.web.api;

import entities.Plan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.plan.PlanService;

@RestController
@RequestMapping(path = "/plan", produces = "application/json")
@CrossOrigin(origins = "*")
public class PlanController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private PlanService planService;

    @Autowired
    public PlanController(PlanService ps) {
        this.planService = ps;
    }

    @RequestMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> createPlan(@RequestBody Plan plan) {
        try {
            int createdId = planService.createPlan(plan);
            return new ResponseEntity<>(createdId, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Plan> updatePlan(@RequestBody Plan plan) {
        try {   
            planService.updatePlan(plan.getPost_id(), plan.getMeeting_point(), plan.getDetail());
            return new ResponseEntity<>(planService.searchPlanByPostId(plan.getPost_id()), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @RequestMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Plan> findPlan(@PathVariable("post_id") int post_id) {
        try {
            return new ResponseEntity<>(planService.searchPlanByPostId(post_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
