package jm.tp.jpashop.pt.web.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Data @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class OrderDto {

    private int orderPrice;
    private int orderItemCnt;
    private LocalDateTime orderDate;
}
