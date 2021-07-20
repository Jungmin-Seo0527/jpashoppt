package jm.tp.jpashop.pt.web.api.controller;

import jm.tp.jpashop.pt.service.MemberService;
import jm.tp.jpashop.pt.web.api.dto.MemberApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/member")
    public MemberApiDto joinMember(@RequestBody MemberApiDto memberApiDto) {
        memberService.join(memberApiDto);
        return memberApiDto;
    }

    @PostMapping("/api/member2")
    public ResponseEntity<MemberApiDto> joinMember2(@RequestBody MemberApiDto memberApiDto) {
        try {
            memberService.join(memberApiDto);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(memberApiDto, BAD_REQUEST);
        }
        return new ResponseEntity<>(memberApiDto, ACCEPTED);
    }
}
