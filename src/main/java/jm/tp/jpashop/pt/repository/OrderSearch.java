package jm.tp.jpashop.pt.repository;

import jm.tp.jpashop.pt.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data @Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class OrderSearch {

    private String memberName;
    private OrderStatus orderStatus;
}
