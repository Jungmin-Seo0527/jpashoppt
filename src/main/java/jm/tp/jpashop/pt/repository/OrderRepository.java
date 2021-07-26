package jm.tp.jpashop.pt.repository;

import jm.tp.jpashop.pt.exception.NotExitOrderException;
import jm.tp.jpashop.pt.model.Order;
import jm.tp.jpashop.pt.web.api.dto.OrderItemDto;
import jm.tp.jpashop.pt.web.api.dto.OrderItemListResponseDto;
import jm.tp.jpashop.pt.web.api.dto.OrderSimpleInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(em.find(Order.class, id));
    }

    public Optional<Order> findOrderItemsById(Long id) {
        return em.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d " +
                        "join fetch o.orderItems oi " +
                        "join fetch oi.item i " +
                        "where o.id = :id ", Order.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Optional<OrderItemListResponseDto> findOrderItemsByIdForDto(Long id) {
        Optional<OrderItemListResponseDto> orderItemListResponseDto = findOnlyOrderInfoById(id);
        orderItemListResponseDto.orElseThrow(NotExitOrderException::new)
                .setOrderItemList(getOrderItemDtoListByOrderId(id));
        return orderItemListResponseDto;
    }

    private List<OrderItemDto> getOrderItemDtoListByOrderId(Long id) {
        String root = "jm.tp.jpashop.pt.web.api.dto.OrderItemDto";
        List<OrderItemDto> orderItemDtoList = em.createQuery(
                "select new " + root + "(i.id, i.name, oi.orderPrice, oi.count, oi.totalOrderPrice) " +
                        "from Order o " +
                        "join o.orderItems oi " +
                        "join oi.item i " +
                        "where o.id = :id", OrderItemDto.class)
                .setParameter("id", id)
                .getResultList();
        return orderItemDtoList;
    }

    private Optional<OrderItemListResponseDto> findOnlyOrderInfoById(Long id) {
        String root = "jm.tp.jpashop.pt.web.api.dto.OrderItemListResponseDto";
        return em.createQuery(
                "select new " + root + "(o.id, m.name, o.orderDate, o.totalPrice) " +
                        "from Order o " +
                        "join o.member m " +
                        "where o.id = :id", OrderItemListResponseDto.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<OrderSimpleInfoDto> findSimpleWithMemberDelivery() {
        String dto = "jm.tp.jpashop.pt.web.api.dto.OrderSimpleInfoDto";
        return em.createQuery(
                "select new " + dto + "(o.id, m.name, o.orderDate, o.status, d.deliveryStatus, d.address) " +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d", OrderSimpleInfoDto.class
        ).getResultList();
    }
}
