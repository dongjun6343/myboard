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

    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "username";
    private static String PASSWORD = "123456789";

    private static String POST_URL = "/login";

    public void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    private void init(){
        memberRepository.save(Member.builder()
                        .username(USERNAME)
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
        map.put(KEY_USERNAME, userid);
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
    public void ?????????_??????() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERNAME, PASSWORD);
        //when
        MvcResult result = perform(POST_URL, MediaType.APPLICATION_JSON, map).
                andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        //then

    }

    @Test
    public void ?????????_?????????_??????_??????() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERNAME + "AAA", PASSWORD);
        //when
        MvcResult result = perform(POST_URL, MediaType.APPLICATION_JSON, map)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        //then
    }

    @Test
    public void ?????????_????????????_??????_??????() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERNAME,PASSWORD+"AAA");
        //when
        MvcResult result = perform(POST_URL, MediaType.APPLICATION_JSON, map)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        //then
    }

    @Test
    public void ?????????_URL_??????_??????() throws  Exception{
        //given
        Map<String, String> map = getUseridPasswordSetting(USERNAME, PASSWORD);
        //when
        perform(POST_URL+"111", MediaType.APPLICATION_JSON, map)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        //then
    }

    @Test
    public void ?????????_???????????????_JSON_??????_??????() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERNAME, PASSWORD);
        //when
        perform(POST_URL, MediaType.APPLICATION_XML, map)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        //then
    }

    @Test
    public void ?????????_HTTP_METHOD_GET_??????() throws Exception {
        //given
        Map<String, String> map = getUseridPasswordSetting(USERNAME, PASSWORD);
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
