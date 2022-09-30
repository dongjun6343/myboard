package board.myboard.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.Filter;

/**
 * 시큐리티를 이용한 JSON 데이터로 로그인
 *
 *
 * 스프링 버전이 업데이트 됨에 따라 WebSecurityConfigurerAdapter와 그 외 몇 가지들이 Deprecated됨.
 *
 * 기존에는 WebSecurityConfigurerAdapter를 상속받아 설정을 오버라이딩 하는 방식이었는데
 * 바뀐 방식에서는 상속받아 오버라이딩하지 않고 모두 Bean으로 등록을 합니다.
 * (바뀐 방식 공부 후 적용)
 * => SecurityFilterChain, WebSecurityCustomizer Bean으로 등록하여 Security 설정하도록 함.
 */


@Configuration
public class SecurityConfig extends WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        /**
         * JSON을 통해 로그인을 진행
         * refreshToken이 만료되기전까지는 토큰을 인증을 진행할거라서
         * => FormLogin, httpBasic => disable()처리
         *
         * csrf : disable()처리
         *
         * 세선은 상태를 유지하지 않음으로 설정.
         * => SessionCreationPolicy.STATELESS
         *
         * antMatchers("/login","/singUp","/").permitAll()
         * => 로그인, 회원가입, 메인페이지에 대해서는 인증없이도 접근가능하도록 설정.
         *
         */

        http.formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/login","/singUp","/").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
