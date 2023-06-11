package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity @Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @JsonIgnore               //순환참조 방지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @JsonIgnore               //순환참조 방지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice;  // 주문당시 가격
    private int count;       // 주문 수량


    // 무분별하게 생성하는 것을 막기 위해서 사용
    // @NoArgsConstructor(access = AccessLevel.PROTECTED) 어노테이션으로 대체 가능.
    //    protected OrderItem() {
    // }

    /**
     * 주문상품   -  생성 메소드
     * */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // 주문한 수량 만큼 재고수량을 빼준다.
        item.removeStock(count);
        return orderItem;
    }


    /** *
     *  취소 시 주문수량 만큼 재고를 원복한다.
     * */
    public void cancel() {
        getItem().addStock(count);
    }

    // 주문 전체 금액 = 상품 가격 * 주문 수량
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

}
