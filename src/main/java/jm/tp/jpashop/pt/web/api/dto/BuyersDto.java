package jm.tp.jpashop.pt.web.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyersDto {

    private Long memberId;
    private String memberName;
    private int orderCnt;

    @Builder.Default
    private List<OrderDto> orders = new ArrayList<>();

    @QueryProjection
    public BuyersDto(Long memberId, String memberName) {
        this.memberId = memberId;
        this.memberName = memberName;
    }

    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
        orderCnt = orders.size();
    }
}
