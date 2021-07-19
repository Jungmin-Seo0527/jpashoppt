package jm.tp.jpashop.pt.web.controller;

import jm.tp.jpashop.pt.service.ItemService;
import jm.tp.jpashop.pt.service.MemberService;
import jm.tp.jpashop.pt.service.OrderService;
import jm.tp.jpashop.pt.web.controller.dto.BookForm;
import jm.tp.jpashop.pt.web.controller.dto.MemberListForm;
import jm.tp.jpashop.pt.web.controller.dto.OrderListResponseForm;
import jm.tp.jpashop.pt.web.controller.dto.OrderRequestDto;
import jm.tp.jpashop.pt.web.controller.dto.OrderSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final MemberService memberService;
    private final ItemService itemService;
    private final OrderService orderService;

    @GetMapping("/order")
    public String createForm(Model model) {
        List<MemberListForm> memberList = memberService.findAll().stream()
                .map(MemberListForm::create)
                .collect(toList());
        List<BookForm> itemList = itemService.findItems().stream()
                .map(BookForm::create)
                .collect(toList());

        model.addAttribute("members", memberList);
        model.addAttribute("items", itemList);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(OrderRequestDto dto) {

        orderService.order(dto.getMemberId(), dto.getItemId(), dto.getCount());

        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearchDto") OrderSearchDto dto, Model model) {
        List<OrderListResponseForm> orders = orderService.findOrders(dto);
        // List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
        return "order/orderList";
    }

    @PostMapping("orders{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId) {

        return "redirect:/";
    }
}
