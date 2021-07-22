package jm.tp.jpashop.pt.web.api.dto;

import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.model.DeliveryStatus;
import jm.tp.jpashop.pt.model.Order;
import jm.tp.jpashop.pt.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * 주문의 단순한 상태를 조회하기 위한 dto
 * 무엇을 주문했는지 보다는 주문자, 주문 시간, 배달 상태, 주문 시간을 확인하기 위함
 */
@Data
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class OrderSimpleInfoDto {

    private Long orderId;
    private String memberName;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private DeliveryStatus deliveryStatus;
    private Address address;

    public OrderSimpleInfoDto(Order order) {
        setOrderId(order.getId());
        setMemberName(order.getMember().getName());
        setOrderStatus(order.getStatus());
        setOrderDate(order.getOrderDate());
        setDeliveryStatus(order.getDelivery().getDeliveryStatus());
        setAddress(order.getDelivery().getAddress());
    }
}
