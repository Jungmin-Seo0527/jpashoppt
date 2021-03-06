package jm.tp.jpashop.pt.web.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.tp.jpashop.pt.exception.NotExitMemberException;
import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.model.OrderItem;
import jm.tp.jpashop.pt.model.item.Book;
import jm.tp.jpashop.pt.model.item.Item;
import jm.tp.jpashop.pt.service.ItemService;
import jm.tp.jpashop.pt.service.MemberService;
import jm.tp.jpashop.pt.service.OrderService;
import jm.tp.jpashop.pt.web.api.dto.MemberApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class MemberApiControllerTest {

    private final MemberService memberService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private int testMemberCnt;
    private int testItemCnt;

    private Member createTestMember() {
        Member testMember = Member.builder()
                .name("user" + ++testMemberCnt)
                .address(Address.builder()
                        .city("인천" + testMemberCnt)
                        .street("마장로" + (testMemberCnt * 10 + 1))
                        .etc(String.valueOf(testMemberCnt * 100 + 23 - 1))
                        .build())
                .build();
        memberService.join(testMember);
        return testMember;
    }

    private Item createTestItem() {
        Book item = Book.builder()
                .name("book" + ++testItemCnt)
                .stockQuantity(testItemCnt * 100)
                .price(testItemCnt)
                .author("서정민" + testItemCnt)
                .isbn("???" + testItemCnt)
                .build();
        itemService.saveItem(item);
        return item;
    }

    @Test
    @DisplayName("테스트 01. 존재하지 않는 이름의 회원이 가입하면 200의 상태와 가입 회원 정보를 반환한다.")
    void _01_joinMemberSuccess() throws Exception {

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
    public void _02_duplicateNameJoinMember() throws Exception {
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
    public void _03_memberList() throws Exception {

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
    public void _04_findMemberInfo() throws Exception {
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
    public void _05_findNoExistMemberInfo() throws Exception {
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
                .andExpect(content().string(NotExitMemberException.ERROR_MESSAGE));
    }

    @Test
    @DisplayName("테스트 06. 회원 정보 수정 - 이름만 변경")
    public void _06_updateMemberInfo() throws Exception {
        // given
        Member member = Member.builder()
                .name("서정민")
                .address(Address.builder()
                        .city("인천")
                        .street("마장로 264번길 66")
                        .etc("경남 502동 706호")
                        .build())
                .build();
        Long memberId = memberService.join(member);

        MemberApiDto memberApiDto = MemberApiDto.create(member);
        String updateName = "서정민1";
        memberApiDto.setName(updateName);

        // when
        ResultActions result = mockMvc.perform(
                post("/api/member/" + memberId)
                        .content(objectMapper.writeValueAsString(memberApiDto))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON + ";charset=utf-8"))
                .andExpect(jsonPath("$.data.name", is(updateName)))
                .andExpect(jsonPath("$.data.address.city", is("인천")))
                .andExpect(jsonPath("$.data.address.street", is("마장로 264번길 66")))
                .andExpect(jsonPath("$.data.address.etc", is("경남 502동 706호")));
    }

    @Test
    @DisplayName("테스트 07. 이름과 주소 모두 변경")
    public void _07_updateMemberInfoNameAndAddress() throws Exception {
        // given
        Member member = Member.builder()
                .name("서정민")
                .address(Address.builder()
                        .city("인천")
                        .street("마장로 264번길 66")
                        .etc("경남 502동 706호")
                        .build())
                .build();
        Long memberId = memberService.join(member);

        MemberApiDto memberApiDto = MemberApiDto.create(member);
        String updateName = "서정민1";
        Address updateAddress = Address.builder()
                .city("서울")
                .street("거리")
                .etc("111")
                .build();
        memberApiDto.setName(updateName);
        memberApiDto.setAddress(updateAddress);

        // when
        ResultActions result = mockMvc.perform(
                post("/api/member/" + memberId)
                        .content(objectMapper.writeValueAsString(memberApiDto))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON + ";charset=utf-8"))
                .andExpect(jsonPath("$.data.name", is(updateName)))
                .andExpect(jsonPath("$.data.address.city", is(updateAddress.getCity())))
                .andExpect(jsonPath("$.data.address.street", is(updateAddress.getStreet())))
                .andExpect(jsonPath("$.data.address.etc", is(updateAddress.getEtc())));
    }

    @Test
    @DisplayName("테스트 08. 존재하지 않는 ID의 회원 정보 수정 요청 - 400에러")
    public void _08_noExistMemberUpdateRequest() throws Exception {
        // given
        Long noExistMemberId = -1L;
        MemberApiDto memberApiDto = MemberApiDto.builder()
                .name("서정민")
                .address(Address.builder()
                        .city("인천")
                        .street("마장로 264번길 66")
                        .etc("경남 502동 706호")
                        .build())
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/member/" + noExistMemberId)
                        .content(objectMapper.writeValueAsString(memberApiDto))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(TEXT_PLAIN + ";charset=utf-8"))
                .andExpect(content().string(NotExitMemberException.ERROR_MESSAGE));
    }

    // TODO: 2021-07-27 회원 주문 내역 조회 테스트 코드 (존재하지 않는 회원에 대한 주문 내역 조회시 예외 발생 구현 요망)

    @Test
    @DisplayName("테스트 09. 회원 id로 회원의 모든 주문 내역 조회 - 조회 성공(존재하는 회원 id)")
    public void _09_findDetailOrderAndOrderItemsByMemberId() throws Exception {
        // given
        Member testMember = createTestMember();
        Item testItem1 = createTestItem();
        Item testItem2 = createTestItem();
        Item testItem3 = createTestItem();

        OrderItem orderItem1 = OrderItem.createOrderItem(testItem1, testItem1.getPrice(), 10);
        OrderItem orderItem2 = OrderItem.createOrderItem(testItem2, testItem2.getPrice(), 10);
        OrderItem orderItem3 = OrderItem.createOrderItem(testItem3, testItem3.getPrice(), 10);
        OrderItem orderITem4 = OrderItem.createOrderItem(testItem1, testItem1.getPrice() / 10 * 9, 10);

        Long orderId1 = orderService.order(testMember.getId(), orderItem1, orderItem2);
        Long orderId2 = orderService.order(testMember.getId(), orderItem3);
        Long orderId3 = orderService.order(testMember.getId(), orderITem4);


        // when
        ResultActions result = mockMvc.perform(
                get("/api/member/" + testMember.getId() + " /orders")
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON + ";charset=utf-8"))
                .andExpect(jsonPath("$.data[0].orderId", is(orderId1.intValue())))
                .andExpect(jsonPath("$.data[0].memberName", is(testMember.getName())))
                .andExpect(jsonPath("$.data[0].orderItemList[0].itemId", is(testItem1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].orderItemList[0].itemName", is(testItem1.getName())))
                .andExpect(jsonPath("$.data[0].orderItemList[0].orderPrice", is(testItem1.getPrice())))
                .andExpect(jsonPath("$.data[0].orderItemList[0].totalPrice", is(10)))
                .andExpect(jsonPath("$.data[0].orderItemList[1].itemName", is(testItem2.getName())))
                .andExpect(jsonPath("$.data[1].orderId", is(orderId2.intValue())))
                .andExpect(jsonPath("$.data[1].orderItemList[0].itemName", is(testItem3.getName())))
                .andExpect(jsonPath("$.data[2].orderId", is(orderId3.intValue())))
                .andExpect(jsonPath("$.data[2].orderItemList[0].itemName", is(testItem1.getName())))
                .andExpect(jsonPath("$.data[2].orderItemList[0].orderPrice", is(testItem1.getPrice() / 10 * 9)));
    }

    @Test
    @DisplayName("테스트 10. 회원 id로 회원의 모든 주문 내역 조회 - 조회 실패(존재하지 않는 회원 id):" +
            " NotExitMemberException 예외")
    public void _10_findDetailOrderAndOrderItemsByMemberIdAndNotExitMemberException() throws Exception {
        // given
        Long noExitMemberId = -1L;

        // when
        ResultActions result = mockMvc.perform(
                get("/api/member/" + noExitMemberId + " /orders")
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(TEXT_PLAIN + ";charset=utf-8"))
                .andExpect(content().string(NotExitMemberException.ERROR_MESSAGE));
    }
}