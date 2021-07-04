package jm.tp.jpashop.pt.service;

import jm.tp.jpashop.pt.model.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;

    @Test
    @DisplayName("회원가입")
    public void join() throws Exception {
        // given
        Member member = new Member();
        member.setName("서정민");

        // when
        Long joinedMemberId = memberService.join(member);

        // then
        Assertions.assertThat(member).isSameAs(memberService.findOne(joinedMemberId));
    }

    @Test
    @DisplayName("중복 회원 가입")
    public void duplicateMemberJoin() throws Exception {
        // given
        Member member1 = new Member();
        Member member2 = new Member();
        member1.setName("서정민");
        member2.setName("서정민");

        // when
        memberService.join(member1);

        // then
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }
}