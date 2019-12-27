/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;


/**
 *
 * @author dgdbp
 */
public class InDayTrip {
    private int traveler_id;
    private int post_id;
    private String begin;
    private String finish;
    private String postTitle;
    private String object;

    public InDayTrip(int traveler_id, int post_id, String begin, String finish, String postTitle, String object) {
        this.traveler_id = traveler_id;
        this.post_id = post_id;
        this.begin = begin;
        this.finish = finish;
        this.postTitle = postTitle;
        this.object = object;
    }
    
    public InDayTrip() {
    }

    public int getTraveler_id() {
        return traveler_id;
    }

    public void setTraveler_id(int traveler_id) {
        this.traveler_id = traveler_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
    
    
}
