package com.example.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers(PathRequest.toH2Console()).permitAll()
				.requestMatchers("/user/signup").permitAll()
				.requestMatchers("/user/signup/rest").permitAll()
				.requestMatchers("/admin").hasAuthority("ROLE_ADMIN")
				.anyRequest().authenticated()
		);

		http.formLogin(login -> login
				.loginProcessingUrl("/login")
				.loginPage("/login")
				.failureUrl("/login?error")
				.usernameParameter("userId")
				.passwordParameter("password")
				.defaultSuccessUrl("/user/list", true)
				.permitAll()
		).logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.permitAll()
		);

		http.headers(headers -> headers
				.frameOptions(FrameOptionsConfig::disable)
		);

		// CSRF 対策を無効に設定 (一時的)
		http.csrf(csrf -> csrf
				.ignoringRequestMatchers(PathRequest.toH2Console())
//				.disable()
		);

		return http.build();
	}

//    @Bean
//    InMemoryUserDetailsManager userDetailsService() {
//    	PasswordEncoder encoder = passwordEncoder();
//    	
//    	UserDetails user = User.withUsername("user")
//    			.password(encoder.encode("password"))
//    			.roles("GENERAL")
//    			.build();
//    	UserDetails admin = User.withUsername("admin")
//    			.password(encoder.encode("password"))
//    			.roles("ADMIN")
//    			.build();
//    	return new InMemoryUserDetailsManager(user, admin);
//    }
}
