package jm.tp.jpashop.pt.web.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.model.Member;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        RequestBuilder requestBuilder = post("/api/member4")
                .content(objectMapper.writeValueAsString(memberApiDto))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON, TEXT_PLAIN)
                .characterEncoding(UTF_8.displayName());

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON + ";charset=utf-8"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name", is("서정민")))
                .andExpect(jsonPath("$.data.address.city", is("인천")))
                .andExpect(jsonPath("$.data.address.street", is("마장로 264번길 66")))
                .andExpect(jsonPath("$.data.address.etc", is("경남 502동 706호")))
                .andReturn()
                .getResponse();
    }

    @Test
    @DisplayName("테스트 02. 이미 존재하는 이름으로 가입을 하면 에러")
    public void duplicateNameJoinMember() throws Exception {
        // given
        MemberApiDto member1 = MemberApiDto.builder()
                .name("서정민")
                .address(Address.builder()
                        .city("ccc")
                        .street("sss")
                        .etc("111")
                        .build())
                .build();
        memberService.join(member1);

        MemberApiDto member2 = MemberApiDto.builder()
                .name("서정민")
                .address(Address.builder()
                        .city("cccc")
                        .street("sssss")
                        .etc("4444")
                        .build())
                .build();
        // when
        ResultActions result = mockMvc.perform(
                post("/api/member4")
                        .content(objectMapper.writeValueAsString(member2))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())

        );

        // then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(APPLICATION_JSON + ";charset=utf-8"))
                .andExpect(jsonPath("error", is("이름이 중복되는 회원이 존재합니다.")))
                .andExpect(jsonPath("data.name", is("서정민")))
                .andReturn()
                .getResponse();
    }

    @Test
    @DisplayName("테스트 03. 모든 회원 목록 조회 요청")
    @Transactional(readOnly = true)
    public void memberList() throws Exception {

        // when
        ResultActions result = mockMvc.perform(
                get("/api/members")
                        .accept(APPLICATION_JSON)

        );

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON + ";charset=utf-8"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()", is(memberService.countMember().intValue())));
    }

    @Test
    @DisplayName("테스트 04. 특정 회원 조회")
    public void findMemberInfo() throws Exception {
        // given
        Member member = Member.builder()
                .name("서정민")
                .address(Address.builder()
                        .city("인천")
                        .street("마장로")
                        .etc("경남")
                        .build())
                .build();
        Long memberId = memberService.join(member);

        // when
        ResultActions result = mockMvc.perform(
                get("/api/member/" + memberId)
                        .accept(APPLICATION_JSON)
        );

        // then
        result.andDo(print());
    }

    @Test
    @DisplayName("테스트 05. 존재하지 않는 id의 회원 조회")
    public void findNoExistMemberInfo() throws Exception {
        // given

        // when
        int notExistId = -1;
        ResultActions result = mockMvc.perform(
                get("/api/member/" + notExistId)
        );

        // then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(TEXT_PLAIN + ";charset=utf-8"))
                .andExpect(content().string("id:" + notExistId + "의 회원은 존재하지 않습니다."));
    }
}