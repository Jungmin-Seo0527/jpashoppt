package jm.tp.jpashop.pt.web.api.dto;

import jm.tp.jpashop.pt.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Data @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class OrderItemDto {

    private Long itemId;
    private String itemName;
    private int orderPrice;
    private int orderCount;
    private int totalPrice;

    public static OrderItemDto create(OrderItem orderItem) {
        return OrderItemDto.builder()
                .itemId(orderItem.getItem().getId())
                .itemName(orderItem.getItem().getName())
                .orderPrice(orderItem.getOrderPrice())
                .orderCount(orderItem.getCount())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }
}
