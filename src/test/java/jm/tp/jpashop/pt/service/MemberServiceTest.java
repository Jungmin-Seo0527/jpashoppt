package jm.tp.jpashop.pt.service;

import jm.tp.jpashop.pt.model.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;

    @Test
    @DisplayName("회원가입")
    public void join() throws Exception {
        // given
        Member member = Member.builder()
                .name("서정민")
                .build();

        // when
        Long joinedMemberId = memberService.join(member);

        // then
        Assertions.assertThat(member).isSameAs(memberService.findOne(joinedMemberId));
    }

    @Test
    @DisplayName("중복 회원 가입")
    public void duplicateMemberJoin() throws Exception {
        // given
        Member member1 = Member.builder().name("서정민").build();
        Member member2 = Member.builder().name("서정민").build();

        // when
        memberService.join(member1);

        // then
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }
}