package jm.tp.jpashop.pt;

import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.model.Delivery;
import jm.tp.jpashop.pt.model.DeliveryStatus;
import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.model.Order;
import jm.tp.jpashop.pt.model.OrderItem;
import jm.tp.jpashop.pt.model.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 테스트용 데이터
 */
@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member = createMember("userA", "서울", "1", "1111");
            em.persist(member);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

            OrderItem orderItem3 = OrderItem.createOrderItem(book2, 20000, 10);
            OrderItem orderItem4 = OrderItem.createOrderItem(book2, 20000, 23);
            OrderItem orderItem5 = OrderItem.createOrderItem(book1, 20000, 15);

            Order order2 = Order.createOrder(member, delivery, orderItem5);
            Order order3 = Order.createOrder(member, delivery, orderItem3, orderItem4);
            em.persist(order2);
            em.persist(order3);
        }

        public void dbInit2() {
            Member member = createMember("userB", "진주", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);
            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

            OrderItem orderItem3 = OrderItem.createOrderItem(book2, 40000, 10);
            Order order2 = Order.createOrder(member, delivery, orderItem1, orderItem3);
            em.persist(order2);
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            return Member.builder()
                    .name(name)
                    .address(Address.builder()
                            .city(city)
                            .street(street)
                            .etc(zipcode)
                            .build())
                    .build();
        }

        private Book createBook(String name, int price, int stockQuantity) {
            return Book.builder()
                    .name(name)
                    .price(price)
                    .stockQuantity(stockQuantity)
                    .build();
        }

        private Delivery createDelivery(Member member) {
            return Delivery.builder()
                    .address(member.getAddress())
                    .deliveryStatus(DeliveryStatus.READY)
                    .build();
        }
    }
}
