package com.appdeveloperblog.ws.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.appdeveloperblog.ws.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurity{

	private final UserService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

//	@SuppressWarnings("deprecation")
//	@Bean
//	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//		http.cors().and().csrf().disable()
//			.authorizeHttpRequests().requestMatchers(HttpMethod.POST, "/users")
//			.permitAll().anyRequest().authenticated();
//		
//		http.authenticationProvider(authenticationProvider());
//		return http.build();
//	}
	
	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		// configure authentication manager builder
		AuthenticationManagerBuilder authenticationManagerBuilder = 
				http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder
		.userDetailsService(userDetailsService)
		.passwordEncoder(bCryptPasswordEncoder);
		
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		
		// customize login url path
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
		authenticationFilter.setFilterProcessesUrl("/users/login");
		
		http
		.cors().and()
		.csrf().disable()
		.authorizeHttpRequests().requestMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
		.permitAll()
		.anyRequest().authenticated()
		.and()
		.authenticationManager(authenticationManager)
		.addFilter(authenticationFilter)
		.addFilter(new AuthorizationFilter(authenticationManager))
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		return http.build();
	}

}
