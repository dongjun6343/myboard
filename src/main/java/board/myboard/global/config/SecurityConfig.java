package board.myboard.global.config;

import board.myboard.domain.member.service.LoginService;
import board.myboard.global.login.filter.JsonUsernamePasswordAuthFilter;
import board.myboard.global.login.handler.LoginFailHandler;
import board.myboard.global.login.handler.LoginSuccessJWTProviderHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;


/**
 * 시큐리티를 이용한 JSON 데이터로 로그인
 * ==>  스프링 시큐리티를 설정을 통해, 기본적인 접근 제어와, 스프링 시큐리티에서 제공하는 PasswordEncoder를 등록
 *
 * 스프링 버전이 업데이트 됨에 따라 WebSecurityConfigurerAdapter와 그 외 몇 가지들이 Deprecated됨.
 *
 * 기존에는 WebSecurityConfigurerAdapter를 상속받아 설정을 오버라이딩 하는 방식이었는데
 * 바뀐 방식에서는 상속받아 오버라이딩하지 않고 모두 Bean으로 등록을 합니다.
 * (바뀐 방식 공부 후 적용)
 *
 * => SecurityFilterChain, WebSecurityCustomizer Bean으로 등록하여 Security 설정하도록 함.
 */

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    private final ObjectMapper objectMapper;
    /**
     * JSON을 통해 로그인을 진행
     * refreshToken이 만료되기전까지는 토큰을 인증을 진행할거라서
     * => FormLogin, httpBasic => disable()처리
     *
     * csrf : disable()처리 (why? --> 깃블로그에 정리함.)
     *
     * 세선은 상태를 유지하지 않음으로 설정.
     * => SessionCreationPolicy.STATELESS
     *
     * antMatchers("/login","/singUp","/").permitAll()
     * => 로그인, 회원가입, 메인페이지에 대해서는 인증없이도 접근가능하도록 설정.
     *
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/signUp","/").permitAll()
                .anyRequest().authenticated();

                http.addFilterAfter(jsonUsernamePasswordAuthFilter(), LogoutFilter.class);

                return http.build();
    }

    // 1 - PasswordEncoder 등록
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // 2 - AuthenticationManager 등록
    @Bean
    public AuthenticationManager authenticationManager(){
        // DaoAuthenticationProvider 사용
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // PasswordEncoder로는 PasswordEncoderFactories.createDelegatingPasswordEncoder() 사용
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(loginService);

        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessJWTProviderHandler loginSuccessJWTProviderHandler(){
        return new LoginSuccessJWTProviderHandler();
    }

    @Bean
    public LoginFailHandler loginFailHandler(){
        return new LoginFailHandler();
    }

    @Bean
    public JsonUsernamePasswordAuthFilter jsonUsernamePasswordAuthFilter(){
        JsonUsernamePasswordAuthFilter jsonUsernamePasswordLoginFilter =
                new JsonUsernamePasswordAuthFilter(objectMapper);

        //추가
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProviderHandler());
        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailHandler());

        return jsonUsernamePasswordLoginFilter;
    }
}
