package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor  // @RequiredArgsConstructor 옵션 사용 시 생성자를 생략할 수 있다.
public class MemberService {

    // 생성자로 사용하되, final 을 사용한다.
    private final MemberRepository memberRepository;

//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원가입
     * */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);    // 저장 시점에서 member.getId() 값을 생성한다.
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 단건 조회
     * */
//    @Transactional(readOnly = true)
    public Member member (Long memberId) {
        return memberRepository.findOne(memberId);

    }

    /**
     * 회원 전체 조회
     * */
//    @Transactional(readOnly = true)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /**
     * 회원 이름으로 조회
     * */
//    @Transactional(readOnly = true)
    public List<Member> findMyName(String userName) {
        return memberRepository.findByName(userName);
    }


    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }


    // 변경감지하고 업데이트 한다.
    @Transactional
    public void update(Long id, String name) {
        Member member =memberRepository.findOne(id);
        member.setName(name);
    }



}
