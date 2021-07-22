package jm.tp.jpashop.pt.service;

import jm.tp.jpashop.pt.exception.NotExitItem;
import jm.tp.jpashop.pt.exception.NotExitOrderException;
import jm.tp.jpashop.pt.model.Delivery;
import jm.tp.jpashop.pt.model.DeliveryStatus;
import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.model.Order;
import jm.tp.jpashop.pt.model.OrderItem;
import jm.tp.jpashop.pt.model.item.Item;
import jm.tp.jpashop.pt.repository.ItemRepository;
import jm.tp.jpashop.pt.repository.MemberRepository;
import jm.tp.jpashop.pt.repository.OrderRepository;
import jm.tp.jpashop.pt.repository.OrderSearch;
import jm.tp.jpashop.pt.web.api.dto.OrderSimpleInfoDto;
import jm.tp.jpashop.pt.web.controller.dto.OrderListResponseForm;
import jm.tp.jpashop.pt.web.controller.dto.OrderSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findById(memberId);

        Delivery delivery = Delivery.builder()
                .address(member.getAddress())
                .deliveryStatus(DeliveryStatus.READY)
                .build();

        Item item = itemRepository.findById(itemId).orElseThrow(NotExitItem::new);

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        Order order = Order.createOrder(member, delivery, orderItem);
        orderRepository.save(order);

        return order.getId();
    }

    @Transactional
    public void cancel(Long orderId) {
        orderRepository.findById(orderId).orElseThrow(NotExitOrderException::new).cancel();
    }

    public OrderSimpleInfoDto findOrder(Long id) {
        return new OrderSimpleInfoDto(orderRepository.findById(id).orElseThrow(NotExitOrderException::new));
    }

    public Optional<List<OrderSimpleInfoDto>> findOrders() {
        List<OrderSimpleInfoDto> orders = orderRepository.findAllByString(OrderSearch.builder().build()).stream()
                .map(OrderSimpleInfoDto::new)
                .collect(Collectors.toList());
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders);
    }

    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }

    public List<OrderListResponseForm> findOrders(OrderSearchDto dto) {
        return orderRepository.findAllByString(dto.toOrderSearch())
                .stream()
                .map(OrderListResponseForm::create)
                .collect(toList());
    }
}
