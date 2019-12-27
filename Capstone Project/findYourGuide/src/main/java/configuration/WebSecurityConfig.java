/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

import entities.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import security.*;

import java.util.Arrays;
import services.account.AccountRepository;

/**
 * @author dgdbp
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AccountRepository repo;
    @Autowired
    TokenHelper tokenHelper;
    private PrincipalService userDetail;
    @Autowired
    private AuthenProvider authProvide;
    @Autowired
    private PrincipalService principalService;
    @Autowired
    private Principal principal;
    @Autowired
    private UserService userService;

    @Value("${order.client.root.url}")
    private String URL_ROOT_CLIENT;

    @Value("${order.client.root.url2}")
    private String URL_ROOT_CLIENT2;

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("to configureGlobal");
        auth.userDetailsService(principalService);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("to Simpson");
        auth
                .inMemoryAuthentication()
                .withUser("Simpson").password("0123456789").roles("GUIDER");
        auth.authenticationProvider(authProvide);
        auth.userDetailsService(principalService);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedOrigins(Arrays.asList(URL_ROOT_CLIENT));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTION"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        System.out.println(source.toString());
        return source;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("to http configure");

        http
//                .cors().and()
                .cors().configurationSource(corsConfigurationSource()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
//                .antMatchers("/location/**").hasAuthority("TRAVELER")
                .antMatchers("/location/**").permitAll()
                .antMatchers("/**/*.jpg").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/account/**").permitAll()
                .antMatchers("/category/**").permitAll()
                .antMatchers("/guiderpost/**").permitAll()
                .antMatchers("/Feedback/**").permitAll()
                .antMatchers("/Guider/**").permitAll()
                .antMatchers("/guider/**").permitAll()
                .antMatchers("/Order/**").permitAll()
                .antMatchers("/Payment/**").permitAll()
                .antMatchers("/plan/**").permitAll()
                .antMatchers("/review/**").permitAll()
                .antMatchers("/Traveler/**").permitAll()
                .antMatchers("/statistic/**").permitAll()
                .anyRequest().authenticated().and()
                .addFilter(getAuthenticationFilter())
                .addFilterBefore(new TokenAuthenFilter(tokenHelper, principalService,repo), BasicAuthenticationFilter.class)
        ;

        http.csrf().disable();


    }

    //  Patterns to ignore from JWT security check
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                HttpMethod.POST,
                "/account/registrator"
        );
        web.ignoring().antMatchers(
                HttpMethod.GET,
                "/",
                "/images/**",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js",
                "/account/**"
        );
    }

    @Bean
    public AuthenticationFilter getAuthenticationFilter() {

        final AuthenticationFilter filter = new AuthenticationFilter(authProvide, tokenHelper,repo, userService);
        filter.setFilterProcessesUrl("/account/login");
        return filter;
    }

}
