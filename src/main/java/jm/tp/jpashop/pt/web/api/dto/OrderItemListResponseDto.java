package jm.tp.jpashop.pt.web.api.dto;

import jm.tp.jpashop.pt.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class OrderItemListResponseDto {

    private Long orderId;

    private String memberName;

    private LocalDateTime orderDate;

    @Builder.Default
    private List<OrderItemDto> orderItemList = new ArrayList<>();

    private int totalPrice;

    public static OrderItemListResponseDto create(Order order) {
        OrderItemListResponseDto dto = OrderItemListResponseDto.builder()
                .orderId(order.getId())
                .memberName(order.getMember().getName())
                .orderDate(order.getOrderDate())
                .build();

        order.getOrderItems().stream()
                .map(OrderItemDto::create)
                .forEach(orderItemDto -> dto.orderItemList.add(orderItemDto));

        dto.setTotalPrice(dto.getOrderItemList().stream().mapToInt(OrderItemDto::getTotalPrice).sum());

        return dto;
    }
}
