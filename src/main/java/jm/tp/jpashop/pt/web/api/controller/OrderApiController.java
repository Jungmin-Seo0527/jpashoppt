package jm.tp.jpashop.pt.web.api.controller;

import jm.tp.jpashop.pt.exception.NotExitOrderException;
import jm.tp.jpashop.pt.service.OrderService;
import jm.tp.jpashop.pt.web.api.dto.ApiResult;
import jm.tp.jpashop.pt.web.api.dto.OrderItemListResponseDto;
import jm.tp.jpashop.pt.web.api.dto.OrderSimpleInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.TEXT_PLAIN;

/**
 * 주문 조회(주문 id 로 주문 조회 - 주문의 현재 상태만 조회)
 * 주문 조회 (사용자 id로 주문 조회 - 완료와 진행 주문)
 * 주문 수정(사용자 id로 조회후 주문 내역을 받아서 수정 - 단 배달이 완료되지 않는 주문만 가능)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    /**
     * 존재하지 않는 주문에 대한 조회 예외 처리
     */
    @ExceptionHandler(NotExitOrderException.class)
    public ResponseEntity<String> notExitOrder(NotExitOrderException e) {
        log.error("없는 주문!!!!");
        return ResponseEntity.badRequest()
                .contentType(TEXT_PLAIN)
                .body(NotExitOrderException.ERROR_MESSAGE);
    }

    /**
     * 주문 전체 조회
     * 단순하게 모든 주문을 조회한다
     * 요청 정보: 사용자 이름, 주문 날짜, 주문 상태, 주소
     * 쿼리문 3개 (order 조회, member 조회, delivery 조회)
     * default_batch_fetch_size: 100 적용으로 member와 delivery 조회에서는 where in 절 적용하여 1 + N 문제 회피
     */
    @GetMapping("/api/orders")
    public ApiResult<List<OrderSimpleInfoDto>> orders() {
        return ApiResult.succeed(orderService.findOrders().orElseThrow(NotExitOrderException::new));
    }

    /**
     * 페치 조인을 적용하여 쿼리문 한번으로 모두 조회
     */
    @GetMapping("/api/orders2")
    public ApiResult<List<OrderSimpleInfoDto>> orders2() {
        return ApiResult.succeed(orderService.findOrders2().orElseThrow(NotExitOrderException::new));
    }

    /**
     * Repository에서 바로 DTO로 조회하는 JPQL을 작성하여 조회
     * 각 테이블에서 원하는 컬럼만 조회할 수 있다.
     */
    @GetMapping("/api/orders3")
    public ApiResult<List<OrderSimpleInfoDto>> orders3() {
        return ApiResult.succeed(orderService.findOrders3().orElseThrow(NotExitOrderException::new));
    }

    @GetMapping("/api/order/{id}")
    public ApiResult<OrderSimpleInfoDto> orders(@PathVariable Long id) {
        return ApiResult.succeed(orderService.findOrder(id));
    }

    /**
     * Order객체로 조회후에 OrderItemListResponseDto로 변환
     * N + 1 문제는 batch size 설정으로 N에 대한 쿼리문을 where in 절로 하나로 만듦(orderItem 엔티티 -> item)
     * 쿼리문: 4개
     */
    @GetMapping("/api/orderItems/{id}")
    public ApiResult<OrderItemListResponseDto> findOrderList(@PathVariable Long id) {
        return ApiResult.succeed(OrderItemListResponseDto.create(orderService.findOrderItemList(id).orElseThrow(NotExitOrderException::new)));
    }

    /**
     * 페치조인으로 위의 메서드의 쿼리문 4개를 1개로 만듦
     */
    @GetMapping("/api/orderItems2/{id}")
    public ApiResult<OrderItemListResponseDto> findOrderList2(@PathVariable Long id) {
        return ApiResult.succeed(OrderItemListResponseDto.create(orderService.findOrderItemList2(id).orElseThrow(NotExitOrderException::new)));
    }

    /**
     * Repository계층에서 join 을 이용해 DTO로 바로 조회하기
     * 쿼리문 2개 - OrderItemListResponseDto에서 OrderItemDto를 리스트로 가지고 있다.
     * 일대다 관계의 엔티티인 OrderItem을 DTO로 따로 조회해서 OrderItemListResponseDto의 OrderItemDtoList에 setting 하는 추가 과정이 필요
     * 4개의 테이블에서 데이터 조회 (Member, Order, OrderItem, Item)
     * 쿼리문 2개
     */
    @GetMapping("/api/orderItems3/{id}")
    public ApiResult<OrderItemListResponseDto> findOrderList3(@PathVariable Long id) {
        return ApiResult.succeed(orderService.findOrderItemList3(id).orElseThrow(NotExitOrderException::new));
    }
}
