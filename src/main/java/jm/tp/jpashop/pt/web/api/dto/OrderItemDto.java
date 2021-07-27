package jm.tp.jpashop.pt.web.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private Long orderId;
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

    public OrderItemDto(Long itemId, String itemName, int orderPrice, int orderCount, int totalPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.orderCount = orderCount;
        this.totalPrice = totalPrice;
    }
}
