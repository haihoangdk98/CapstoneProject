package winter.findGuider.web.api;

import com.paypal.api.payments.Refund;
import com.paypal.base.rest.PayPalRESTException;
import entities.InDayTrip;
import entities.Notification;
import entities.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.Mail.MailService;
import services.Paypal.PaypalService;
import services.Post.PostService;
import services.account.AccountRepository;
import services.contributionPoint.ContributionPointService;
import services.guider.GuiderService;
import services.trip.TripService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping(path = "/Order", produces = "application/json")
@CrossOrigin(origins = "*")
public class TripController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private TripService tripService;
    private PaypalService paypalService;
    private MailService mailService;
    private ContributionPointService contributionPointService;
    private GuiderService guiderService;
    private AccountRepository accountRepository;
    private PostService postService;
    private WebSocketNotificationController webSocketNotificationController;
    @Value("${order.buffer}")
    private String bufferPercent;

    @Autowired
    public TripController(TripService os, PaypalService ps, MailService ms,
                          ContributionPointService cps, GuiderService gs,
                          AccountRepository ar, PostService postService, WebSocketNotificationController wsc) {
        this.tripService = os;
        this.paypalService = ps;
        this.mailService = ms;
        this.contributionPointService = cps;
        this.guiderService = gs;
        this.accountRepository = ar;
        this.postService = postService;
        this.webSocketNotificationController = wsc;
    }

    @RequestMapping("/GetAvailableHours")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ArrayList<String>> getAvailableBookingHour(@RequestBody Order newOrder) {
        try {
            ArrayList<String> availableHour = tripService.getGuiderAvailableHours(newOrder.getBegin_date().toLocalDate(),
                    newOrder.getPost_id(), newOrder.getGuider_id());
            return new ResponseEntity<>(availableHour, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/GetOrderByStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Order>> getOrderByStatus(@RequestParam("role") String role, @RequestParam("id") int id,
                                                        @RequestParam("status") String status, @RequestParam("page") long page) {
        try {
            return new ResponseEntity<>(tripService.findTripByStatus(role, id, status, page), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/GetOrderByStatusPageCount")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> GetOrderByStatusPageCount(@RequestParam("role") String role, @RequestParam("id") int id,
                                                             @RequestParam("status") String status) {
        try {
            return new ResponseEntity<>(tripService.findTripByStatusPageCount(role, id, status), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/GetClosestFinishDate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getClosestFinishDate(@RequestBody Order newOrder) {
        try {
            String finishDate = tripService.getClosestTripFinishDate(newOrder.getBegin_date().toLocalDate(),
                    newOrder.getGuider_id());
            return new ResponseEntity<>(finishDate, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/checkCanBookTrip")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> CheckCanBookTrip(@RequestBody Order newOrder) {
        try {
            // Check for availability of order
            boolean canBook;
            int count = tripService.checkAvailabilityOfTrip(newOrder);
            if (count != 0) {
                canBook = false;
            } else {
                canBook = true;
            }
            return new ResponseEntity<>(canBook, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/refuseTrip/{trip_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> refuseTrip(@PathVariable("trip_id") long trip_id) {
        Order cancelOrder = new Order();
        try {
            cancelOrder = tripService.findTripById(trip_id);
            // start cancel order
            boolean cancelSuccess;
            Refund refund = paypalService.refundPayment(cancelOrder.getTransaction_id());
            if (refund.getState().equals("completed")) {
                paypalService.createRefundRecord(cancelOrder.getTransaction_id(), "success");
                cancelSuccess = tripService.cancelTrip(cancelOrder.gettrip_id());
                if (!cancelSuccess) {
                    return new ResponseEntity<>("Cancel Fail", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Refund fail", HttpStatus.NOT_FOUND);
            }

            // send notification
            DateTimeFormatter formatterForNoti = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime current = LocalDateTime.now();
            current = LocalDateTime.parse(current.format(formatterForNoti));
            String traveler_username = accountRepository.findAccountNameByAccountId(cancelOrder.getTraveler_id());
            String guider_username = accountRepository.findAccountNameByAccountId(cancelOrder.getGuider_id());
            Notification notification = new Notification();
            notification.setUser(guider_username);
            notification.setReceiver(traveler_username);
            notification.setType("Notification");
            notification.setSeen(false);
            notification.setDateReceived(current);
            notification.setContent("CANCELLATION: The order on tour " + postService.findSpecificPost(cancelOrder.getPost_id()).getTitle() + " was canceled by guider " + guider_username);
            webSocketNotificationController.sendMessage(notification);
            return new ResponseEntity<>("Cancel Success", HttpStatus.OK);
        } catch (PayPalRESTException e) {
            try {
                String message = e.getDetails().getMessage();
                paypalService.createRefundRecord(cancelOrder.getTransaction_id(), message);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            } catch (Exception exc) {
                logger.error(exc.getMessage());
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/CancelOrderAsTraveler")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> cancelOrderAsTraveler(@RequestParam("trip_id") int trip_id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime rightNow = LocalDateTime.now();
        rightNow = LocalDateTime.parse(rightNow.format(formatter));
        Order cancelOrder = new Order();
        try {
            cancelOrder = tripService.findTripById(trip_id);
            // check if refund is needed
            boolean isRefund = !tripService.checkTripReach24Hours(cancelOrder, rightNow);
            // start cancel order
            boolean cancelSuccess;
            if (isRefund) {
                Refund refund = paypalService.refundPayment(cancelOrder.getTransaction_id());
                if (refund.getState().equals("completed")) {
                    paypalService.createRefundRecord(cancelOrder.getTransaction_id(), "success");
                    cancelSuccess = tripService.cancelTrip(cancelOrder.gettrip_id());
                    if (!cancelSuccess) {
                        return new ResponseEntity<>("Cancel Fail", HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>("Refund fail", HttpStatus.NOT_FOUND);
                }
            } else {
                cancelSuccess = tripService.cancelTrip(cancelOrder.gettrip_id());
                if (!cancelSuccess) {
                    return new ResponseEntity<>("Cancel Fail", HttpStatus.NOT_FOUND);
                }
            }

            // send notification
            DateTimeFormatter formatterForNoti = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime current = LocalDateTime.now();
            current = LocalDateTime.parse(current.format(formatterForNoti));
            String traveler_username = accountRepository.findAccountNameByAccountId(cancelOrder.getTraveler_id());
            String guider_username = accountRepository.findAccountNameByAccountId(cancelOrder.getGuider_id());
            Notification notification = new Notification();
            notification.setUser(traveler_username);
            notification.setReceiver(guider_username);
            notification.setType("Notification");
            notification.setSeen(false);
            notification.setDateReceived(current);

            notification.setContent("CANCELLATION: The order on tour " + postService.findSpecificPost(cancelOrder.getPost_id()).getTitle() + " was canceled by traveler " + traveler_username);
            webSocketNotificationController.sendMessage(notification);
            return new ResponseEntity<>("Cancel Success", HttpStatus.OK);
        } catch (PayPalRESTException e) {
            try {
                String message = e.getDetails().getMessage();
                paypalService.createRefundRecord(cancelOrder.getTransaction_id(), message);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            } catch (Exception exc) {
                logger.error(exc.getMessage());
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/CancelOrderAsGuider")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> cancelOrderAsGuider(@RequestParam("trip_id") int trip_id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime rightNow = LocalDateTime.now();
        rightNow = LocalDateTime.parse(rightNow.format(formatter));
        Order cancelOrder = new Order();
        try {
            cancelOrder = tripService.findTripById(trip_id);
            // check if penalty is needed
            boolean isPenalty = tripService.checkTripReach24Hours(cancelOrder, rightNow);
            // start cancel order
            boolean cancelSuccess;
            // refund traveler
            Refund refund = paypalService.refundPayment(cancelOrder.getTransaction_id());
            if (refund.getState().equals("completed")) {
                paypalService.createRefundRecord(cancelOrder.getTransaction_id(), "success");
            } else {
                return new ResponseEntity<>("Refund fail", HttpStatus.NOT_FOUND);
            }
            // penalty guider contribution point
            if (isPenalty) {
                int guiderId = (int) guiderService.findGuiderWithPostId(cancelOrder.getPost_id()).getGuider_id();
                contributionPointService.penaltyGuiderForCancel(guiderId);
                cancelSuccess = tripService.cancelTrip(cancelOrder.gettrip_id());
                if (!cancelSuccess) {
                    return new ResponseEntity<>("Cancel Fail", HttpStatus.NOT_FOUND);
                }
            } else {
                cancelSuccess = tripService.cancelTrip(cancelOrder.gettrip_id());
                if (!cancelSuccess) {
                    return new ResponseEntity<>("Cancel Fail", HttpStatus.NOT_FOUND);
                }
            }

            // send notification
            DateTimeFormatter formatterForNoti = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime current = LocalDateTime.now();
            current = LocalDateTime.parse(current.format(formatterForNoti));
            String traveler_username = accountRepository.findAccountNameByAccountId(cancelOrder.getTraveler_id());
            String guider_username = accountRepository.findAccountNameByAccountId(cancelOrder.getGuider_id());
            Notification notification = new Notification();
            notification.setUser(guider_username);
            notification.setReceiver(traveler_username);
            notification.setType("Notification");
            notification.setSeen(false);
            notification.setDateReceived(current);
            notification.setContent("CANCELLATION: Your order on tour " + postService.findSpecificPost(cancelOrder.getPost_id()).getTitle() + " of guider " + guider_username + " was canceled");
            webSocketNotificationController.sendMessage(notification);

            Order order = tripService.findTripById(trip_id);
            boolean isMailVerified = accountRepository.isMailVerified(order.getTraveler_id());
            if (isMailVerified) {
                String email = accountRepository.getEmail(order.getTraveler_id());
                String content = mailService.getMailContent(order, "CANCELLED");
                mailService.sendMail(email, "TravelWLocal Tour Cancelled", content);
            }
            return new ResponseEntity<>("Cancel Success", HttpStatus.OK);
        } catch (PayPalRESTException e) {
            try {
                String message = e.getDetails().getMessage();
                paypalService.createRefundRecord(cancelOrder.getTransaction_id(), message);
                return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            } catch (Exception exc) {
                logger.error(exc.getMessage());
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/checktime")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> checkTime(@RequestBody Order order) {
        try {
            System.out.println(order.getGuider_id() + "" + order.getBegin_date() + order.getFinish_date());
            int count = tripService.checkAvailabilityOfTrip(order);
            System.out.println("dup " + count);
            if (count > 0) {
                throw new Exception("Booking: there is intersection in time");
            } else {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/AcceptOrder/{trip_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> acceptOrder(@PathVariable("trip_id") int orderId) {
        try {
            // Check for availability of order
            int count = tripService.checkTripExist(orderId);
            if (count != 0) {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }
            boolean result = tripService.acceptTrip(orderId);
            if (result) {
                Order order = tripService.findTripById(orderId);

                // send mail
                boolean isMailVerified = accountRepository.isMailVerified(order.getTraveler_id());
                if (isMailVerified) {
                    String email = accountRepository.getEmail(order.getTraveler_id());
                    String content = mailService.getMailContent(order, "ONGOING");
                    mailService.sendMail(email, "TravelWLocal Tour Accepted", content);
                }


                // send notification
                DateTimeFormatter formatterForNoti = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                LocalDateTime current = LocalDateTime.now();
                current = LocalDateTime.parse(current.format(formatterForNoti));
                String traveler_username = accountRepository.findAccountNameByAccountId(order.getTraveler_id());
                String guider_username = accountRepository.findAccountNameByAccountId(order.getGuider_id());
                Notification notification = new Notification();
                notification.setUser(guider_username);
                notification.setReceiver(traveler_username);
                notification.setType("Notification");
                notification.setSeen(false);
                notification.setDateReceived(current);

                notification.setContent("<ACCEPTED: Your order on tour " + postService.findSpecificPost(order.getPost_id()).getTitle() + " was accepted by guider " + guider_username);
                webSocketNotificationController.sendMessage(notification);

                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                throw new Exception("Accept False");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/getOrderByWeek/{guider_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<List<InDayTrip>>> getOrderByWeek(@PathVariable("guider_id") int id, @RequestBody Date order) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order);
            cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            Date start = cal.getTime();
            //System.out.println("Start of this week:       " + start);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            Date end = cal.getTime();
            //System.out.println("Start of the next week:   " + end);
            List<Order> lo = tripService.getTripByWeek(id, start, end);
            List<List<InDayTrip>> lli = new ArrayList();
            for (long dayStart = start.getTime(); dayStart < end.getTime(); dayStart = dayStart + 86400000) {
                List<InDayTrip> li = new ArrayList();
                long dayEnd = dayStart + 86400000;
                for (Order o : lo) {
                    if (Timestamp.valueOf(o.getBegin_date()).getTime() >= dayEnd
                            || Timestamp.valueOf(o.getFinish_date()).getTime() <= dayStart) {
                        continue;
                    } // trip not include this day
                    else if (Timestamp.valueOf(o.getBegin_date()).getTime() <= dayStart
                            && Timestamp.valueOf(o.getFinish_date()).getTime() <= dayEnd) {
                        //a trip not start but end within this day
                        li.add(new InDayTrip(o.getTraveler_id(), o.getPost_id(),
                                "00:00", o.getFinish_date().getHour() + ":" + o.getFinish_date().getMinute(),
                                o.getPostTitle(), o.getObject()));
                    } else if (Timestamp.valueOf(o.getBegin_date()).getTime() >= dayStart
                            && Timestamp.valueOf(o.getFinish_date()).getTime() <= dayEnd) {
                        //a trip take place within this day
                        li.add(new InDayTrip(o.getTraveler_id(), o.getPost_id(),
                                o.getBegin_date().getHour() + ":" + o.getBegin_date().getMinute(),
                                o.getFinish_date().getHour() + ":" + o.getFinish_date().getMinute(),
                                o.getPostTitle(), o.getObject()));
                    } else if (Timestamp.valueOf(o.getBegin_date()).getTime() >= dayStart
                            && Timestamp.valueOf(o.getFinish_date()).getTime() >= dayEnd) {
                        //a trip start but not end within this day
                        li.add(new InDayTrip(o.getTraveler_id(), o.getPost_id(),
                                o.getBegin_date().getHour() + ":" + o.getBegin_date().getMinute(), "24:00",
                                o.getPostTitle(), o.getObject()));
                    }
                    if (Timestamp.valueOf(o.getBegin_date()).getTime() <= dayStart
                            && Timestamp.valueOf(o.getFinish_date()).getTime() >= dayEnd) {
                        //a trip start include this day
                        li.add(new InDayTrip(o.getTraveler_id(), o.getPost_id(),
                                "00:00", "24:00",
                                o.getPostTitle(), o.getObject()));
                    }
                }
                lli.add(li);
            }
            return new ResponseEntity<>(lli, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/GetPossibleDayInMonth/{guider_id}/{duration}")
    public ResponseEntity<List<Long>> GetPossibleDayInMonth(@PathVariable("guider_id") int id,
                                                            @PathVariable("duration") long duration, @RequestBody Date order) {
        List<Long> ll = new ArrayList();
        //System.out.println("duration: "+ duration * 60 * 60 * 1000 );
        long exact = (long) Math.ceil(duration * 60 * 60 * 1000);
        long buffer = (long) Math.ceil(duration * 60 * 60 * 1000 * 0.3);
        //System.out.println("estimate: " + duration);
        try {
            //get begin  day
            Calendar cal = Calendar.getInstance();
            cal.setTime(order);
            cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            if ((cal.get(Calendar.MONTH) == 0)) {
                cal.set(Calendar.MONTH, 11);
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
            }
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date start = cal.getTime();
            //get finish
            cal.setTime(order);
            cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            if ((cal.get(Calendar.MONTH) == 11)) {
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
            }
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date end = cal.getTime();

            List<Order> lo = tripService.getTripByWeek(id, start, end);
            //System.out.println("trips: " + lo.size());
            List<java.util.Map.Entry<Long, Long>> avaiDuration = new ArrayList();
            //System.out.println("Start of this month:       " + start);
            //System.out.println("End of this month:       " + end);
            avaiDuration.add(new AbstractMap.SimpleEntry<>(new Long(start.getTime()), new Long(end.getTime())));
            //lo already order by begin_date
            //remove occupied period
            for (Order o : lo) {
                java.util.Map.Entry<Long, Long> entry = avaiDuration.get(avaiDuration.size() - 1);
                long startPoint = entry.getKey().longValue();
                long endPoint = entry.getValue().longValue();
                avaiDuration.remove(entry);
                long orderStart = Date.from(o.getBegin_date().atZone(ZoneId.of("UTC"))
                        .toInstant()).getTime() - buffer - exact;
                long orderFinish = Date.from(o.getFinish_date().atZone(ZoneId.of("UTC"))
                        .toInstant()).getTime() + buffer;
                //long orderFinish = Timestamp.valueOf(o.getFinish_date()).getTime() + buffer;
                //System.out.println("start Order:" + o.getBegin_date());
                //System.out.println("end Order:" + o.getFinish_date());
                avaiDuration.add(new AbstractMap.SimpleEntry<>(
                        startPoint,
                        (startPoint < orderStart) ? orderStart : startPoint));
                avaiDuration.add(new AbstractMap.SimpleEntry<>(
                        (endPoint < orderFinish) ? endPoint : orderFinish,
                        endPoint));
            }
            //System.out.println(avaiDuration.size());
            //filter period that qualified
            //get date from those perid
            for (java.util.Map.Entry<Long, Long> entry : avaiDuration) {
                long startPoint = entry.getKey().longValue();
                //System.out.println("start Date:" + Timestamp.valueOf(LocalDateTime.ofInstant(Instant.ofEpochMilli(startPoint), ZoneOffset.UTC)));
                long endPoint = entry.getValue().longValue();
                //System.out.println("end Date:" + Timestamp.valueOf(LocalDateTime.ofInstant(Instant.ofEpochMilli(endPoint), ZoneOffset.UTC)));
                if ((endPoint - startPoint) > 30 * 60) {
                    for (long i = (startPoint / 86400000); i <= endPoint / 86400000; i++) {
                        //System.out.println("add Date:" + Timestamp.valueOf(LocalDateTime.ofInstant(Instant.ofEpochMilli((i * 86400000)), ZoneOffset.UTC)));
                        ll.add(new Long(i * 86400000));
                    }
                }
            }
            return new ResponseEntity<>(ll, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ArrayList(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/GetExpectedTourEnd")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getExpectedTourEnd(@RequestBody Order order) {
        try {
            return new ResponseEntity<>(tripService.getExpectedEndTripTime(order.getPost_id(), order.getBegin_date()), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
