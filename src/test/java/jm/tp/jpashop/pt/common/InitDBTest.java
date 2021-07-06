package jm.tp.jpashop.pt.common;

import jm.tp.jpashop.pt.model.Member;
import jm.tp.jpashop.pt.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class InitDBTest {

    @Autowired MemberService memberService;

    @Test
    @DisplayName("PostConstruct 맴버 확인")
    public void checkMembers() throws Exception {
        // given

        // when
        List<Member> all = memberService.findAll();

        // then
        assertThat(all.size()).isEqualTo(2);
    }
}