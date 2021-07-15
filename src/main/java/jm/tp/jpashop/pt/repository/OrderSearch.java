package jm.tp.jpashop.pt.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class OrderSearch {

    private String memberName;
    private String orderStatus;
}
