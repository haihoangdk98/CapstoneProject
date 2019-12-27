package entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Guider {
    private long guider_id;
    //private long contract_id;
    private String first_name;
    private String last_name;

    @JsonFormat(pattern = "MM/dd/yyyy HH:mm")
    private LocalDateTime date_of_birth;
    private String phone;
    private String profile_video;
    private String about_me;
    private long contribution;
    private String city;
    private String[] languages;
    private boolean active;
    private long rated;
    private String avatar;
    private String passion;
    private String name;

    public Guider() {
    }

    public String getProfile_video() {
        return profile_video;
    }

    public void setProfile_video(String profile_video) {
        this.profile_video = profile_video;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Guider(long guider_id, String first_name, String last_name, LocalDateTime date_of_birth, String phone, String about_me, long contribution, String city, String[] languages, boolean active, long rated, String avatar, String passion, String profile_video) {
        this.guider_id = guider_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_of_birth = date_of_birth;
        this.phone = phone;
        this.about_me = about_me;
        this.contribution = contribution;
        this.city = city;
        this.languages = languages;
        this.active = active;
        this.rated = rated;
        this.avatar = avatar;
        this.passion = passion;
        this.profile_video = profile_video;
    }
    public Guider(long guider_id, String first_name, String last_name, LocalDateTime date_of_birth, String phone, String about_me, long contribution, String city, String[] languages, boolean active, long rated, String avatar, String passion, String name,String profile_video) {
        this.guider_id = guider_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_of_birth = date_of_birth;
        this.phone = phone;
        this.about_me = about_me;
        this.contribution = contribution;
        this.city = city;
        this.languages = languages;
        this.active = active;
        this.rated = rated;
        this.avatar = avatar;
        this.passion = passion;
        this.name = name;
        this.profile_video = profile_video;
    }
    
    

    public long getGuider_id() {
        return guider_id;
    }

    public void setGuider_id(long guider_id) {
        this.guider_id = guider_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public LocalDateTime getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDateTime date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public long getContribution() {
        return contribution;
    }

    public void setContribution(long contribution) {
        this.contribution = contribution;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getRated() {
        return rated;
    }

    public void setRated(long rated) {
        this.rated = rated;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassion() {
        return passion;
    }

    public void setPassion(String passion) {
        this.passion = passion;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
