/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package winter.findGuider.web.api;

import entities.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import security.AuthenProvider;
import security.UserService;
import services.Mail.MailService;
import services.account.AccountRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import security.TokenHelper;

/**
 * @author dgdbp
 */
@RestController
@RequestMapping(path = "/account")
@CrossOrigin(origins = {"http://localhost:3000"})
public class AccountController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private UserService userService;
    private AccountRepository repo;
    private AuthenProvider auth;
    private MailService mailService;
    private TokenHelper tokenService;
    @Value("${order.server.root.url}")
    private String URL_ROOT_SERVER;
    @Value("${order.client.root.url}")
    private String URL_ROOT_CLIENT;
    @Value("${order.client.root.url.domain}")
    private String URL_ROOT_CLIENT_DOMAIN;

    @Autowired
    public AccountController(AccountRepository repo, UserService userService, AuthenProvider auth, MailService ms, TokenHelper tokenService) {
        this.userService = userService;
        this.repo = repo;
        this.auth = auth;
        this.mailService = ms;
        this.tokenService = tokenService;
    }

    @PostMapping(path = "change", consumes = "application/json")
    public ResponseEntity<String> changePassword(@RequestBody Account acc) {
        Account model = null;
        try {

            model = repo.findAccountByName(
                    SecurityContextHolder.getContext().getAuthentication().getName());

            if (userService.getEncoder().matches(acc.getPassword(), model.getPassword())) {
                repo.changePassword(model.getUserName(), userService.getEncoder().encode(acc.getRePassword()));

            } else {
                throw new Exception("wrong password");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("true", HttpStatus.OK);
    }

    @PostMapping(path = "registrator", consumes = "application/json")
    public ResponseEntity registerUserAccount(@RequestBody Account acc, HttpServletResponse response
    ) {
        response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Account registered = null;
        acc.setToken("");
        acc.setExpired(new Date());
        try {
            //System.out.println(acc.getPassword() + "/" + acc.getUserName() + "/" + acc.getRole());
            registered = userService.registerNewUserAccount(acc);
            registered.setPassword("");
            registered.setToken("");
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // rest of the implementation
        //let log in after registration
        return new ResponseEntity<>(registered, HttpStatus.OK);

    }

    @RequestMapping("/logout")
    public ResponseEntity logout(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        Cookie sidCookie = new Cookie("token", "");
        sidCookie.setPath("/");
        sidCookie.setHttpOnly(true);
        sidCookie.setDomain(URL_ROOT_CLIENT_DOMAIN);
        sidCookie.setMaxAge(0);
        response.addCookie(sidCookie);
        Cookie refresh = new Cookie("refresh", "");
        refresh.setPath("/");
        //sidCookie.setSecure(true);
        refresh.setHttpOnly(true);
        refresh.setDomain(URL_ROOT_CLIENT_DOMAIN);
        refresh.setMaxAge(0);
        response.addCookie(refresh);
        return new ResponseEntity("bye", HttpStatus.OK);
    }

    @RequestMapping("/findAll")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Account>> findAll(HttpServletResponse response) {
        try {
            response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return new ResponseEntity(repo.findAllAccount(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/findAccountByNameAdmin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Account>> findAccountByNameAdmin(@RequestParam("name") String name, HttpServletResponse response) {
        try {
            response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return new ResponseEntity(repo.findAccountByNameAdmin(name), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/emailConfirm")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> confirmEmail(@RequestParam("token") String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            repo.confirmEmail(token);
            URI home = new URI(URL_ROOT_CLIENT);
            httpHeaders.setLocation(home);
            return new ResponseEntity(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/resendEmailConfirmation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> resendEmailConfirmation(@RequestParam("account_id") long account_id,
            HttpServletResponse response) {
        try {
            response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            String token = repo.insertEmailConfirmToken(account_id);
            String email = repo.getEmail((int) account_id);
            String content = "Hello " + repo.findAccountNameByAccountId((int) account_id) + "\n\n";
            content = content.concat("To verify your email, please click here : ");
            content = content.concat(URL_ROOT_SERVER + "/account/emailConfirm?token=" + token);
            mailService.sendMail(email, "TravelWLocal Email Confirmation", content);
            return new ResponseEntity(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/forgotPasswordConfirm")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> forgotPasswordConfirm(@RequestParam("username") String username,
            HttpServletResponse response) {
        try {
            response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            String message = repo.sendEmailForgotPassword(username);
            return new ResponseEntity(message, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/forgotPassword")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> forgotPassword(@RequestParam("token") String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            repo.resetForgotPassword(token);
            URI home = new URI(URL_ROOT_CLIENT);
            httpHeaders.setLocation(home);
            return new ResponseEntity(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/refresh")
    public ResponseEntity refresh(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String authToken = tokenService.resolveToken(request);

        //logger.warn("AuthToken: " +authToken);
        if (authToken != null) {
            // get username from token
            String username = tokenService.getUsername(authToken);
            String token = tokenService.createToken(username);
            //logger.warn("UserName: "+username);
            if (username != null) {
                Cookie sidCookie = new Cookie("token", token);
                sidCookie.setPath("/");
                sidCookie.setHttpOnly(true);
                sidCookie.setDomain(URL_ROOT_CLIENT_DOMAIN);    
                response.addCookie(sidCookie);
            } else {
                logger.error("Something is wrong with Token.");
            }
        }

        return new ResponseEntity("goon", HttpStatus.OK);
    }
}
