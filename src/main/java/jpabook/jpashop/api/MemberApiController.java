package jpabook.jpashop.api;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

 /*  PostMan 요청 테스트 데이터
{
    "name"    : "API-anakin1"
  , "address" : { "city"   : "Wonju"
                , "street" : "Jumsil"
                , "zipcode": "26"
                }
}
 */

    /**
     * API 생성 Tips.
     *    1. 엔티티를 파라미터로 만들지 마라.
     *    2. 엔티티를 직접 외부에 노출하지 마라.
     *    3. API Spec에 맞는 DTO를 만들어서 노출해라.
     * */

    /**
     *   조회 API 테스트
     *   확장이 어렵기 때문에 비추천함.
     * */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    /**
     * 조회 결과로 노출할 것만 전달해 주는 보안에 좋은 방식임.
     * */
    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m-> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }



    /**
     *   등록 API 테스트
     * */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    @Data
    static class CreateMemberRequest {
        @NotEmpty(message = "회원 이름은 필수 입니다")
        private String name;
    }


    /**
     *   수정 API 테스트
     * */
    @PutMapping("/api/v3/members/{id}")
    public UpdateMemberResponse updateMemberV1(
            @PathVariable("id") Long id ,
            @RequestBody @Valid UpdateMemberRequest request) {

        // 수정시에는 무조건 변경 감지를 사용한다.
        memberService.update(id, request.getName());
        Member findeMember = memberService.findOne(id);
        return new UpdateMemberResponse(findeMember.getId(), findeMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        @NotEmpty(message = "회원 이름은 필수 입니다")
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }


}
