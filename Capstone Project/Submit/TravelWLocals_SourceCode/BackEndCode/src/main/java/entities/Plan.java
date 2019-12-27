package entities;

public class Plan {
    private int plan_id;
    private String meeting_point;
    private String detail;
    private int post_id;

    public Plan() {
    }

    public Plan(int plan_id, String meeting_point, String detail, int post_id) {
        this.plan_id = plan_id;
        this.meeting_point = meeting_point;
        this.detail = detail;
        this.post_id = post_id;
    }

    public Plan(String meeting_point, String detail, int post_id) {
        this.meeting_point = meeting_point;
        this.detail = detail;
        this.post_id = post_id;
    }

    public int getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(int plan_id) {
        this.plan_id = plan_id;
    }

    public String getMeeting_point() {
        return meeting_point;
    }

    public void setMeeting_point(String meeting_point) {
        this.meeting_point = meeting_point;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }
}
