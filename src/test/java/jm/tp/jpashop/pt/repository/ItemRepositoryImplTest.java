package jm.tp.jpashop.pt.repository;

import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.model.Delivery;
import jm.tp.jpashop.pt.model.DeliveryStatus;
import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.model.OrderItem;
import jm.tp.jpashop.pt.model.item.Book;
import jm.tp.jpashop.pt.model.item.Item;
import jm.tp.jpashop.pt.service.ItemService;
import jm.tp.jpashop.pt.service.MemberService;
import jm.tp.jpashop.pt.service.OrderService;
import jm.tp.jpashop.pt.web.api.dto.ItemBuyersDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DisplayName("ItemRepositoryImpl 테스트")
class ItemRepositoryImplTest {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;
    private final ItemRepositoryImpl itemRepository;
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

    private Delivery createTestDelivery(Member member) {
        return Delivery.builder()
                .address(member.getAddress())
                .deliveryStatus(DeliveryStatus.READY)
                .build();
    }


    @Test
    @DisplayName("orderItemIdList 조회 - 임시 테스트")
    public void findOrderItemIdListByItemId() {
        // given
        Member member1 = createTestMember();
        Member member2 = createTestMember();

        Book item1 = (Book) createTestItem();
        Book item2 = (Book) createTestItem();
        Book item3 = (Book) createTestItem();
        Book item4 = (Book) createTestItem();

        OrderItem orderItem1 = OrderItem.createOrderItem(item1, 10, 2);
        OrderItem orderItem2 = OrderItem.createOrderItem(item1, 10, 3);
        OrderItem orderItem3 = OrderItem.createOrderItem(item1, 20, 3);

        OrderItem orderItem4 = OrderItem.createOrderItem(item2, 20, 1);
        OrderItem orderItem5 = OrderItem.createOrderItem(item2, 20, 1);

        OrderItem orderItem6 = OrderItem.createOrderItem(item3, 10, 1);

        OrderItem orderItem7 = OrderItem.createOrderItem(item4, 10, 1);


        orderService.order(member1.getId(), orderItem1, orderItem4, orderItem7);
        orderService.order(member2.getId(), orderItem2, orderItem5, orderItem6);
        orderService.order(member2.getId(), orderItem3);

        // when
        List<Long> orderItem1IdList = itemRepository.findOrderItemIdById(item1.getId());
        List<Long> orderItem2IdList = itemRepository.findOrderItemIdById(item2.getId());
        List<Long> orderItem3IdList = itemRepository.findOrderItemIdById(item3.getId());
        List<Long> orderItem4IdList = itemRepository.findOrderItemIdById(item4.getId());
        List<Long> orderItemNoExitIdList = itemRepository.findOrderItemIdById(-1L);

        System.out.println("----------------------------------------------------------");
        System.out.println("member1 = " + member1.getId());
        System.out.println("member2 = " + member2.getId());
        ItemBuyersDto result = itemRepository.findItemBuyersById(item1.getId());
        System.out.println("result = " + result);


        // then
        assertThat(orderItem1IdList).containsExactly(orderItem1.getId(), orderItem2.getId(), orderItem3.getId());
        assertThat(orderItem2IdList).containsExactly(orderItem4.getId(), orderItem5.getId());
        assertThat(orderItem3IdList).containsExactly(orderItem6.getId());
        assertThat(orderItem4IdList).containsExactly(orderItem7.getId());
        assertThat(orderItemNoExitIdList).isEmpty();
    }
}