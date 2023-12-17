package jpabook.jpashop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;


    @Test
    void 회원가입() {

        Member member = new Member();
        member.setName("김지민ㅋ");

        Long joinId = memberService.join(member);

        assertEquals(member, memberRepository.findOne(joinId));
    }

    @Test
    void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);

        // then
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, ()->{
            memberService.join(member2); // 중복 예외 터져야 함
        });
        assertEquals("이미 존재하는 회원입니다.", illegalStateException.getMessage());
    }


}