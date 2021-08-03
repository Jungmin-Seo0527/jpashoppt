package jm.tp.jpashop.pt.web.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDto {

    @JsonIgnore
    private Long orderId;

    @JsonIgnore
    private Long memberId;

    private int orderPrice;
    private int orderItemCnt;
    private LocalDateTime orderDate;

    @QueryProjection

    public OrderDto(Long orderId, Long memberId, int orderPrice, int orderItemCnt, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.orderPrice = orderPrice;
        this.orderItemCnt = orderItemCnt;
        this.orderDate = orderDate;
    }
}
