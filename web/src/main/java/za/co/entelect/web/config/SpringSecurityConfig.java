package za.co.entelect.web.config;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import za.co.entelect.config.ConfigProperties;
import za.co.entelect.web.security.CustomAccessDeniedHandler;
import za.co.entelect.web.security.LoginFailureHandler;
import za.co.entelect.web.security.LoginSuccessHandler;

/**
 * Java Configuration Class containing settings for the Spring Security setup.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Setter
    private UserDetailsService userDetailsService;

    @Autowired
    private ConfigProperties config;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * The method defines which URL paths should be secured and which should not.
     * Specifically, the "/" path are configured to not require any authentication.
     * All other paths must be authenticated.
     *
     * @param http The {@code HttpSecurity} object relevant to the current request.
     * @throws Exception if authorizeRequests, formLogin and logout throws any exceptions.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(switchUserFilter(userDetailsService));

        http
            .authorizeRequests()
            .antMatchers(
                "/",
                "/register",
                "/login",
                "/loginFailed",
                "/loginPassed",
                "/logoutSuccess",
                "/verification",
                "/verification/resend",
                "/verification/verify",
                "/forgotPassword",
                "/forgotPassword/reset",
                "/accessDenied",
                "/admin/dev/email",
                "/test/**",
                "/view/**",
                "/users/**",
                "/teams/**",
                "/clans/**",
                "/organizations/**",
                "/games/**",
                "/cups/**",
                "/news/**",
                "/matches/**",
                "/404",
                "/error",
                "/viewError",
                "/defaultResources",
                "/leagues/**",
                "/divisions/**",
                "/legs/**"
            ).permitAll()
            .antMatchers(HttpMethod.GET,
                "/api/public/**"
            ).permitAll()
            .antMatchers(
                "/monitoring",
                "/login/impersonate"
            ).hasAnyRole("ADMIN")
            .antMatchers(
                "/logout/impersonate"
            ).hasAnyRole("PREVIOUS_ADMINISTRATOR")
            .antMatchers(
                "/profile",
                "/profile/**",
                "/account",
                "/account/**"
            ).fullyAuthenticated()
            .anyRequest()
                .authenticated() // All Requests to any URL not specified above should be authenticated.
                .and().formLogin()
                    .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                    .successHandler(loginSuccessHandler())
                    .failureHandler(loginFailureHandler())
                .and().logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/logoutSuccess")
                    .invalidateHttpSession(true)
                .and().csrf()
                .and().headers()
                    .xssProtection().disable()
                .and().headers()
                    .frameOptions().disable()
                .and().exceptionHandling()
                    .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and().rememberMe()
                        .rememberMeParameter("remember")
                        .tokenValiditySeconds(config.getSecuritySessionRememberMeSeconds())
                        .key(config.getSecuritySessionRememberMeKey());
    }

    /**
     * Configures the web security(Adds to the Spring Security Filter Chain) to ignore the matched URLS.
     * Used to configure global exclusions. Role or user based security should be handled by the {@code configure(HttpSecurity)}
     * This is for all the static resources. Css, Js, Images etc.
     *
     * @param web The {@code WebSecurity} object that will be used to build up the Security Filter.
     * @throws Exception If any of the builder methods throws an {@code Exception}
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**", "/uploaded-resources/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
         return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler("/loginPassed");
    }

    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public SwitchUserFilter switchUserFilter(UserDetailsService userDetailsService) {
        SwitchUserFilter suFilter = new SwitchUserFilter();
        suFilter.setUserDetailsService(userDetailsService);
        suFilter.setSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
            String url = httpServletRequest.getHeader("referer");
            if (url == null) {
                httpServletResponse.sendRedirect("/");
            } else {
                httpServletResponse.sendRedirect(url);
            }
        });
        return suFilter;
    }
}
