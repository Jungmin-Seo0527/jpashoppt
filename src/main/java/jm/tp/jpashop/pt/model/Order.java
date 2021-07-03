package jm.tp.jpashop.pt.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "orders")
@RequiredArgsConstructor
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;
    private Member member;
    private List<OrderItem> orderItems = new ArrayList<>();
    private Delivery delivery;

    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
}
