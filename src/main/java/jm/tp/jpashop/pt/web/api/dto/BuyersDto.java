package jm.tp.jpashop.pt.web.api.dto;

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
    private Long memberName;
    private int orderCnt;

    @Builder.Default
    private List<OrderDto> orders = new ArrayList<>();
}
