package entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TravelerReview {
    private long review_id;
    private long traveler_id;
    private long guider_id;
    private LocalDateTime post_date;
    private String review;
    private boolean visible;
    private String traveler_name;
    private String guider_name;
    private String guider_image;

    public TravelerReview() {
    }

    public TravelerReview(long review_id, LocalDateTime post_date, String review, boolean visible, String traveler_name, String guider_name) {
        this.review_id = review_id;
        this.post_date = post_date;
        this.review = review;
        this.visible = visible;
        this.traveler_name = traveler_name;
        this.guider_name = guider_name;
    }

    public TravelerReview(long traveler_id, long guider_id, String review) {
        this.traveler_id = traveler_id;
        this.guider_id = guider_id;
        this.review = review;
    }

    public TravelerReview(long review_id, LocalDateTime post_date, String review, String guider_name, String guider_image) {
        this.review_id = review_id;
        this.post_date = post_date;
        this.review = review;
        this.guider_name = guider_name;
        this.guider_image = guider_image;
    }

    public long getReview_id() {
        return review_id;
    }

    public void setReview_id(long review_id) {
        this.review_id = review_id;
    }

    public long getTraveler_id() {
        return traveler_id;
    }

    public void setTraveler_id(long traveler_id) {
        this.traveler_id = traveler_id;
    }

    public long getGuider_id() {
        return guider_id;
    }

    public void setGuider_id(long guider_id) {
        this.guider_id = guider_id;
    }

    public LocalDateTime getPost_date() {
        return post_date;
    }

    public void setPost_date(LocalDateTime post_date) {
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

    public String getTraveler_name() {
        return traveler_name;
    }

    public void setTraveler_name(String traveler_name) {
        this.traveler_name = traveler_name;
    }

    public String getGuider_name() {
        return guider_name;
    }

    public void setGuider_name(String guider_name) {
        this.guider_name = guider_name;
    }

    public String getGuider_image() {
        return guider_image;
    }

    public void setGuider_image(String guider_image) {
        this.guider_image = guider_image;
    }
}
