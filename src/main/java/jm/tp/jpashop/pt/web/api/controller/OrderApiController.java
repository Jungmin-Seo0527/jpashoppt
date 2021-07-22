package jm.tp.jpashop.pt.web.api.controller;

import jm.tp.jpashop.pt.exception.NotExitOrderException;
import jm.tp.jpashop.pt.service.OrderService;
import jm.tp.jpashop.pt.web.api.dto.ApiResult;
import jm.tp.jpashop.pt.web.api.dto.OrderSimpleInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @ExceptionHandler(NotExitOrderException.class)
    public ResponseEntity<String> notExitOrder(NotExitOrderException e) {
        log.error("없는 주문!!!!");
        return ResponseEntity.badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body(NotExitOrderException.ERROR_MESSAGE);
    }

    /**
     * 주문 전체 조회
     * 단순하게 모든 주문을 조회한다
     * 요청 정보: 사용자 이름, 주문 날짜, 주문 상태, 주소
     */
    @GetMapping("/api/orders")
    public ApiResult<List<OrderSimpleInfoDto>> orders() {
        return ApiResult.succeed(orderService.findOrders().orElseThrow(NotExitOrderException::new));
    }

    @GetMapping("/api/order/{id}")
    public ApiResult<OrderSimpleInfoDto> orders(@PathVariable Long id) {
        return ApiResult.succeed(orderService.findOrder(id));
    }
}
