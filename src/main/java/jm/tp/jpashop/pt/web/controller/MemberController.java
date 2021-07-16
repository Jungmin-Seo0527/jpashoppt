package jm.tp.jpashop.pt.web.controller;

import jm.tp.jpashop.pt.service.MemberService;
import jm.tp.jpashop.pt.web.controller.dto.MemberForm;
import jm.tp.jpashop.pt.web.controller.dto.MemberListForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", MemberForm.builder().build());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) return "members/createMemberForm";

        memberService.join(form.toMemberEntity());
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {

        List<MemberListForm> members = memberService.findAll().stream()
                .map(MemberListForm::create)
                .collect(Collectors.toList());

        model.addAttribute("members", members);
        return "members/memberList";
    }
}
