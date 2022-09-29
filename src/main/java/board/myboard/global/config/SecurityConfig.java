package board.myboard.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

import javax.servlet.Filter;

/**
 * 시큐리티를 이용한 JSON 데이터로 로그인
 *
 *
 * 스프링 버전이 업데이트 됨에 따라 WebSecurityConfigurerAdapter와 그 외 몇 가지들이 Deprecated.
 *
 * 기존에는 WebSecurityConfigurerAdapter를 상속받아 설정을 오버라이딩 하는 방식이었는데
 * 바뀐 방식에서는 상속받아 오버라이딩하지 않고 모두 Bean으로 등록을 합니다.
 * (바뀐 방식 공부 후 적용)
 */


@Configuration
public class SecurityConfig extends WebSecurityConfiguration {

}
