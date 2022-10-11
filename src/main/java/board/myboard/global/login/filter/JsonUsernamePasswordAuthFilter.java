package board.myboard.global.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * Security 커스텀하여 JSON로그인 구현.
 * (성공시 JWT 발급 구현할 예정)
 */

public class JsonUsernamePasswordAuthFilter extends AbstractAuthenticationProcessingFilter {

    // /login/oauth2/ + ????? 로 오는 요청을 처리
    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json"; // json타입의 데이터로만 로그인 한다.

    private final ObjectMapper objectMapper;

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    // /login요청에 , POST로 온 요청 매칭.
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD);

    public JsonUsernamePasswordAuthFilter(ObjectMapper objectMapper) {
        // 위에 설정한 /login/oauth2/* 의 요청에 , GET으로 온 요청을 처리하기 위한 설정.
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)){
            throw new AuthenticationServiceException("Content-Type not supported" + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
        String username = usernamePasswordMap.get(USERNAME_KEY);
        String password = usernamePasswordMap.get(PASSWORD_KEY);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,password);
        // AnticationManager => SecurityConfig 파일에서 설정해줄것.
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
