package services.trip;

import com.paypal.base.rest.PayPalRESTException;
import entities.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import services.Paypal.PaypalService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class TripServiceImpl implements TripService {

    private static final String UNCONFIRMED = "WAITING";
    private static final String ONGOING = "ONGOING";
    private static final String FINISHED = "FINISHED";
    private static final String CANCELLED = "CANCELLED";

    private static final String HOUR_TAIL_0 = ":00";
    private static final String HOUR_TAIL_30 = ":30";
    private static final String HOUR_POSITION_BEFORE = "before";
    private static final String HOUR_POSITION_AFTER = "after";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private int LIMIT = 5;
    private JdbcTemplate jdbcTemplate;

    @Value("${order.buffer}")
    private String bufferPercent;
    @Autowired
    private PaypalService paypalService;

    @Autowired
    public TripServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createTrip(Order newOrder) throws Exception {
        String insertQuery = "insert into trip (traveler_id,post_id,begin_date,finish_date,adult_quantity,children_quantity,fee_paid,transaction_id,status)"
                + "values (?,?,?,?,?,?,?,?,?)";
        double totalHour = this.getTourTotalHour(newOrder.getPost_id());
        long bufferHour = (long) java.lang.Math.ceil(totalHour / 100 * Integer.parseInt(bufferPercent));
        jdbcTemplate.update(insertQuery, newOrder.getTraveler_id(), newOrder.getPost_id(),
                Timestamp.valueOf(newOrder.getBegin_date()),
                Timestamp.valueOf(newOrder.getFinish_date().plusHours(bufferHour).minusMinutes(30)),
                newOrder.getAdult_quantity(), newOrder.getChildren_quantity(), newOrder.getFee_paid(),
                newOrder.getTransaction_id(), UNCONFIRMED);
    }

    @Override
    public Order findTripById(long trip_id) throws Exception {
        Order searchOrder = new Order();
        String query = "select *, post.guider_id from trip " +
                "inner join post on post.post_id = trip.post_id " +
                "where trip_id = ?";
        searchOrder = jdbcTemplate.queryForObject(query, new RowMapper<Order>() {
            @Override
            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Order(rs.getInt("trip_id"), rs.getInt("traveler_id"),
                        rs.getInt("guider_id"), rs.getInt("post_id"),
                        rs.getTimestamp("begin_date").toLocalDateTime(),
                        rs.getTimestamp("finish_date").toLocalDateTime(),
                        rs.getInt("adult_quantity"), rs.getInt("children_quantity"),
                        rs.getLong("fee_paid"), rs.getString("transaction_id"),
                        rs.getString("status"));
            }
        }, trip_id);
        return searchOrder;
    }

    @Override
    public List<Order> findTripByStatus(String role, int id, String status, long page) throws Exception {
        List<Order> result;
        String query;
        if (role.equalsIgnoreCase("guider")) {
            query = "SELECT o.*, p.guider_id, p.title, t.first_name, t.last_name FROM trip as o "
                    + " inner join traveler as t on o.traveler_id = t.traveler_id "
                    + " inner join post as p on p.post_id = o.post_id "
                    + " where p.guider_id = ? and status = ? "
                    + " order by begin_date limit ? offset ?";
        } else if (role.equalsIgnoreCase("traveler")) {
            query = "SELECT o.*, p.guider_id, p.title, g.first_name, g.last_name "
                    + "FROM trip as o "
                    + "inner join post as p on p.post_id = o.post_id "
                    + "inner join guider as g on p.guider_id = g.guider_id "
                    + "where o.traveler_id = ? and status = ? "
                    + "order by begin_date limit ? offset ?";
        } else {
            throw new Exception("wrong role");
        }

        result = jdbcTemplate.query(query, new RowMapper<Order>() {
            @Override
            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Order(rs.getInt("trip_id"),
                        rs.getInt("traveler_id"),
                        rs.getInt("guider_id"),
                        rs.getInt("post_id"),
                        rs.getTimestamp("begin_date").toLocalDateTime(),
                        rs.getTimestamp("finish_date").toLocalDateTime(),
                        rs.getInt("adult_quantity"),
                        rs.getInt("children_quantity"),
                        rs.getDouble("fee_paid"),
                        rs.getString("transaction_id"),
                        rs.getString("status"),
                        rs.getString("title"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getTimestamp("book_time").toLocalDateTime());
            }
        }, id, status, LIMIT, page * LIMIT);
        return result;
    }

    @Override
    public int findTripByStatusPageCount(String role, int id, String status) throws Exception {
        String query;
        if (role.equalsIgnoreCase("guider")) {
            query = "SELECT count(trip_id) FROM trip as o "
                    + " inner join traveler as t on o.traveler_id = t.traveler_id "
                    + " inner join post as p on p.post_id = o.post_id "
                    + " where p.guider_id = ? and status = ?";
        } else if (role.equalsIgnoreCase("traveler")) {
            query = "SELECT count(trip_id) FROM trip as o "
                    + "inner join post as p on p.post_id = o.post_id "
                    + "inner join guider as g on p.guider_id = g.guider_id "
                    + "where o.traveler_id = ? and status = ?";
        } else {
            throw new Exception("wrong role");
        }
        double count = jdbcTemplate.queryForObject(query, new Object[]{id, status}, double.class);
        int page = (int) Math.ceil(count / LIMIT);
        return page;
    }

    @Override
    public boolean acceptTrip(long trip_id) throws Exception {
        String check = "select count(trip_id) from trip where trip_id = ? and status = ?";
        int count = jdbcTemplate.queryForObject(check, new Object[]{trip_id, UNCONFIRMED}, int.class);
        if (count == 0) {
            return false;
        }
        String query = "update trip set status = ? where trip_id = ?";
        jdbcTemplate.update(query, ONGOING, trip_id);
        return true;
    }

    @Override
    public boolean cancelTrip(long trip_id) throws Exception {
        // check trip exist
        String check = "select count(trip_id) from trip where trip_id = ? and status = ? or status = ?";
        int count = jdbcTemplate.queryForObject(check, new Object[]{trip_id, UNCONFIRMED, ONGOING}, int.class);
        if (count == 0) {
            return false;
        }
        // check finish time
        LocalDateTime rightNow = LocalDateTime.now();
        String getFinishDateQuery = "select finish_date from trip where trip_id = ?";
        LocalDateTime finishDate = jdbcTemplate.queryForObject(getFinishDateQuery, new Object[]{trip_id}, LocalDateTime.class);
        if (finishDate.getDayOfMonth() > rightNow.getDayOfMonth()) {
            return false;
        } else if (finishDate.getDayOfMonth() == rightNow.getDayOfMonth()) {
            if (finishDate.getHour() > rightNow.getHour()) {
                return false;
            } else if (finishDate.getHour() == rightNow.getHour()) {
                if (finishDate.getMinute() > rightNow.getMinute()) {
                    return false;
                }
            }
        }
        // Cancel trip
        String query = "update trip set status = ? where trip_id = ?";
        jdbcTemplate.update(query, CANCELLED, trip_id);
        return true;
    }

    @Override
    public boolean finishTrip(long trip_id) throws Exception {
        String check = "select count(trip_id) from trip where trip_id = ? and status = ?";
        int count = jdbcTemplate.queryForObject(check, new Object[]{trip_id, ONGOING}, int.class);
        if (count == 0) {
            return false;
        }
        String query = "update trip set status = ? where trip_id = ?";
        jdbcTemplate.update(query, FINISHED, trip_id);
        return true;
    }

    @Override
    public void getTripGuiderId_FinishDate(Order newOrder) throws Exception {
        String query = "SELECT guider_id, total_hour FROM post where post_id = ?";
        jdbcTemplate.queryForObject(query, new RowMapper<Order>() {
            @Override
            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                newOrder.setGuider_id(rs.getInt("guider_id"));
                Timestamp finishDate = Timestamp.valueOf(newOrder.getBegin_date().plusHours(rs.getLong("total_hour")));
                newOrder.setFinish_date(finishDate.toLocalDateTime());
                return newOrder;
            }
        }, newOrder.getPost_id());
    }

    @Override
    public int checkTripExist(long id) throws Exception {
        int count = 0;
        String query = "SELECT t1.begin_date , t1.finish_date, p2.guider_id FROM trip as t1"
                + " inner join post as p2 on t1.post_id = p2.post_id "
                + " where t1.trip_id = ? ";
        Map<String, Object> times = jdbcTemplate.queryForMap(query, id);

        query = "SELECT count (trip_id) FROM trip as t1 "
                + "inner join post as p2 on  t1.post_id = p2.post_id  "
                + "where (p2.guider_id = ?) and (t1.status = ?) "
                + "and ((t1.begin_date between ? and ?) "
                + "or (t1.finish_date between ? and ?))";
        count = jdbcTemplate.queryForObject(query, new Object[]{times.get("guider_id"), ONGOING,
                times.get("begin_date"), times.get("finish_date"),
                times.get("begin_date"), times.get("finish_date")}, int.class);
        return count;
    }

    @Override
    public int checkAvailabilityOfTrip(Order newOrder) throws Exception {
        int count = 0;
        String query = "SELECT count (trip_id) FROM trip "
                + "inner join post on post.post_id = trip.post_id "
                + "where (post.guider_id = ?) and (status = ?) "
                + "and ((begin_date between ? and ?) "
                + "or (finish_date between ? and ?))";
        int guider_id = newOrder.getGuider_id();
        Timestamp acceptableBeginDate = Timestamp.valueOf(newOrder.getBegin_date());
        Timestamp acceptableFinishDate = Timestamp.valueOf(newOrder.getFinish_date());
        count = jdbcTemplate.queryForObject(query, new Object[]{guider_id, ONGOING, acceptableBeginDate,
                acceptableFinishDate, acceptableBeginDate, acceptableFinishDate}, int.class);
        return count;
    }

    @Override
    public ArrayList<String> getGuiderAvailableHours(LocalDate chosenDate, int post_id, int guider_id) throws Exception {
        // Create default available hours
        ArrayList<String> availableHours = new ArrayList<>();
        this.createHourArray(availableHours, 0, 24);

        // check if day in mid tour
        boolean isMidTour = this.checkDayInMidTour(guider_id, chosenDate);
        if (isMidTour) {
            availableHours.clear();
            return availableHours;
        }

        // Get guider schedule
        List<Order> guiderSchedule = this.getGuiderSchedule(guider_id, chosenDate);
        if (!guiderSchedule.isEmpty()) {
            // Get occupy hours
            ArrayList<String> occupyHour = this.getOccupyHours(guiderSchedule, chosenDate);
            // Clear out occupy hours from available hours
            availableHours.removeAll(occupyHour);
        }

        // Get unacceptable hours
        ArrayList<String> unacceptableHours = this.getUnacceptableHours(post_id, guider_id, availableHours, chosenDate);
        if (!unacceptableHours.isEmpty()) {
            availableHours.removeAll(unacceptableHours);
        }

        return availableHours;
    }

    @Override
    public String getClosestTripFinishDate(LocalDate date, int guider_id) throws Exception {
        String closestFinishDate = "";
        String query = "SELECT finish_date FROM trip "
                + "inner join post on post.post_id = trip.post_id "
                + "where post.guider_id = ? and finish_date < ? "
                + "and status = ? "
                + "order by finish_date desc "
                + "limit 1";
        List<String> result = jdbcTemplate.query(query, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        }, guider_id, date, FINISHED);
        if (result == null || result.isEmpty()) {
            return "a very long time ago";
        } else {
            closestFinishDate = result.get(0);
        }
        closestFinishDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(closestFinishDate));
        return closestFinishDate;
    }

    @Override
    public boolean checkTripReach24Hours(Order cancelOrder, LocalDateTime rightNow) throws Exception {
        LocalDateTime boundaryDay = cancelOrder.getBegin_date().minusDays(1);
        // check day
        if (rightNow.getDayOfMonth() < boundaryDay.getDayOfMonth()) {
            return false;
        } else if (rightNow.getDayOfMonth() > boundaryDay.getDayOfMonth()) {
            return true;
        } else {
            if (rightNow.getHour() < boundaryDay.getHour()) {
                return false;
            } else if (rightNow.getHour() > boundaryDay.getHour()) {
                return true;
            } else {
                if (rightNow.getMinute() < boundaryDay.getMinute()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    @Override
    public String getExpectedEndTripTime(int post_id, LocalDateTime begin_date) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        double totalHour = this.getTourTotalHour(post_id);
        long bufferHour = (long) java.lang.Math.ceil(totalHour / 100 * Integer.parseInt(bufferPercent));
        long duration = (long) totalHour + bufferHour;
        LocalDateTime end_date = begin_date.plusHours(duration);
        return end_date.format(formatter);
    }

    private double getTourTotalHour(int post_id) throws Exception {
        double total_hour = 0;
        String query = "SELECT total_hour FROM post where post_id = ?";
        total_hour = (double) jdbcTemplate.queryForObject(query, new Object[]{post_id}, int.class);
        return total_hour;
    }

    private void createHourArray(ArrayList<String> array, int startHour, int endHour) throws Exception {
        String[] hourTails = {HOUR_TAIL_0, HOUR_TAIL_30};
        for (int i = startHour; i < endHour; i++) {
            for (int j = 0; j < 2; j++) {
                String availableTime = i + hourTails[j];
                if (i < 10) {
                    availableTime = "0" + availableTime;
                }
                if (!array.contains(availableTime)) {
                    array.add(availableTime);
                }
            }
        }
    }

    private boolean checkDayInMidTour(long guider_id, LocalDate date) throws Exception {
        String query = "SELECT count(trip_id) FROM trip "
                + "inner join post on trip.post_id = post.post_id "
                + "where post.guider_id = ? and status = ? "
                + "and date(begin_date) < ? and date(finish_date) > ? ";
        int count = jdbcTemplate.queryForObject(query, new Object[]{guider_id, ONGOING, date, date}, int.class);
        if (count != 0) {
            return true;
        } else {
            return false;
        }
    }

    private List<Order> getGuiderSchedule(int guider_id, LocalDate date) throws Exception {
        List<Order> guiderSchedule = new ArrayList<>();
        String query = "SELECT trip_id, begin_date, finish_date FROM trip "
                + "inner join post on trip.post_id = post.post_id "
                + "where post.guider_id = ? and status = ? "
                + "and (Date(begin_date) = ? "
                + "or Date(finish_date) = ?) "
                + "order by begin_date";
        guiderSchedule = jdbcTemplate.query(query, new RowMapper<Order>() {
            @Override
            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                Order temp = new Order();
                temp.setBegin_date(rs.getTimestamp("begin_date").toLocalDateTime());
                temp.setFinish_date(rs.getTimestamp("finish_date").toLocalDateTime());
                return temp;
            }
        }, guider_id, ONGOING, date, date);
        return guiderSchedule;
    }

    private ArrayList<String> getOccupyHours(List<Order> guiderSchedule, LocalDate date) throws Exception {
        ArrayList<String> occupyHour = new ArrayList<>();
        for (Order order : guiderSchedule) {
            int beginHour = order.getBegin_date().getHour();
            int finishHour = order.getFinish_date().getHour();
            int beginMinute = order.getBegin_date().getMinute();
            int finishMinute = order.getFinish_date().getMinute();

            if (order.getBegin_date().getDayOfMonth() < date.getDayOfMonth()) {
                this.createHourArray(occupyHour, 0, finishHour + 1);
                if (finishMinute == 0) {
                    this.removeHour(occupyHour, finishHour, HOUR_POSITION_AFTER);
                }
            } else if (order.getFinish_date().getDayOfMonth() > date.getDayOfMonth()) {
                boolean frontExist = this.checkDuplicateHours(occupyHour, beginHour);
                this.createHourArray(occupyHour, beginHour, 24);
                if (!frontExist) {
                    if (beginMinute != 0) {
                        this.removeHour(occupyHour, beginHour, HOUR_POSITION_BEFORE);
                    }
                }
            } else {
                boolean frontExist = this.checkDuplicateHours(occupyHour, beginHour);
                this.createHourArray(occupyHour, beginHour, finishHour + 1);
                if (beginMinute != 0 && finishMinute == 0) {
                    if (!frontExist) {
                        this.removeHour(occupyHour, beginHour, HOUR_POSITION_BEFORE);
                    }
                    this.removeHour(occupyHour, finishHour, HOUR_POSITION_AFTER);
                } else if (beginMinute == 0 && finishMinute == 0) {
                    this.removeHour(occupyHour, finishHour, HOUR_POSITION_AFTER);
                } else if (beginMinute != 0 && finishMinute != 0) {
                    if (!frontExist) {
                        this.removeHour(occupyHour, beginHour, HOUR_POSITION_BEFORE);
                    }
                }
            }
        }
        Collections.sort(occupyHour);
        return occupyHour;
    }

    private ArrayList<String> getUnacceptableHours(int post_id, int guider_id, ArrayList<String> availableHours,
                                                   LocalDate chosenDate) throws Exception {

        ArrayList<String> unacceptableHours = new ArrayList<>();
        double totalHour = this.getTourTotalHour(post_id);
        long bufferHour = (long) java.lang.Math.ceil(totalHour / 100 * Integer.parseInt(bufferPercent));
        long duration = (long) totalHour + bufferHour;
        for (String hour : availableHours) {
            LocalDateTime checkDate = LocalDateTime.parse(chosenDate + "T" + hour);
            checkDate = checkDate.plusHours(duration);
            String query = "SELECT count(trip_id) FROM trip " +
                    "inner join post on trip.post_id = post.post_id " +
                    "where post.guider_id = ? and status = ? " +
                    "and (begin_date <= ? " +
                    "and finish_date >= ?)";
            int count = jdbcTemplate.queryForObject(query, new Object[]{guider_id, ONGOING, checkDate, checkDate}, int.class);
            if (count != 0) {
                unacceptableHours.add(hour);
            }
        }
        return unacceptableHours;
    }

    private void removeHour(ArrayList<String> array, int hour, String type) throws Exception {
        if (hour < 10) {
            array.remove("0" + hour + (type.equals(HOUR_POSITION_BEFORE) ? HOUR_TAIL_0 : HOUR_TAIL_30));
        } else {
            array.remove(hour + (type.equals(HOUR_POSITION_BEFORE) ? HOUR_TAIL_0 : HOUR_TAIL_30));
        }
    }

    private boolean checkDuplicateHours(ArrayList<String> occupyHour, int hour) throws Exception {
        if (hour < 10) {
            if (occupyHour.contains("0" + hour + HOUR_TAIL_0) || occupyHour.contains("0" + hour + HOUR_TAIL_30)) {
                return true;
            }
        } else {
            if (occupyHour.contains(hour + HOUR_TAIL_0) || occupyHour.contains(hour + HOUR_TAIL_30)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Order> getTripByWeek(int id, Date start, Date end) throws Exception {
        List<Order> result = new ArrayList<>();
        String query = "SELECT o.*, p.guider_id, p.title, t.first_name, t.last_name FROM trip as o "
                + " inner join traveler as t on o.traveler_id = t.traveler_id "
                + " inner join post as p on p.post_id = o.post_id "
                + " where p.guider_id = ? and (o.status = 'ONGOING') "
                + " and ((o.begin_date between ? and ?) or (o.finish_date between ? and ?) )"
                + " order by o.begin_date ; ";

        result = jdbcTemplate.query(query, new RowMapper<Order>() {
            @Override
            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Order(rs.getInt("trip_id"),
                        rs.getInt("traveler_id"),
                        rs.getInt("guider_id"),
                        rs.getInt("post_id"),
                        rs.getTimestamp("begin_date").toLocalDateTime(),
                        rs.getTimestamp("finish_date").toLocalDateTime(),
                        rs.getInt("adult_quantity"),
                        rs.getInt("children_quantity"),
                        rs.getDouble("fee_paid"),
                        rs.getString("transaction_id"),
                        rs.getString("status"),
                        rs.getString("title"),
                        rs.getString("first_name") + " " + rs.getString("last_name"));
            }
        }, id, new Timestamp(start.getTime()), new Timestamp(end.getTime()), new Timestamp(start.getTime()), new Timestamp(end.getTime()));

        return result;
    }

    @Scheduled(cron = "0 0 * * * *")
    //@Scheduled(cron = "0 0/5 * 1/1 * *")
    public void cancelTripFilter() throws PayPalRESTException {
        List<Map<String, Object>> lo = new ArrayList<>();
        String query = "select trip_id, transaction_id from trip "
                + " where extract (epoch from (now() - book_time))::integer "
                + " > extract(epoch from TIMESTAMP '1970-1-1 05:00:00')::integer "
                + " and status = 'WAITING'; ";
        lo = jdbcTemplate.queryForList(query);

        List<String> update = new ArrayList<>();
        for (Map m : lo) {
            paypalService.refundPayment(m.get("transaction_id").toString());
            update.add("update trip set status = 'CANCELLED' where trip_id = " + m.get("trip_id"));
        }
        logger.warn(update.toString());

        String[] updateList = update.toArray(new String[0]);
        if (updateList.length > 0) {
            jdbcTemplate.batchUpdate(updateList);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    //@Scheduled(cron = "0 0/5 * 1/1 * *")
    public void finishTripFilter() {
        List<Map<String, Object>> lo = new ArrayList<>();
        String query = "select trip_id from trip "
                + " where extract (epoch from (now() - finish_date))::integer "
                + " <= extract(epoch from TIMESTAMP '1970-1-2 00:00:00')::integer "
                + " and now() > finish_date "
                + " and status = 'ONGOING'; ";
        lo = jdbcTemplate.queryForList(query);

        List<String> update = new ArrayList<>();
        for (Map m : lo) {

            update.add("update trip set status = 'FINISHED' where trip_id = " + m.get("trip_id"));
        }
        logger.warn(update.toString());

        String[] updateList = update.toArray(new String[0]);
        if (updateList.length > 0) {
            jdbcTemplate.batchUpdate(updateList);
        }
    }

}
