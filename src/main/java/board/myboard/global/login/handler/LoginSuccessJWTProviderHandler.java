package board.myboard.global.login.handler;

import board.myboard.domain.member.repository.MemberRepository;
import board.myboard.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProviderHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String username = extractUsername(authentication);

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        // isPresent() 메소드
        //- Boolean 타입
        //- Optional 객체가 값을 가지고 있다면 true, 값이 없다면 false 리턴

        // 로그인 성공 시 JWT를 제공하는 코드 작성
        memberRepository.findByUsername(username).ifPresent(
                member -> member.updateRefreshToken(refreshToken));

        log.info("로그인에 성공합니다. username: {}", username);
        log.info("AccessToken을 발급합니다.  AccessToken : {}", accessToken);
        log.info("RefreshToken을 발급합니다. RefreshToken: {}", refreshToken);
    }

    private String extractUsername(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
