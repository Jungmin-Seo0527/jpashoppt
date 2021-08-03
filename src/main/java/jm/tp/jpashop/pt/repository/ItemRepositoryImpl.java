package jm.tp.jpashop.pt.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jm.tp.jpashop.pt.web.api.dto.BuyersDto;
import jm.tp.jpashop.pt.web.api.dto.ItemBuyersDto;
import jm.tp.jpashop.pt.web.api.dto.OrderDto;
import jm.tp.jpashop.pt.web.api.dto.QBuyersDto;
import jm.tp.jpashop.pt.web.api.dto.QItemBuyersDto;
import jm.tp.jpashop.pt.web.api.dto.QOrderDto;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jm.tp.jpashop.pt.model.QMember.member;
import static jm.tp.jpashop.pt.model.QOrder.order;
import static jm.tp.jpashop.pt.model.QOrderItem.orderItem;
import static jm.tp.jpashop.pt.model.item.QItem.item;

public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ItemBuyersDto findItemBuyersById(Long id) {
        ItemBuyersDto itemBuyersDto = queryFactory
                .select(new QItemBuyersDto(
                        item.id,
                        item.name
                ))
                .from(item)
                .where(item.id.eq(id))
                .fetchFirst();


        Map<Long, List<OrderDto>> map = getFetch(id);
        List<Long> memberIds = new ArrayList<>(map.keySet());

        List<BuyersDto> buyers = queryFactory
                .select(new QBuyersDto(
                        member.id,
                        member.name
                ))
                .from(member)
                .where(member.id.in(memberIds))
                .fetch();
        for (BuyersDto buyer : buyers) {
            buyer.setOrders(map.get(buyer.getMemberId()));
        }
        itemBuyersDto.setBuyers(buyers);

        return itemBuyersDto;
    }

    public Map<Long, List<OrderDto>> getFetch(Long id) {
        return queryFactory
                .select(new QOrderDto(
                        order.id,
                        member.id,
                        orderItem.orderPrice,
                        orderItem.count,
                        order.orderDate
                ))
                .from(orderItem)
                .join(orderItem.order, order)
                .join(orderItem.item, item)
                .join(order.member, member)
                .where(orderItem.item.id.eq(id))
                .fetch().stream()
                .collect(Collectors.groupingBy(OrderDto::getMemberId));
    }

    public List<Long> findOrderItemIdById(Long id) {
        return queryFactory
                .select(orderItem.id)
                .from(orderItem)
                .join(orderItem.item, item)
                .where(item.id.eq(id))
                .fetch();
    }
}
