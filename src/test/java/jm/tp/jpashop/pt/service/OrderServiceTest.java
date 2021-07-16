package jm.tp.jpashop.pt.service;

import jm.tp.jpashop.pt.exception.NotEnoughStockException;
import jm.tp.jpashop.pt.exception.NotExit;
import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.model.DeliveryStatus;
import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.model.Order;
import jm.tp.jpashop.pt.model.OrderStatus;
import jm.tp.jpashop.pt.model.item.Album;
import jm.tp.jpashop.pt.model.item.Book;
import jm.tp.jpashop.pt.model.item.Item;
import jm.tp.jpashop.pt.model.item.Movie;
import jm.tp.jpashop.pt.repository.ItemRepository;
import jm.tp.jpashop.pt.repository.MemberRepository;
import jm.tp.jpashop.pt.repository.OrderRepository;
import jm.tp.jpashop.pt.repository.OrderSearch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired private ItemService itemService;

    private Long createTestItem(String name, int price, int stockQuantity) {
        Item book = Book.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
        itemRepository.save(book);
        return book.getId();
    }

    private Long createTestMember(String name, String city, String street, String etc) {
        Member member = Member.builder()
                .name(name)
                .address(Address.builder()
                        .city(city)
                        .street(street)
                        .etc(etc)
                        .build()
                ).build();

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
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getDelivery().getAddress()).isSameAs(member.getAddress());
        assertThat(order.getDelivery().getDeliveryStatus()).isEqualTo(DeliveryStatus.READY);
    }

    @Test
    @DisplayName("상품 재고를 초과하는 주문")
    public void order2() throws Exception {
        // given
        int itemQuantity = 10;
        Long memberId = createTestMember("서정민1", "서울", "잠실로", "234");
        Long itemId = createTestItem("토비의 Spring", 10000, itemQuantity);

        // when
        assertThrows(NotEnoughStockException.class, () -> {
            Long orderId = orderService.order(memberId, itemId, itemQuantity + 10);
        });

        // then
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
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("주문 취소를 하려 했으나 이미 배달 완료")
    public void orderCancel2() throws Exception {
        // given
        int itemQuantity = 10;
        int itemBuyCount = 5;
        Long memberId = createTestMember("서정민1", "서울", "잠실로", "234");
        Long itemId = createTestItem("토비의 Spring", 10000, itemQuantity);
        Long orderId = orderService.order(memberId, itemId, itemBuyCount);

        Order order = orderRepository.findById(orderId).orElseThrow(NotExit::new);

        // when
        order.getDelivery().complete();
        assertThrows(IllegalStateException.class, () -> {
            orderService.cancel(orderId);
        });

        // then
        Item item = itemRepository.findById(itemId).orElseThrow(NotExit::new);
        assertThat(item.getStockQuantity()).isEqualTo(itemQuantity - itemBuyCount);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    @DisplayName("이름과 주문 상태로 주문 조회")
    public void searchByNameAndStatus() throws Exception {
        // given
        Member member1 = Member.builder().name("서정민").build();
        Long memberId = memberService.join(member1);

        List<Item> items = itemService.findItems();

        // when
        Long orderId = orderService.order(memberId, items.get(0).getId(), 10);

        OrderSearch orderSearch = OrderSearch.builder()
                .memberName(member1.getName())
                .orderStatus(OrderStatus.ORDER)
                .build();

        // then
        List<Order> findOrdersByNameAndStatus = orderService.findOrders(orderSearch);

        assertThat(findOrdersByNameAndStatus.stream()
                .map(Order::getId)
                .collect(Collectors.toList())
        ).contains(orderId);
        assertThat(findOrdersByNameAndStatus).contains(orderRepository.findById(orderId).orElseThrow());
    }

    @Test
    @DisplayName("이름으로만 주문하기 (주문 상태는 ORDER, CANCEL 모두 조회)")
    public void searchByOnlyName() {
        // given
        Member member = Member.builder()
                .name("서정민")
                .build();
        Long memberId = memberService.join(member);

        Item album = Album.builder()
                .name("앨범")
                .stockQuantity(100)
                .build();
        Long itemId1 = itemService.saveItem(album);

        Movie movie = Movie.builder()
                .name("매트릭스")
                .stockQuantity(100)
                .build();
        Long itemId2 = itemService.saveItem(movie);

        Long orderId = orderService.order(memberId, itemId1, 10);
        Long orderId2 = orderService.order(memberId, itemId2, 10);

        // when
        OrderSearch orderSearch = OrderSearch.builder()
                .memberName(member.getName())
                .build();
        List<Order> orders = orderService.findOrders(orderSearch);

        // then
        List<Long> findOrderIdList = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        assertThat(findOrderIdList).contains(orderId, orderId2);
    }

    @Test
    @DisplayName("주문 상태로만 주문하기 (모든 이름의 고객에 대한 주문에 대해 ORDER 상태인 주문들 모두 조회)")
    public void searchByOnlyStatus() throws Exception {
        // given
        Member member = Member.builder()
                .name("서정민")
                .build();
        Long memberId = memberService.join(member);

        Item album = Album.builder()
                .name("앨범")
                .stockQuantity(100)
                .build();
        Long itemId1 = itemService.saveItem(album);

        Movie movie = Movie.builder()
                .name("매트릭스")
                .stockQuantity(100)
                .build();
        Long itemId2 = itemService.saveItem(movie);

        Long orderId = orderService.order(memberId, itemId1, 10);
        Long orderId2 = orderService.order(memberId, itemId2, 10);

        // when
        OrderSearch os = OrderSearch.builder()
                .orderStatus(OrderStatus.ORDER)
                .build();
        List<Order> orders = orderService.findOrders(os);

        // then
        assertThat(orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList())
        ).contains(orderId, orderId2);
    }
}