/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import entities.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import services.account.AccountRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dgdbp
 */
@Component
public class AuthenProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(AuthenProvider.class);
    private Logger logger = LoggerFactory.getLogger(getClass());
    private PasswordEncoder encoder;
    private AccountRepository userService;

    @Autowired
    public AuthenProvider(AccountRepository userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    public PasswordEncoder getEncoder() {
        return encoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("to AuthProvider");
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        System.out.println("role " + authentication.getAuthorities().toString());
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        Account acc = null;
        try {
            acc = userService.findAccountByName(username);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BadCredentialsException("Authentication failed");
        }
        grantedAuths.add(new SimpleGrantedAuthority(acc.getRole()));
        if (acc != null && acc.getUserName().equals(username) && encoder.matches(password, acc.getPassword())) {
            if (!authentication.getAuthorities().toString().contains(acc.getRole())) {
                throw new BadCredentialsException("Authentication failed");
            }
            return new UsernamePasswordAuthenticationToken(username, acc.getId(), grantedAuths);
        } else {
            throw new BadCredentialsException("Authentication failed");
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }

}
