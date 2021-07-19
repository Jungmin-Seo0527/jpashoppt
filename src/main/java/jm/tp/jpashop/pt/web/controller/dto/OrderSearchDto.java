package jm.tp.jpashop.pt.web.controller.dto;

import jm.tp.jpashop.pt.model.OrderStatus;
import jm.tp.jpashop.pt.repository.OrderSearch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class OrderSearchDto {
    private String memberName;
    private OrderStatus orderStatus;

    public OrderSearch toOrderSearch() {
        return OrderSearch.builder()
                .memberName(getMemberName())
                .orderStatus(getOrderStatus())
                .build();
    }
}
