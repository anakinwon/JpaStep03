package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor  // @RequiredArgsConstructor 옵션 사용 시 생성자를 생략할 수 있다.
public class MemberRepository {
    //@PersistenceContext
    //private EntityManager em;
    private final EntityManager em;

    // 스프링부트가 자동적으로 처리해 주기 때문에 생략됨.
    //@PersistenceUnit
    //private EntityManagerFactory emf;

    // 회원저장하기
    public void save(Member member) {
        em.persist(member);
    }

    // 회원ID로 조회하기
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // 전체회원 조회하기
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    // 회원이름으로 조회하기
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
