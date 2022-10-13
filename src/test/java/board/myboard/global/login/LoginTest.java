package board.myboard.global.login;

import board.myboard.domain.member.Member;
import board.myboard.domain.member.Role;
import board.myboard.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


@AutoConfigureMockMvc
@Transactional
@SpringBootTest
public class LoginTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    ObjectMapper objectMapper = new ObjectMapper();

    private static String KEY_USERID = "userid";
    private static String KEY_PASSWORD = "password";
    private static String USERID = "userid";
    private static String PASSWORD = "123456789";

    private static String POST_URL = "/login";

    public void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    private void init(){
        memberRepository.save(Member.builder()
                        .userid(USERID)
                        .password(passwordEncoder.encode(PASSWORD))
                        .name("MEMBER1")
                        .nickName("NickName1")
                        .role(Role.USER)
                        .age(28)
                        .build());
        clear();
    }

    private Map getUseridPasswordSetting(String userid, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERID, userid);
        map.put(KEY_PASSWORD, password);
        return map;
    }

    private ResultActions perform(String url, MediaType mediaType, Map useridAndPassword) throws Exception{
        return mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(mediaType)
                .contentType(objectMapper.writeValueAsString(useridAndPassword)));
    }

    @Test
    public void 로그인_성공() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERID, PASSWORD);
        //when
        MvcResult result = perform(POST_URL, MediaType.APPLICATION_JSON, map).
                andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        //then

    }

    @Test
    public void 로그인_아이디_오류_실패() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERID + "AAA", PASSWORD);
        //when
        MvcResult result = perform(POST_URL, MediaType.APPLICATION_JSON, map)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        //then
    }

    @Test
    public void 로그인_비밀번호_오류_실패() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERID,PASSWORD+"AAA");
        //when
        MvcResult result = perform(POST_URL, MediaType.APPLICATION_JSON, map)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        //then
    }

    @Test
    public void 로그인_URL_오류_실패() throws  Exception{
        //given
        Map<String, String> map = getUseridPasswordSetting(USERID, PASSWORD);
        //when
        perform(POST_URL+"111", MediaType.APPLICATION_JSON, map)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        //then
    }

    @Test
    public void 로그인_데이터형식_JSON_아님_실패() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERID, PASSWORD);
        //when
        perform(POST_URL, MediaType.APPLICATION_XML, map)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        //then
    }

    @Test
    public void 로그인_HTTP_METHOD_GET_실패() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERID, PASSWORD);
        //when
        ResultActions resultActions =  mockMvc.perform(MockMvcRequestBuilders
                        .get(POST_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        //then
    }
}
