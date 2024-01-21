package org.farmfresh.online.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .regexMatchers("/farmfoods/inventory","/farmfoods/landing","/farmfoods/category","/farmfoods/products","/farmfoods/items")
                .authenticated().and().formLogin().loginPage("/admin/login").loginProcessingUrl("/login").and().httpBasic()
                .and()
                .logout()
                ;
        http.csrf().disable();
        /*
        To prevent the below error:
        No 'Access-Control-Allow-Origin' header is present on the requested resource.
        If an opaque response serves your needs, set the request's mode to 'no-cors' to fetch the resource with CORS disabled.
         */
        http.headers().frameOptions().disable();
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsManager(){
        /*
        Commenting out the approach 1 to try the new approach

        UserDetails shopper = User.withDefaultPasswordEncoder()
                .username("shopper")
                .password("farm")
                .authorities("read")
                .build();
        UserDetails manager = User.withDefaultPasswordEncoder()
                .username("manager")
                .password("farm")
                .authorities("admin")
                .build();
        return new InMemoryUserDetailsManager(shopper, manager);
         */
        /*
        Approach 2: Instead of using withDefaultPasswordEncoder, we are using NoOpPasswordEncoder
         */
        UserDetails shopper1 = User.withUsername("Nithya")
                .password("farm")
                .authorities("read")
                .build();
        UserDetails shopper2 = User.withUsername("Guest")
                .password("farm")
                .authorities("read")
                .build();
        UserDetails shopper3 = User.withUsername("Sathya")
                .password("farm")
                .authorities("read")
                .build();
        UserDetails shopper4 = User.withUsername("Lavanya")
                .password("farm")
                .authorities("read")
                .build();
        UserDetails shopper5 = User.withUsername("Swetha")
                .password("farm")
                .authorities("read")
                .build();
        UserDetails shopper6 = User.withUsername("Dharshana")
                .password("farm")
                .authorities("read")
                .build();
        UserDetails shopper7 = User.withUsername("Naga")
                .password("farm")
                .authorities("read")
                .build();
        UserDetails manager = User.withUsername("Priya")
                .password("farm")
                .authorities("admin")
                .build();
        return new InMemoryUserDetailsManager(shopper1, shopper2, shopper3, shopper4, shopper5, shopper6, shopper7, manager);

    }

    /*
    The password Encoder is needed only for Approach 2.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

}