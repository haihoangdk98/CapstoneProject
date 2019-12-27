package entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {
    private int trip_id;
    private int traveler_id;
    private int guider_id;
    private int post_id;

    @JsonFormat(pattern = "MM/dd/yyyy HH:mm")
    private LocalDateTime begin_date;

    @JsonFormat(pattern = "MM/dd/yyyy HH:mm")
    private LocalDateTime finish_date;

    private int adult_quantity;
    private int children_quantity;
    private double fee_paid;
    private String transaction_id;
    private String status;
    private float rated;
    private String postTitle;
    private String object;
    private LocalDateTime book_time;

    public Order() {
    }


    public Order(int trip_id, int traveler_id, int guider_id, int post_id, LocalDateTime begin_date, LocalDateTime finish_date, int adult_quantity, int children_quantity, double fee_paid, String transaction_id, String status) {
        this.trip_id = trip_id;
        this.traveler_id = traveler_id;
        this.guider_id = guider_id;
        this.post_id = post_id;
        this.begin_date = begin_date;
        this.finish_date = finish_date;
        this.adult_quantity = adult_quantity;
        this.children_quantity = children_quantity;
        this.fee_paid = fee_paid;
        this.transaction_id = transaction_id;
        this.status = status;
    }

    public Order(int trip_id, int traveler_id, int post_id, LocalDateTime begin_date, LocalDateTime finish_date, int adult_quantity, int children_quantity, double fee_paid, String transaction_id, String status) {
        this.trip_id = trip_id;
        this.traveler_id = traveler_id;
        this.post_id = post_id;
        this.begin_date = begin_date;
        this.finish_date = finish_date;
        this.adult_quantity = adult_quantity;
        this.children_quantity = children_quantity;
        this.fee_paid = fee_paid;
        this.transaction_id = transaction_id;
        this.status = status;
    }

    public Order(int traveler_id, int post_id, LocalDateTime begin_date, LocalDateTime finish_date, int adult_quantity, int children_quantity, double fee_paid, String transaction_id, String status) {
        this.traveler_id = traveler_id;
        this.post_id = post_id;
        this.begin_date = begin_date;
        this.finish_date = finish_date;
        this.adult_quantity = adult_quantity;
        this.children_quantity = children_quantity;
        this.fee_paid = fee_paid;
        this.transaction_id = transaction_id;
        this.status = status;
    }

    public Order(int trip_id, int traveler_id, int guider_id, float fee_paid, float rated) {
        this.trip_id = trip_id;
        this.traveler_id = traveler_id;
        this.guider_id = guider_id;
        this.fee_paid = fee_paid;
        this.rated = rated;
    }

    public Order(int trip_id, int traveler_id, int guider_id, int post_id, LocalDateTime begin_date, LocalDateTime finish_date, int adult_quantity, int children_quantity, double fee_paid, String transaction_id, String status, String postTitle, String object) {
        this.trip_id = trip_id;
        this.traveler_id = traveler_id;
        this.guider_id = guider_id;
        this.post_id = post_id;
        this.begin_date = begin_date;
        this.finish_date = finish_date;
        this.adult_quantity = adult_quantity;
        this.children_quantity = children_quantity;
        this.fee_paid = fee_paid;
        this.transaction_id = transaction_id;
        this.status = status;
        this.postTitle = postTitle;
        this.object = object;
    }

    public Order(int trip_id, int traveler_id, int guider_id, int post_id, LocalDateTime begin_date, LocalDateTime finish_date, int adult_quantity, int children_quantity, double fee_paid, String transaction_id, String status, String postTitle, String object, LocalDateTime book_time) {
        this.trip_id = trip_id;
        this.traveler_id = traveler_id;
        this.guider_id = guider_id;
        this.post_id = post_id;
        this.begin_date = begin_date;
        this.finish_date = finish_date;
        this.adult_quantity = adult_quantity;
        this.children_quantity = children_quantity;
        this.fee_paid = fee_paid;
        this.transaction_id = transaction_id;
        this.status = status;
        this.postTitle = postTitle;
        this.object = object;
        this.book_time = book_time;
    }


    public Order(int guider_id, int traveler_id, double fee_paid) {
        this.guider_id = guider_id;
        this.fee_paid = fee_paid;
        this.traveler_id = traveler_id;
    }

    public float getRated() {
        return rated;
    }

    public void setRated(float rated) {
        this.rated = rated;
    }

    public int gettrip_id() {
        return trip_id;
    }

    public void settrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public int getTraveler_id() {
        return traveler_id;
    }

    public void setTraveler_id(int traveler_id) {
        this.traveler_id = traveler_id;
    }

    public int getGuider_id() {
        return guider_id;
    }

    public void setGuider_id(int guider_id) {
        this.guider_id = guider_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public LocalDateTime getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(LocalDateTime begin_date) {
        this.begin_date = begin_date;
    }

    public LocalDateTime getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(LocalDateTime finish_date) {
        this.finish_date = finish_date;
    }

    public int getAdult_quantity() {
        return adult_quantity;
    }

    public void setAdult_quantity(int adult_quantity) {
        this.adult_quantity = adult_quantity;
    }

    public int getChildren_quantity() {
        return children_quantity;
    }

    public void setChildren_quantity(int children_quantity) {
        this.children_quantity = children_quantity;
    }

    public double getFee_paid() {
        return fee_paid;
    }

    public void setFee_paid(double fee_paid) {
        this.fee_paid = fee_paid;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String status) {
        this.postTitle = status;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String status) {
        this.object = status;
    }
}
