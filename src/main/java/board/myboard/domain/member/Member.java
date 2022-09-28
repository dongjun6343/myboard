package board.myboard.domain.member;


import board.myboard.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

/**
 * 요구사항
 * 1. 아이디는 중복될 수 없습니다.
 * 2. 비밀번호, 이름, 닉네임, 나이는 변경할 수 있습니다.
 * 3. 비밀번호는 암호화가 되어 DB에 저장합니다.
 * 4. 모든 엔터티는 등록시간과 수정시간을 등록합니다. (JPA Auditing 사용)
 * 5. 로그인에 성공하면 JWT를 발급해주고 사용자는 게시판 서비스 API를 이용할 수 있습니다.
 * 6. 회원가입으로는 USER권한의 회원만 가입할 수 있고, ADMIN권한은 직접 DB를 통해 입력해서 지정합니다.
 */


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id; //PK

    @Column(nullable = false, length = 30, unique = true)
    private String userid; //아이디

    private String password;

    @Column(nullable = false, length = 30)
    private String name; // 이름

    @Column(nullable = false, length = 30)
    private String nickName;  // 닉네임

    @Column(nullable = false, length = 30)
    private int age; // 나이

    @Enumerated(EnumType.STRING)
    private Role role; // 권한

    //정보 수정
    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.password = passwordEncoder.encode(password);
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    public void updateAge(int age){
        this.age = age;
    }

    // 패스워드 암호화.
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }

    @Builder
    public Member(String userid, String password, String name, String nickName, int age, Role role) {
        this.userid = userid;
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.age = age;
        this.role = role;
    }
}
