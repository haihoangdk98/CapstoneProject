/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.util.WebUtils;

/**
 *
 * @author dgdbp
 */
@Service
public class TokenHelper {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${jwt.secret}")
    private String secretKey;

//    @Value("${jwt.expire:3600000}")
    private long validityInMilliseconds = 1000*60*60*2; // 2h

    @Autowired
    private PrincipalService principalService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username) {

        //The JWT signature algorithm we will be using to sign the token    
        Claims claims = Jwts.claims().setSubject(username);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        System.out.println(username + "|" + now + "|" + validity);
        //Builds the JWT and serializes it to a compact, URL-safe string
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)//
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = principalService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            logger.error(e.toString());
    
        }
        return null;
    }

    public String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
        String bearerToken = null;
        try{
            bearerToken = WebUtils.getCookie(request, "token").getValue();
        } catch (Exception e) {
           logger.error(e.toString());
        }
        
        if (bearerToken != null) {
            return bearerToken;
        }
        return null;
    }

    public boolean validateToken(String token, UserDetails principal) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
//            System.out.println("ID: " + claims.getId());
//            System.out.println("Subject: " + claims.getSubject());
//            System.out.println("Issuer: " + claims.getIssuer());
//            System.out.println("Expiration: " + claims.getExpiration());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error(e.toString());
        }

        return false;
    }
}
