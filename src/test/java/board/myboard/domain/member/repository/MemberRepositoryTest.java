package board.myboard.domain.member.repository;

import board.myboard.domain.member.Member;
import board.myboard.domain.member.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    public void 회원저장_성공() throws Exception{
        //given
        // 변수값
        Member member = Member.builder()
                .username("TEST00")
                .name("박동준")
                .password("1234567")
                .nickName("dongurijun")
                .role(Role.USER)
                .age(22)
                .build();

        //when
        // 실행
        Member saveMember = memberRepository.save(member);

        //then
        // 검증값
        Member findMember = memberRepository.findById(saveMember.getId()).orElseThrow(
                () -> new RuntimeException("저장된 회원이 없습니다.") //예외클래스 만들기.
        );

        Assertions.assertThat(findMember.equals(saveMember.getId()));
    }

    @Test
    public void 성공_회원수정() throws Exception{
        //given
        String updatePassword = "1234567890";

        Member member = Member.builder()
                .username("TEST00")
                .name("박동준")
                .password("1234567")
                .nickName("dongurijun")
                .role(Role.USER)
                .age(22)
                .build();
        memberRepository.save(member);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //when
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(() -> new Exception());
        findMember.updateAge(29);
        findMember.updateName("박동준UPDATE");
        findMember.updateNickName("dongjun");
        findMember.updatePassword(passwordEncoder, updatePassword);
        em.flush();

        //then
        Member findUpdateMember = memberRepository.findById(findMember.getId()).orElseThrow(() -> new Exception());

        Assertions.assertThat(findMember.equals(findUpdateMember));
        Assertions.assertThat(passwordEncoder.matches(updatePassword, findMember.getPassword())).isTrue();



    }
}