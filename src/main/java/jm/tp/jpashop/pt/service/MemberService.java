package jm.tp.jpashop.pt.service;

import jm.tp.jpashop.pt.exception.NotExitMemberException;
import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.model.dto.MemberUpdateInfoDto;
import jm.tp.jpashop.pt.repository.MemberRepository;
import jm.tp.jpashop.pt.web.api.dto.MemberApiDto;
import jm.tp.jpashop.pt.web.api.dto.OrderItemListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final OrderService orderService;

    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    @Transactional
    public Long join(MemberApiDto dto) {
        Member member = dto.toMemberEntity();
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> members = memberRepository.findByName(member.getName());
        if (!members.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Long countMember() {
        return memberRepository.countAll();
    }

    @Transactional
    public MemberApiDto update(Long id, MemberApiDto memberApiDto) {
        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new NotExitMemberException();
        }
        member.updateInfo(MemberUpdateInfoDto.create(memberApiDto));
        return MemberApiDto.create(member);
    }

    public List<OrderItemListResponseDto> findOrdersDetailInfoById(Long id) {
        return orderService.findOrderItemsByMemberId(id);
    }
}
