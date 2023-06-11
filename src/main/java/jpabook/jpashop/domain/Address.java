package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    // JPA 는 기본생성자를 반드시 만들어야 함.
    // protected 로 생성자를 만들어 줌으로써 남용을 방지한다.
    protected Address() {
    }

    // 생성할 때만 값이 제공되는 것이 좋음.
    // Setter는 제공하지 않는 것을 추천함.
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
