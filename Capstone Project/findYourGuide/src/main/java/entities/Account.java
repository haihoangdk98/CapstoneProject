/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author dgdbp
 */
@Data
@Component
public class Account {

    private long id;
    //    @NotNull
    //    @NotEmpty
    private String userName;
    //    @NotNull
    //    @NotEmpty
    private String password;
    private String email;
    //    @NotNull
    //    @NotEmpty
    private String role;
    private String token;
    private Date expired;
    private String rePassword;
    private boolean status;
    private String email_token;
    private boolean email_verified;
    private boolean isGuiderActive;
    private boolean isContractExist;

    public Account(long id, String userName, String password, String role, String token, Date expired) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.token = token;
        this.expired = expired;
    }

    public Account(String password, String rePassword) {
        this.password = password;
        this.rePassword = rePassword;
    }

    public Account(long id, String userName, String password, String email, String role) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public Account(long id, String userName, String role) {
        this.id = id;
        this.userName = userName;
        this.role = role;
    }

    public Account(long id, String userName, String role, boolean isGuiderActive, boolean isContractExist) {
        this.id = id;
        this.userName = userName;
        this.role = role;
        this.isGuiderActive = isGuiderActive;
        this.isContractExist = isContractExist;
    }

    public Account() {
    }

    public Account(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public Account(String userName, String password, String email, String role) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public Account(long id, String userName, String email, String role) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.role = role;
    }

    public Account(long id, String userName, String email, String role, boolean status) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public Account(long id, String email, String email_token, boolean email_verified) {
        this.id = id;
        this.email = email;
        this.email_token = email_token;
        this.email_verified = email_verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String password) {
        this.rePassword = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getEmail_token() {
        return email_token;
    }

    public void setEmail_token(String email_token) {
        this.email_token = email_token;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    public boolean isGuiderActive() {
        return isGuiderActive;
    }

    public void setGuiderActive(boolean guiderActive) {
        isGuiderActive = guiderActive;
    }

    public boolean isContractExist() {
        return isContractExist;
    }

    public void setContractExist(boolean contractExist) {
        isContractExist = contractExist;
    }
}
