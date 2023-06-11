package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @JsonIgnore  // 회원 조회 시 주소정보를 노출하고 싶지 않을 때 쓸 수 있는 옵션.
    @Embedded
    private Address address;

    @JsonIgnore               //순환참조 방지
    @OneToMany(mappedBy = "member")  // 나는 연관관계 주인이 아니라, 거울일뿐이다라는 의미로, 여기서는 변경이 일어나지 않는다.
    private List<Order> orders = new ArrayList<>();

}
