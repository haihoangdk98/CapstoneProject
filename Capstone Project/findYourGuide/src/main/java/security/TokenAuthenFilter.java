/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.WebUtils;
import services.account.AccountRepository;

/**
 *
 * @author dgdbp
 */
@Component
public class TokenAuthenFilter extends OncePerRequestFilter {

    @Value("${order.client.root.url.domain}")
    private String URL_ROOT_CLIENT_DOMAIN;
    private TokenHelper tokenService;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private PrincipalService principalService;
    private AccountRepository repo;

    public TokenAuthenFilter(TokenHelper tokenService, PrincipalService principalService, AccountRepository repo) {
        this.tokenService = tokenService;
        this.repo = repo;
        this.principalService = principalService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws ServletException, IOException {

        String username = null;
        String authToken;
        authToken = tokenService.resolveToken(request);
        //System.out.println("is null ? "+authToken);
        if (authToken != null) {
                username = tokenService.getUsername(authToken);
            //logger.warn("UserName: "+username);
            if (username != null) {
                // get user
                UserDetails userDetails = principalService.loadUserByUsername(username);
                if (tokenService.validateToken(authToken, userDetails)) {
                    // create authentication
                    TokenBaseAuthentication authentication = new TokenBaseAuthentication(userDetails);
                    authentication.setToken(authToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication); // Adding Token in Security COntext
                }
            } else {
                //logger.error("Authen != null wrong with Token.");
                String refresh = WebUtils.getCookie(request, "refresh").getValue();
                username = repo.getUserByRefresh(refresh);
                //System.out.println(username);
                authToken = tokenService.createToken(username);
                Cookie sidCookie = WebUtils.getCookie(request, "token");
                sidCookie.setValue(authToken);
                response.addCookie(sidCookie);
            }
        }
        fc.doFilter(request, response);
    }

}
