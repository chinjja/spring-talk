package com.chinjja.talk.domain.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.chinjja.talk.domain.auth.common.JwtAuthenticationEntryPoint;
import com.chinjja.talk.domain.auth.common.JwtTokenProvider;
import com.chinjja.talk.domain.auth.filter.JwtRequestFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final UserDetailsService userDetailsService;
	private final JwtTokenProvider jwtTokenProvider;
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.httpBasic().disable()
		.formLogin().disable()
		.csrf().disable()
		.authorizeHttpRequests()
		.antMatchers("/auth/**").permitAll()
		.antMatchers("/websocket/**").permitAll()
		.antMatchers(HttpMethod.GET, "/storage/**").permitAll()
		.antMatchers("/verification/**").authenticated()
		.anyRequest().hasRole("USER")
		.and()
		.cors()
		.and()
		.exceptionHandling()
		.authenticationEntryPoint(jwtAuthenticationEntryPoint)
		.and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	JwtRequestFilter jwtRequestFilter() {
		return new JwtRequestFilter(userDetailsService, jwtTokenProvider);
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOriginPattern("*");
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}