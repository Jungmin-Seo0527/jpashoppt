package jm.tp.jpashop.pt.web.api.controller;

import jm.tp.jpashop.pt.exception.NotExitMemberException;
import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.service.MemberService;
import jm.tp.jpashop.pt.web.api.dto.ApiResult;
import jm.tp.jpashop.pt.web.api.dto.MemberApiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

    @ExceptionHandler(value = NotExitMemberException.class)
    public ResponseEntity<String> handleDemoException(NotExitMemberException e) {
        log.error("데모용 에러 발생!!!");
        return ResponseEntity
                .badRequest()
                .contentType(TEXT_PLAIN)
                .body(NotExitMemberException.ERROR_MESSAGE);
    }

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

    @GetMapping("/api/members")
    public ApiResult<List<MemberApiDto>> memberList() {
        return ApiResult.succeed(memberService.findAll().stream()
                .map(MemberApiDto::create)
                .collect(toList()));
    }

    @GetMapping("/api/member/{id}")
    public ResponseEntity<ApiResult<MemberApiDto>> updateMemberInfo(@PathVariable Long id) {
        Member member = memberService.findOne(id);
        if (member == null) {
            throw new NotExitMemberException();
        }
        return ResponseEntity.ok(ApiResult.succeed(MemberApiDto.create(member)));
    }

    @PostMapping("/api/member/{id}")
    public ResponseEntity<ApiResult<MemberApiDto>> updateMemberInfo(@PathVariable Long id,
                                                                    @RequestBody MemberApiDto memberApiDto) {
        MemberApiDto member = memberService.update(id, memberApiDto);
        return ResponseEntity.ok(ApiResult.succeed(member));
    }
}
