package board.myboard.global.jwt.service;

import board.myboard.domain.member.Member;
import board.myboard.domain.member.Role;
import board.myboard.domain.member.repository.MemberRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JwtServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtService jwtService = new JwtServicelmpl(memberRepository);

    @Autowired
    EntityManager em;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    private String username = "username";

    @BeforeEach
    public void init(){
        Member member = Member.builder().username(username).password("123456789").name("Member1")
                .nickName("Nickname1").role(Role.USER)
                .age(22).build();

        memberRepository.save(member);
        clear();
    }
    
    //초기화
    private void clear() {
        em.flush();
        em.clear();
    }

    private DecodedJWT getVerify(String token){
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
    }

    private HttpServletRequest setRequest(String accessToken, String refreshToken){

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

        mockHttpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);
        mockHttpServletRequest.addHeader(refreshHeader, BEARER+headerRefreshToken);

        return mockHttpServletRequest;
    }


    @Test
    public void createAccessToken_발급_성공() throws Exception {
        //given, when
        String accessToken = jwtService.createAccessToken(username);
        DecodedJWT verify = getVerify(accessToken);

        String subject = verify.getSubject();
        String findUsername = verify.getClaim(USERNAME_CLAIM).asString();
        //then
        Assertions.assertThat(findUsername).isEqualTo(username);
        Assertions.assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    public void createRefreshToken_발급_성공() throws Exception {
        // given, when
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);
        String subject = verify.getSubject();
        String username = verify.getClaim(USERNAME_CLAIM).asString();
        //then
        Assertions.assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
        Assertions.assertThat(username).isNull();
    }

    @Test
    public void updateRefreshToken_refreshToken_업데이트() throws Exception{
        //given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();
        Thread.sleep(3000);

        //when
        String reRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, reRefreshToken);
        clear();

        //then
        Assertions.assertThat(memberRepository.findByRefreshToken(refreshToken));
    }

    @Test
    public void destroyRefreshToken_제거_성공() throws Exception {
        //given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();
        //when
        jwtService.destroyRefreshToken(username);
        clear();
        //then
        Member member = memberRepository.findByUsername(username).get();
        Assertions.assertThat(member.getRefreshToken()).isNull();

    }
    @Test
    public void 토근_유효성_검사_성공() throws Exception{
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        //when , then
        Assertions.assertThat(jwtService.isTokenValid(accessToken)).isTrue();
        Assertions.assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
    }

    @Test
    public void setAccessTokenHeader_헤더_설정_성공() throws Exception {
        //given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);

        //when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);
        //then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        Assertions.assertThat(headerAccessToken).isEqualTo(accessToken);
    }

    @Test
    public void setRefreshTokenHeader_헤더_설정_성공() throws Exception {
        //given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);

        //when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);
        //then
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
        Assertions.assertThat(headerRefreshToken).isEqualTo(refreshToken);

    }

    @Test
    public void  sendAccessAndRefreshToken_토큰_전송_성공() throws Exception {
        //given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        //when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        //then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        Assertions.assertThat(headerAccessToken).isEqualTo(accessToken);
        Assertions.assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    public void extractAccessToken_AcessToken_추출_성공() throws Exception{
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        //when
        String extractAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(() -> new Exception("토큰이 없습니다."));

        //then
        Assertions.assertThat(extractAccessToken).isEqualTo(accessToken);
        Assertions.assertThat(getVerify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(username);

    }
}