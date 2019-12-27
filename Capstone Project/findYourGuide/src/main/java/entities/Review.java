package entities;

import lombok.Data;

import java.util.Date;

@Data
public class Review {
    private long trip_id;
    private long rated;
    private Date post_date;
    private String review;
    private boolean visible;
    private String traveler_image;
    private String traveler_name;

    public Review() {
    }

    public Review(long trip_id, long rated, Date post_date, String review, boolean visible) {
        this.trip_id = trip_id;
        this.rated = rated;
        this.post_date = post_date;
        this.review = review;
        this.visible = visible;
    }

    public Review(long trip_id, long rated, String review) {
        this.trip_id = trip_id;
        this.rated = rated;
        this.review = review;
    }

    public Review(long trip_id, long rated, Date post_date, String review, boolean visible, String traveler_name, String traveler_image) {
        this.trip_id = trip_id;
        this.rated = rated;
        this.post_date = post_date;
        this.review = review;
        this.visible = visible;
        this.traveler_image = traveler_image;
        this.traveler_name = traveler_name;
    }

    public long getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(long trip_id) {
        this.trip_id = trip_id;
    }

    public long getRated() {
        return rated;
    }

    public void setRated(long rated) {
        this.rated = rated;
    }

    public Date getPost_date() {
        return post_date;
    }

    public void setPost_date(Date post_date) {
        this.post_date = post_date;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getTraveler_image() {
        return traveler_image;
    }

    public void setTraveler_image(String traveler_image) {
        this.traveler_image = traveler_image;
    }

    public String getTraveler_name() {
        return traveler_name;
    }

    public void setTraveler_name(String traveler_name) {
        this.traveler_name = traveler_name;
    }
}
