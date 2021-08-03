package jm.tp.jpashop.pt.web.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Data @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class ItemBuyersDto {
    private Long itemId;
    private String itemName;

    @Builder.Default
    private List<BuyersDto> buyers = new ArrayList<>();

    private int buyersCnt;

    @QueryProjection
    public ItemBuyersDto(Long itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public void setBuyers(List<BuyersDto> buyers) {
        this.buyers = buyers;
        buyersCnt = buyers.size();
    }
}
