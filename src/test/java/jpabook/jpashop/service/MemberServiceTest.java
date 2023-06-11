package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional                                       // 기본값이 Rollback,
public class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;                     // Insert 쿼리를 꼭 확인하기 위해서,,,

    @Test
    @Rollback(false)                                 // Rollback을 할 경우 Insert 쿼리가 로그에 남지 않는다.
    public void  회원가입() {
        // Given
        Member member = new Member();
        member.setName("Anakin");
        member.setAddress(new Address("Sungname", "JungJa","12345"));

        // When
        Long saveId = memberService.join(member);

        // Then
        em.flush(); // Insert 쿼리를 꼭 확인하고 싶을 때...
        // 저장한 ID와 조회한 ID가 같으면 정상
        assertEquals(member, memberRepository.findOne(saveId));
    }

    // expected 옵션 사용 시 try ~ catch 문 생략 가능
    @Test(expected = IllegalStateException.class)
    public void  중복회원가입테스트() {
        // Given
        Member member1 = new Member();
        member1.setName("Anakin");

        Member member2 = new Member();
        member2.setName("Anakin");

        // When
        memberService.join(member1);
//        try {
            memberService.join(member2);
//        } catch (IllegalStateException e) {
//            return;
//        }

        // Then
        fail("중복회원명으로 오류가 발생했습니다.");
    }


    @Test
    public void 회원ID조회() {
        // Given
//        Member member = new Member();
//        member.setId(1L);
//
//        // When
//        Long userId = member.getId();
//
//        // Then
//        assertEquals("Anakin", memberRepository.findOne(userId).getName());
    }

    @Test
    public void 회원전체조회() {
    }

    @Test
    public void 회원이름으로조회() {
    }
}