/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import entities.Account;
import entities.Guider;
import entities.Traveler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.Mail.MailService;
import services.account.AccountRepository;
import services.guider.GuiderService;
import services.traveler.TravelerService;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author dgdbp
 */
@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private AccountRepository repo;
    private PasswordEncoder passwordEncoder;
    private TokenHelper TokenHelper;
    private MailService mailService;
    @Autowired
    private GuiderService gs;
    @Autowired
    private TravelerService ts;

    @Value("${order.server.root.url}")
    private String URL_ROOT_SERVER;

    @Autowired
    public UserService(AccountRepository repo, PasswordEncoder passwordEncoder, TokenHelper tokenService, MailService ms) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.TokenHelper = tokenService;
        this.mailService = ms;
    }
    public PasswordEncoder getEncoder() {
        return passwordEncoder;
    }
    //add register user here
    @Transactional
    public Account registerNewUserAccount(Account acc)
            throws Exception {

        if (nameExisted(acc.getUserName())) {
            throw new Exception(
                    "There is an account with that user name: "
                            + acc.getUserName());
        }
        //acc.setToken(TokenHelper.createToken(acc.getUserName()));
        acc.setPassword(passwordEncoder.encode(acc.getPassword()));
        long id = repo.addAccount(acc);
        if (acc.getRole().equalsIgnoreCase("GUIDER")) {
            gs.createGuider(new Guider(id, acc.getUserName(), "", LocalDateTime.now(), "", "", 0,
                    "", new String[]{}, false, 0, "https://res.cloudinary.com/findguider/image/upload/account.jpg",
                    "", ""));
        } else if (acc.getRole().equalsIgnoreCase("TRAVELER")) {
            ts.createTraveler(new Traveler(id, acc.getUserName(), "", "", 0, new java.sql.Timestamp(
                    new Date().getTime()).toLocalDateTime(), "", "", "", "", "",
                    new String[]{}, "", "", "https://res.cloudinary.com/findguider/image/upload/account.jpg"));
        }
        // the rest of the registration operation
        String token = repo.insertEmailConfirmToken(id);
        String email = acc.getEmail();
        String content = "Hello " + acc.getUserName() + "\n\n";
        content = content.concat("To verify your email, please click here : ");
        content = content.concat(URL_ROOT_SERVER + "/account/emailConfirm?token=" + token);
        mailService.sendMail(email, "TravelWLocal Email Confirmation", content);
        return acc;
    }

    private boolean nameExisted(String name) throws Exception {
        Account user = null;
        try {
            user = repo.findAccountByName(name);
            if (user != null) {
                return true;
            }
        } catch (EmptyResultDataAccessException empty) {
            return false;
        }
        return false;
    }

    public boolean canGuiderLogin(long guider_id) {
        return repo.canGuiderLogin(guider_id);
    }
}
