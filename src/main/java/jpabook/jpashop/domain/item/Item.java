package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)    // 상속관계 매핑전략에서 부모 클래스에서 생성전략 설정. SINGLE_TABLE/TABLE_PER_CLASS/JOINED
@DiscriminatorColumn(name="DTYPE")                       // 구분코드값 매핑 Album=A, Book=B, Movie=M
public abstract class Item {                             // 구현체를 가져가므로 추상클래스로 만들어야 함??!!

    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;        // 상품이름
    private int price;          // 상품가격
    private int stockQuantity;  // 상품 재고수량

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /**
     *  <비지니스 로직 처리>
     * 도메인 주도 설계 (DDD)
     *   - 엔티티 자체에서 해결할 수 있는 경우
     *   - 엔티티에 비지니스로직을 넣는 것이 좋다.
     *   -   예시 : 재고 수량 처리
     * */

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 취소 수량이 주문 수량보다 클 때 오류발생시킴.
     *
     * */
    public void removeStock(int quantity) {
        int restStock =this.stockQuantity - quantity;
        if (restStock<0) {
            throw new NotEnoughStockException("Not Enough Stocks~!");
        }
        this.stockQuantity = restStock;
    }

}
