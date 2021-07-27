package jm.tp.jpashop.pt.web.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.tp.jpashop.pt.exception.NotExitOrderException;
import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.model.OrderItem;
import jm.tp.jpashop.pt.model.item.Book;
import jm.tp.jpashop.pt.model.item.Item;
import jm.tp.jpashop.pt.service.ItemService;
import jm.tp.jpashop.pt.service.MemberService;
import jm.tp.jpashop.pt.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static java.nio.charset.StandardCharsets.UTF_8;
import static jm.tp.jpashop.pt.model.DeliveryStatus.READY;
import static jm.tp.jpashop.pt.model.OrderStatus.ORDER;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
@RequiredArgsConstructor
@DisplayName("OrderApiController 테스트")
@Transactional
@Slf4j
class OrderApiControllerTest {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final EntityManager em;

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
    @DisplayName("테스트 01. 주문 단건 조회 - 조회 성공: 주문 정보 반환")
    public void _01_findOrder() throws Exception {
        Member member = createTestMember();
        Book item = (Book) createTestItem();
        Long orderId = orderService.order(member.getId(), item.getId(), 10);

        ResultActions result = mockMvc.perform(
                get("/api/order/" + orderId)
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())

        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON + ";charset=utf-8"))
                .andExpect(jsonPath("$.data.orderId", is(orderId.intValue())))
                .andExpect(jsonPath("$.data.memberName", is(member.getName())))
                .andExpect(jsonPath("$.data.orderStatus", is(String.valueOf(ORDER))))
                .andExpect(jsonPath("$.data.deliveryStatus", is(String.valueOf(READY))))
                .andExpect(jsonPath("$.data.address.city", is(member.getAddress().getCity())))
                .andExpect(jsonPath("$.data.address.street", is(member.getAddress().getStreet())))
                .andExpect(jsonPath("$.data.address.etc", is(member.getAddress().getEtc())))
                .andReturn();
    }

    @Test
    @DisplayName("테스트 02. 주문 단건 조회 - 조회 실패: 존재하지 않는 주문")
    public void _02_findNoExitOrderNoExitException() throws Exception {
        // given
        Long noExitOrderId = -1L;

        // when
        ResultActions result = mockMvc.perform(
                get("/api/order/" + noExitOrderId)
                        .accept(APPLICATION_JSON)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(TEXT_PLAIN + ";charset=utf-8"))
                .andExpect(content().string(NotExitOrderException.ERROR_MESSAGE))
                .andReturn();
    }

    /**
     * 장바구니 기능의 부재로 service 계층에서 여러 상품을 한번에 주문 불가능
     */
    @Test
    @DisplayName("테스트 03. 주문 단건 조회(주문 상품 목록까지 조회) - 조회 성공")
    public void _03_findOrderItemsByOrderId() throws Exception {
        // given
        Member member = createTestMember();
        Book item1 = (Book) createTestItem();
        Book item2 = (Book) createTestItem();
        Book item3 = (Book) createTestItem();

        OrderItem orderItem1 = OrderItem.createOrderItem(item1, item1.getPrice(), 10);
        OrderItem orderItem2 = OrderItem.createOrderItem(item2, item2.getPrice(), 11);
        OrderItem orderItem3 = OrderItem.createOrderItem(item3, item3.getPrice(), 13);

        Long orderId = orderService.order(member.getId(), orderItem1, orderItem2, orderItem3);


        // when
        ResultActions result = mockMvc.perform(
                get("/api/orderItems2/" + orderId)
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON + ";charset=utf-8"))
                .andExpect(jsonPath("$.data.orderId", is(orderId.intValue())))
                .andExpect(jsonPath("$.data.memberName", is(member.getName())))
                .andReturn();
    }

    @Test
    @DisplayName("테스트 04. 주문 단건 조회(주문 상품 목록까지 조회) - 조회 실패: 존재하지 않는 주문")
    public void _04_findNotExitOrderItemsById() throws Exception {
        // given
        Long notExitOrderId = -1L;

        // when
        ResultActions result = mockMvc.perform(
                get("/api/orderItems2/" + notExitOrderId)
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(TEXT_PLAIN + ";charset=utf-8"))
                .andExpect(content().string(NotExitOrderException.ERROR_MESSAGE))
                .andReturn();
    }

    @Test
    @DisplayName("테스트 05. 주문 단건 조회(Repository 계층에서 DTO로 조회) - 조회 성공")
    public void _05_findOrderItemsByOrderIdSelectByDtoInRepository() throws Exception {
        // given
        Member member = createTestMember();
        Book item1 = (Book) createTestItem();
        Book item2 = (Book) createTestItem();
        Book item3 = (Book) createTestItem();

        OrderItem orderItem1 = OrderItem.createOrderItem(item1, item1.getPrice(), 10);
        OrderItem orderItem2 = OrderItem.createOrderItem(item2, item2.getPrice(), 11);
        OrderItem orderItem3 = OrderItem.createOrderItem(item3, item3.getPrice(), 13);

        Long orderId = orderService.order(member.getId(), orderItem1, orderItem2, orderItem3);

        // when
        ResultActions result = mockMvc.perform(
                get("/api/orderItems3/" + orderId)
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON + ";charset=utf-8"))
                .andExpect(jsonPath("$.data.orderId", is(orderId.intValue())))
                .andExpect(jsonPath("$.data.memberName", is(member.getName())))
                .andExpect(jsonPath("$.data.orderItemList[0].itemId", is(orderItem1.getItem().getId().intValue())))
                .andExpect(jsonPath("$.data.orderItemList[0].itemName", is(item1.getName())))
                .andExpect(jsonPath("$.data.orderItemList[0].orderPrice", is(item1.getPrice())))
                .andExpect(jsonPath("$.data.orderItemList[0].orderCount", is(10)))
                .andExpect(jsonPath("$.data.orderItemList[0].totalPrice", is(item1.getPrice() * 10)))
                .andReturn();
    }

    @Test
    @DisplayName("테스트 06. 주문 단건 조회(Repository 계층에서 DTO로 조회) - 조회 실패(존재하지 않는 주문)")
    public void _06_test() throws Exception {
        // given
        Long notExitOrderId = -1L;

        // when
        ResultActions result = mockMvc.perform(
                get("/api/orderItems3/" + notExitOrderId)
                        .accept(APPLICATION_JSON, TEXT_PLAIN)
                        .characterEncoding(UTF_8.displayName())
        );

        // then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(TEXT_PLAIN + ";charset=utf-8"))
                .andExpect(content().string(NotExitOrderException.ERROR_MESSAGE))
                .andReturn();
    }
}