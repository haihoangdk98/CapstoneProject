/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.account;

import entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import services.Mail.MailService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author dgdbp
 */
@Repository
public class AccountRepository {
    private JdbcTemplate jdbc;
    private MailService mailService;
    private PasswordEncoder passwordEncoder;

    @Value("${order.server.root.url}")
    private String URL_ROOT_SERVER;

    @Autowired
    public AccountRepository(JdbcTemplate jdbc, MailService ms, PasswordEncoder pe) {
        this.jdbc = jdbc;
        this.mailService = ms;
        this.passwordEncoder = pe;
    }

    public Account findAccountByName(String name) throws Exception {
        return jdbc.queryForObject("select * from account where user_name=?", new RowMapper<Account>() {
            @Override
            public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Account(
                        rs.getLong("account_id"),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role"));
            }
        }, name);
    }

    public List<Account> findAccountByNameAdmin(String name) throws Exception {
        name = name.toUpperCase();
        return jdbc.query("SELECT account_id, user_name, email, role, active FROM account \n" +
                "left join guider on account_id = guider_id \n" +
                "where upper(user_name) like '%" + name + "%' and role = 'GUIDER'", new RowMapper<Account>() {
            @Override
            public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Account(rs.getInt("account_id"), rs.getString("user_name"),
                        rs.getString("email"), rs.getString("role"), rs.getBoolean("active"));
            }
        });
    }

    public String findAccountNameByAccountId(int account_id) throws Exception {
        Account account = jdbc.queryForObject("select * from account where account_id=?", new RowMapper<Account>() {
            @Override
            public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Account(
                        rs.getLong("account_id"),
                        rs.getString("user_name"),
                        rs.getString("email"),
                        rs.getString("role"));
            }
        }, account_id);
        return account.getUserName();
    }

    public int addAccount(Account acc) throws Exception {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = " INSERT INTO public.account( "
                + " user_name, password, email ,role ) "
                + " VALUES ( ?, ?, ?, ? ) ";
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, new String[]{"account_id"});
            ps.setString(1, acc.getUserName());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getEmail());
            ps.setString(4, acc.getRole());
            return ps;
        }, keyHolder);
        return (int) keyHolder.getKey();
    }

    public boolean canGuiderLogin(long guider_id) {
        String query = "select active from guider where guider_id = ?";
        return jdbc.queryForObject(query, new Object[]{guider_id}, boolean.class);
    }

    public boolean isContractExist(long guider_id) {
        String query = "select count(contract_id) from contract where guider_id = ?";
        int count = jdbc.queryForObject(query, new Object[]{guider_id}, int.class);
        if (count == 0) {
            return false;
        } else {
            return true;
        }
    }

    public int changePassword(String name, String pass) throws Exception {
        String query = "update account set password = ? where user_name = ? ; ";
        return jdbc.update(query, pass, name);
    }

    public String getEmail(int account_id) throws Exception {
        String query = "select email from account where account_id = ?";
        return jdbc.queryForObject(query, new Object[]{account_id}, String.class);
    }

    public boolean isMailVerified(long account_id) throws Exception {
        String query = "select email_verified from account where account_id = ?";
        return jdbc.queryForObject(query, new Object[]{account_id}, boolean.class);
    }

    public List<Account> findAllAccount() throws Exception {
        List<Account> result = new ArrayList<>();
        String query = "SELECT account_id, user_name, email, role, active FROM account left join guider on account_id = guider_id";
        result = jdbc.query(query, new RowMapper<Account>() {
            @Override
            public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Account(rs.getInt("account_id"), rs.getString("user_name"),
                        rs.getString("email"), rs.getString("role"), rs.getBoolean("active"));
            }
        });
        return result;
    }

    public String insertEmailConfirmToken(long account_id) throws Exception {
        String token = this.generateRandomSalt(50);
        jdbc.update("update account set email_token = ? where account_id = ?", token, account_id);
        return account_id + "E" + token;
    }

    public void confirmEmail(String token) throws Exception {
        String[] data = token.split("E", 2);
        int account_id = Integer.parseInt(data[0]);
        String checkToken = data[1];
        String query = "select email_token from account where account_id = ?";
        String email_token = jdbc.queryForObject(query, new Object[]{account_id}, String.class);
        String content = "";
        if (email_token.equals(checkToken)) {
            // Change verified status
            jdbc.update("update account set email_verified = true where account_id = ?", account_id);
            // Mail content
            content = "Your email has been verified !\n" +
                    "Have a nice day\n\n";
            content = content.concat("Sincerely,\nTravelWLocal");
        } else {
            content = "Your email has not been verified !\n" + "Please try again later !" +
                    "Have a nice day\n\n";
            content = content.concat("Sincerely,\nTravelWLocal");
        }
        // Send result email
        String email = this.getEmail(account_id);
        mailService.sendMail(email, "TravelWLocal Email Confirmation", content);
    }

    public String sendEmailForgotPassword(String username) throws Exception {
        Account acc = this.findAccountByName(username);
        if (acc == null) {
            return "Account does not exist";
        }

        if (!this.isMailVerified(acc.getId())) {
            return "Your email was not verified";
        }

        // Send confirmation email
        String token = acc.getId() + "E" + this.generateRandomSalt(20);
        String content = "Hello " + acc.getUserName() + "\n\n";
        content = content.concat("It seem like you have sent us a request to reset forgotten password.\n");
        content = content.concat("If you want to reset your password, please click the link:\n");
        content = content.concat(URL_ROOT_SERVER + "/account/forgotPassword?token=" + token + "\n\n");
        content = content.concat("We will send your new password shortly after you click the link.\n\n");
        content = content.concat("Sincerely,\nTravelWLocal");
        mailService.sendMail(acc.getEmail(), "TravelWLocal Forgot Password", content);
        return "An email has been sent to you";
    }

    public void resetForgotPassword(String token) throws Exception {
        String[] data = token.split("E", 2);
        int account_id = Integer.parseInt(data[0]);
        String checkToken = data[1];
        String username = this.findAccountNameByAccountId(account_id);
        String email = this.getEmail(account_id);
        if (checkToken.length() == 20) {
            // Set new password
            String newPassword = this.generateRandomSalt(10);
            String query = "update account set password = ? where account_id = ?";
            jdbc.update(query, passwordEncoder.encode(newPassword), account_id);

            // Sent new password to user
            String content = "Hello " + username + "\n\n";
            content = content.concat("Here is your new password: " + newPassword + "\n");
            content = content.concat("We highly recommend you to change it to your own.\n\n");
            content = content.concat("Sincerely,\nTravelWLocal");
            mailService.sendMail(email, "TravelWLocal Forgot Password", content);
        }
    }

    private String generateRandomSalt(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }
    
    
    public String getEmailToken(String userName) {
        return jdbc.queryForObject("select email_token from account where user_name = ? ; ", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("email_token");}
        },userName);
    }
    
    public String getUserByRefresh(String refresh) {
        return jdbc.queryForObject("select user_name from account where email_token = ? ; ", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("user_name");}
        },refresh);
    }
}
