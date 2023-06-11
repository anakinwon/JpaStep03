package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity @Getter @Setter
@DiscriminatorValue("B")            // SINGLE_TABLE 일 경우 구분값.
public class Book extends Item {

    private String author;
    private String isbn;

}
