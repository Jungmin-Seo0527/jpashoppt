package jm.tp.jpashop.pt.service;

import jm.tp.jpashop.pt.exception.NotEnoughStockException;
import jm.tp.jpashop.pt.exception.NotExit;
import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.model.Order;
import jm.tp.jpashop.pt.model.OrderStatus;
import jm.tp.jpashop.pt.model.item.Book;
import jm.tp.jpashop.pt.model.item.Item;
import jm.tp.jpashop.pt.repository.ItemRepository;
import jm.tp.jpashop.pt.repository.MemberRepository;
import jm.tp.jpashop.pt.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderServiceTest {

    @Autowired private OrderService orderService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private MemberService memberService;
    @Autowired private OrderRepository orderRepository;

    private Long createTestItem(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        itemRepository.save(book);
        return book.getId();
    }

    private Long createTestMember(String name, String city, String street, String etc) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, etc));
        memberService.join(member);
        return member.getId();
    }

    @Test
    @DisplayName("상품 주문 - 수량이 10개가 남아 있는 상품 5개 주문")
    public void order() {
        // given
        Long memberId = createTestMember("서정민", "인천", "마장로", "264");
        Long itemId = createTestItem("JPA", 1000, 10);
        Member member = memberRepository.findById(memberId);
        Item item = itemRepository.findById(itemId).orElseThrow(NotExit::new);

        // when
        Long orderId = orderService.order(memberId, itemId, 5);

        // then
        assertThat(item.getStockQuantity()).isEqualTo(5);
        Order order = orderRepository.findById(orderId).orElseThrow(NotExit::new);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getDelivery().getAddress()).isSameAs(member.getAddress());
    }

    @Test
    @DisplayName("상품 재고를 초과하는 주문")
    public void order2() throws Exception {
        // given
        int itemQuantity = 10;
        Long memberId = createTestMember("서정민1", "서울", "잠실로", "234");
        Long itemId = createTestItem("토비의 Spring", 10000, itemQuantity);

        // when

        // then
        assertThrows(NotEnoughStockException.class, () -> {
            Long orderId = orderService.order(memberId, itemId, itemQuantity + 10);
        });
        Item item = itemRepository.findById(itemId).orElseThrow(NotExit::new);
        assertThat(item.getStockQuantity()).isEqualTo(itemQuantity);
    }

    @Test
    @DisplayName("주문 취소")
    public void orderCancel() throws Exception {
        // given
        int itemQuantity = 10;
        int itemBuyCount = 5;
        Long memberId = createTestMember("서정민1", "서울", "잠실로", "234");
        Long itemId = createTestItem("토비의 Spring", 10000, itemQuantity);
        Long orderId = orderService.order(memberId, itemId, itemBuyCount);

        // when
        orderService.cancel(orderId);

        // then
        Item item = itemRepository.findById(itemId).orElseThrow(NotExit::new);
        Order order = orderRepository.findById(orderId).orElseThrow(NotExit::new);
        assertThat(item.getStockQuantity()).isEqualTo(itemQuantity);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
    }
}