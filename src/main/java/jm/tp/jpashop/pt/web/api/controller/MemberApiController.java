package jm.tp.jpashop.pt.web.api.controller;

import jm.tp.jpashop.pt.service.MemberService;
import jm.tp.jpashop.pt.web.api.dto.ApiResult;
import jm.tp.jpashop.pt.web.api.dto.MemberApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.status(BAD_REQUEST).body(memberApiDto);
        }
        return ResponseEntity.ok(memberApiDto);
    }

    @PostMapping("/api/member3")
    public ApiResult<MemberApiDto> joinMember3(@RequestBody MemberApiDto memberApiDto) {
        try {
            memberService.join(memberApiDto);
        } catch (IllegalStateException e) {
            return ApiResult.failed(memberApiDto, "중복하는 회원이 존재합니다.");
        }
        return ApiResult.succeed(memberApiDto);
    }

    @PostMapping("/api/member4")
    public ResponseEntity<ApiResult<MemberApiDto>> joinMember4(@RequestBody MemberApiDto memberApiDto) {
        try {
            memberService.join(memberApiDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(ApiResult.failed(memberApiDto, "이름이 중복되는 회원이 존재합니다."));
        }
        return ResponseEntity.ok(ApiResult.succeed(memberApiDto));
    }
}
