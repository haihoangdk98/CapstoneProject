package entities;

import java.time.LocalDateTime;

public class Feedback {
    private int feedback_id;
    private int account_id;
    private String message;
    private LocalDateTime time_sent;
    private String sender_name;

    public Feedback() {
    }

    public Feedback(int feedback_id, int account_id, String message, LocalDateTime time_sent) {
        this.feedback_id = feedback_id;
        this.account_id = account_id;
        this.message = message;
        this.time_sent = time_sent;
    }

    public Feedback(int feedback_id, String message, LocalDateTime time_sent, String sender_name) {
        this.feedback_id = feedback_id;
        this.message = message;
        this.time_sent = time_sent;
        this.sender_name = sender_name;
    }

    public int getFeedback_id() {
        return feedback_id;
    }

    public void setFeedback_id(int feedback_id) {
        this.feedback_id = feedback_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(LocalDateTime time_sent) {
        this.time_sent = time_sent;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }
}
