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
@DisplayName("MemberApiController ?????????")
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
                        .city("??????" + testMemberCnt)
                        .street("?????????" + (testMemberCnt * 10 + 1))
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
                .author("?????????" + testItemCnt)
                .isbn("???" + testItemCnt)
                .build();
        itemService.saveItem(item);
        return item;
    }

    @Test
    @DisplayName("????????? 01. ???????????? ?????? ????????? ????????? ???????????? 200??? ????????? ?????? ?????? ????????? ????????????.")
    void _01_joinMemberSuccess() throws Exception {

        // given
        MemberApiDto memberApiDto = MemberApiDto.builder()
                .name("?????????")
                .address(Address.builder()
                        .city("??????")
                        .street("????????? 264?????? 66")
                        .etc("?????? 502??? 706???")
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
                .andExpect(jsonPath("$.data.name", is("?????????")))
                .andExpect(jsonPath("$.data.address.city", is("??????")))
                .andExpect(jsonPath("$.data.address.street", is("????????? 264?????? 66")))
                .andExpect(jsonPath("$.data.address.etc", is("?????? 502??? 706???")))
                .andReturn()
                .getResponse();
    }

    @Test
    @DisplayName("????????? 02. ?????? ???????????? ???????????? ????????? ?????? ??????")
    public void _02_duplicateNameJoinMember() throws Exception {
        // given
        MemberApiDto member1 = MemberApiDto.builder()
                .name("?????????")
                .address(Address.builder()
                        .city("ccc")
                        .street("sss")
                        .etc("111")
                        .build())
                .build();
        memberService.join(member1);

        MemberApiDto member2 = MemberApiDto.builder()
                .name("?????????")
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
                .andExpect(jsonPath("error", is("????????? ???????????? ????????? ???????????????.")))
                .andExpect(jsonPath("data.name", is("?????????")))
                .andReturn()
                .getResponse();
    }

    @Test
    @DisplayName("????????? 03. ?????? ?????? ?????? ?????? ??????")
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
    @DisplayName("????????? 04. ?????? ?????? ??????")
    public void _04_findMemberInfo() throws Exception {
        // given
        Member member = Member.builder()
                .name("?????????")
                .address(Address.builder()
                        .city("??????")
                        .street("?????????")
                        .etc("??????")
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
    @DisplayName("????????? 05. ???????????? ?????? id??? ?????? ??????")
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
    @DisplayName("????????? 06. ?????? ?????? ?????? - ????????? ??????")
    public void _06_updateMemberInfo() throws Exception {
        // given
        Member member = Member.builder()
                .name("?????????")
                .address(Address.builder()
                        .city("??????")
                        .street("????????? 264?????? 66")
                        .etc("?????? 502??? 706???")
                        .build())
                .build();
        Long memberId = memberService.join(member);

        MemberApiDto memberApiDto = MemberApiDto.create(member);
        String updateName = "?????????1";
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
                .andExpect(jsonPath("$.data.address.city", is("??????")))
                .andExpect(jsonPath("$.data.address.street", is("????????? 264?????? 66")))
                .andExpect(jsonPath("$.data.address.etc", is("?????? 502??? 706???")));
    }

    @Test
    @DisplayName("????????? 07. ????????? ?????? ?????? ??????")
    public void _07_updateMemberInfoNameAndAddress() throws Exception {
        // given
        Member member = Member.builder()
                .name("?????????")
                .address(Address.builder()
                        .city("??????")
                        .street("????????? 264?????? 66")
                        .etc("?????? 502??? 706???")
                        .build())
                .build();
        Long memberId = memberService.join(member);

        MemberApiDto memberApiDto = MemberApiDto.create(member);
        String updateName = "?????????1";
        Address updateAddress = Address.builder()
                .city("??????")
                .street("??????")
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
    @DisplayName("????????? 08. ???????????? ?????? ID??? ?????? ?????? ?????? ?????? - 400??????")
    public void _08_noExistMemberUpdateRequest() throws Exception {
        // given
        Long noExistMemberId = -1L;
        MemberApiDto memberApiDto = MemberApiDto.builder()
                .name("?????????")
                .address(Address.builder()
                        .city("??????")
                        .street("????????? 264?????? 66")
                        .etc("?????? 502??? 706???")
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

    // TODO: 2021-07-27 ?????? ?????? ?????? ?????? ????????? ?????? (???????????? ?????? ????????? ?????? ?????? ?????? ????????? ?????? ?????? ?????? ??????)

    @Test
    @DisplayName("????????? 09. ?????? id??? ????????? ?????? ?????? ?????? ?????? - ?????? ??????(???????????? ?????? id)")
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
    @DisplayName("????????? 10. ?????? id??? ????????? ?????? ?????? ?????? ?????? - ?????? ??????(???????????? ?????? ?????? id):" +
            " NotExitMemberException ??????")
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