/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import entities.Account;
import entities.AuthenticationImp;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dgdbp
 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    private AuthenProvider authenticationManager;
    private TokenHelper TokenHelper;

    @Value("${order.client.root.url}")
    private String URL_ROOT_CLIENT;

    @Value("${order.client.root.url.domain}")
    private String URL_ROOT_CLIENT_DOMAIN;

    public AuthenticationFilter(AuthenProvider authenticationManager, TokenHelper tokenService) {
        this.authenticationManager = authenticationManager;
        this.TokenHelper = tokenService;

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            Account acc = new ObjectMapper().readValue(request.getInputStream(), Account.class);
            return authenticationManager.authenticate(new AuthenticationImp(acc));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String token = TokenHelper.createToken(authResult.getPrincipal().toString());
        response.setHeader("Access-Control-Allow-Origin", URL_ROOT_CLIENT);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
        Cookie sidCookie = new Cookie("token", token);
        sidCookie.setPath("/");
//            sidCookie.setSecure(true);
        sidCookie.setHttpOnly(true);
        sidCookie.setDomain(URL_ROOT_CLIENT_DOMAIN);
        response.addCookie(sidCookie);
        //System.out.println(authResult.getCredentials().toString());
        Account res = new Account(Integer.parseInt(authResult.getCredentials().toString())
                , authResult.getPrincipal().toString(), authResult.getAuthorities().toArray()[0].toString());
        response.getWriter().write(new Gson().toJson(res));
        //response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}

// String body = null;
//            StringBuilder stringBuilder = new StringBuilder();
//            BufferedReader bufferedReader = null;
//
//            try {
//                InputStream inputStream = request.getInputStream();
//                if (inputStream != null) {
//                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    char[] charBuffer = new char[128];
//                    int bytesRead = -1;
//                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
//                         System.out.println(stringBuilder.toString());
//                        stringBuilder.append(charBuffer, 0, bytesRead);
//                    }
//                } else {
//                    stringBuilder.append("how?");
//                }
//            } catch (IOException ex) {
//                throw ex;
//            } finally {
//                if (bufferedReader != null) {
//                    try {
//                        bufferedReader.close();
//                    } catch (IOException ex) {
//                        throw ex;
//                    }
//                }
//            }
//
//            body = stringBuilder.toString();
