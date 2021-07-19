package jm.tp.jpashop.pt.web.controller.dto;

import jm.tp.jpashop.pt.model.Order;
import jm.tp.jpashop.pt.model.OrderItem;
import jm.tp.jpashop.pt.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class OrderListResponseForm {
    private Long id;
    private String memberName;
    private List<OrderItem> orderItems;
    private OrderStatus status;
    private LocalDateTime orderDate;

    public static OrderListResponseForm create(Order order) {
        return OrderListResponseForm.builder()
                .id(order.getId())
                .memberName(order.getMember().getName())
                .orderItems(order.getOrderItems())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .build();
    }
}
