/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
/**
 *
 * @author dgdbp
 */
public class TokenBaseAuthentication extends AbstractAuthenticationToken{
    private final  UserDetails principal;
    
    private String token;   

    public TokenBaseAuthentication( UserDetails principal) {
        super(principal.getAuthorities());
        this.principal = principal;
    }
    
    public String getToken() {
        return token;
    }

    public void setToken( String token ) {
        this.token = token;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public UserDetails  getPrincipal() {
        return principal;
    }
    
}
