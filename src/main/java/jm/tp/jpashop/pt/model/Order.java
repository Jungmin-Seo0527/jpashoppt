package jm.tp.jpashop.pt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Table(name = "orders")
@Getter @Builder @Setter(PRIVATE)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @JsonIgnore
    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int totalPrice;

    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = Order.builder()
                .status(OrderStatus.ORDER)
                .orderDate(LocalDateTime.now())
                .build();
        order.setMember(member);
        order.setDelivery(delivery);

        stream(orderItems).forEach(order::addOrderItem);
        order.setTotalPrice(stream(orderItems)
                .mapToInt(OrderItem::getTotalPrice)
                .sum());

        return order;
    }

    // ???????????? ?????????

    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.putBucket(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.matchingOrder(this);
    }

    // === business logic

    public void changeOrderStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }

    public void cancel() {
        if (getDelivery().getDeliveryStatus() == DeliveryStatus.COMP)
            throw new IllegalStateException("????????? ????????? ????????? ????????? ????????? ?????????.");
        changeOrderStatus(OrderStatus.CANCEL);

        orderItems.forEach(OrderItem::cancel);
    }

    // --- inquiry logic

    public int getTotalPrice() {
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
