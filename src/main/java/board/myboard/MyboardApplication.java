package board.myboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * start.spring.io
 * 1. Web : 웹 관련 기능
 * 2. JPA : JPA 사용
 * 3. Security : 인증 관련 기능
 * 4. Lombok : 롬복 사용
 * 5. Validation : Http요청 시 데이터 형식 검증
 * 6. H2 : 간편하게 사용하는 DB(배포 시 다른 DB로 변경해야함)
 *
 * ======================================================
 *
 * 초기 세팅
 * 1. Setting => gradle => Build  and ... , Run Test... intellij IDEA로 선택
 * (Gradle로 설정할 경우 실행을 Gradle에게 위임하므로  시간이 조금 오래걸림.)
 *
 * 2. Setting => AnnotationProcessors => Enable annotation .. 체크박스 클릭
 * ( AnnotationProcessors : 컴파일 시점에 Annotation 기반으로 코드를 변경하거나 생성하는 방법
 *   Lombok을 통해서 코드를 생성할 것이므로 체크해야 Lombok을 사용할 수 있음.)
 *
 */

@EnableJpaAuditing
@SpringBootApplication
public class MyboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyboardApplication.class, args);
	}

}
