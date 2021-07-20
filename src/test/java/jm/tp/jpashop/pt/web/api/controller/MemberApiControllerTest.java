package jm.tp.jpashop.pt.web.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.service.MemberService;
import jm.tp.jpashop.pt.web.api.dto.MemberApiDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
@RequiredArgsConstructor
@DisplayName("MemberApiController 테스트")
@Transactional
class MemberApiControllerTest {

    private final MemberService memberService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Test
    @DisplayName("테스트 01. 존재하지 않는 이름의 회원이 가입하면 200의 상태와 가입 회원 정보를 반환한다.")
    void joinMemberSuccess() throws Exception {

        // given
        MemberApiDto memberApiDto = MemberApiDto.builder()
                .name("서정민")
                .address(Address.builder()
                        .city("인천")
                        .street("마장로 264번길 66")
                        .etc("경남 502동 706호")
                        .build())
                .build();

        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/member4")
                .content(objectMapper.writeValueAsString(memberApiDto))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON, TEXT_PLAIN)
                .characterEncoding(StandardCharsets.UTF_8.displayName());

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name", is("서정민")))
                .andExpect(jsonPath("$.data.address.city", is("인천")))
                .andExpect(jsonPath("$.data.address.street", is("마장로 264번길 66")))
                .andExpect(jsonPath("$.data.address.etc", is("경남 502동 706호")))
                .andReturn()
                .getResponse();
    }
}