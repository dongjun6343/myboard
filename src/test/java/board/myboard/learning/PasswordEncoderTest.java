package board.myboard.learning;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 작성하고 있는 애플리케이션 로직과 관계 없이,
 * 어떠한 라이브러리나 외부 기능을 검증해보기 위해 사용되는 테스트를 학습 테스트라고 한다.
 */
@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void 패스워드_암호화_성공() throws Exception {
        //given
        String password = "1234567890";
        //when
        String encodePassword = passwordEncoder.encode(password);
        //then
        Assertions.assertThat(encodePassword.startsWith("{"));
        Assertions.assertThat(encodePassword.contains("{bcrypt}"));
        Assertions.assertThat(encodePassword).isNotEqualTo(password);
    }
    @Test
    public void 암호화된_비밀번호_매치_성공() throws Exception {
        //given
        String password = "1234567890";

        //when
        String encodePassword = passwordEncoder.encode(password);

        //then
        Assertions.assertThat(passwordEncoder.matches(password, encodePassword)).isTrue();

    }
}
