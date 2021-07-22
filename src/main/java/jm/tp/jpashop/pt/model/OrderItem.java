package jm.tp.jpashop.pt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jm.tp.jpashop.pt.model.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter @Setter(PRIVATE) @Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;
    private int totalOrderPrice;

    public static OrderItem createOrderItem(Item item, int price, int count) {
        item.removeStock(count);
        return OrderItem.builder()
                .item(item)
                .orderPrice(price)
                .count(count)
                .totalOrderPrice(price * count)
                .build();
    }

    // business logic

    /**
     * order 객체에 orderItem 넣기(장바구니 기능)
     * orderItem 에서 어떤 아이템을 몇개를 살 것인가를 정하면
     * order객체가 orderItem 리스트를 가짐으로써 장바구니 역할을 한다.
     * 최종 주문에서 order객체가 일괄적으로 모든 orderItem을 구매한다.
     */
    public void putBucket(Order order) {
        setOrder(order);
    }

    /**
     * 상품 주문 취소 -> 상품 수량 원상 복귀
     */
    public void cancel() {
        getItem().addStock(count);
    }

    // inquiry logic

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

}
