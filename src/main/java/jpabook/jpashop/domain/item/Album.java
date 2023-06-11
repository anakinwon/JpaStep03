package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity @Getter @Setter
@DiscriminatorValue("A")            // SINGLE_TABLE 일 경우 구분값.
public class Album extends Item {

    private String artist;
    private String etc;

}
