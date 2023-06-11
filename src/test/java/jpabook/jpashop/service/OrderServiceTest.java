package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    @Rollback(false)
    public void 상품주문() {
        // Given
        Member member = new Member();
        member.setName("Anakin");
        member.setAddress(new Address("SungNam","JungJa","11223"));
        em.persist((member));

        Book book = new Book();
        book.setName("JPA 기초");
        book.setPrice(20000);
        book.setStockQuantity(10);
        em.persist(book);

        // When
        int orderCount = 4;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // Then
        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("상품주문 시 상태가 같아야 한다.", OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("상품주문 시 수량이 같아야 한다.", 1, getOrder.getOrderItems().size());
        Assert.assertEquals("상품주문 시 가격 * 수량이 같아야 한다.", 20000*orderCount, getOrder.getTotalPrice());
        Assert.assertEquals("상품주문 시 주문수량 만큼 재고가 줄어야 한다.", 6, book.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() {
        // Given
        Member member = createMember();
        Book book = createBook();

        // When
        int orderCount = 11;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // Then
        fail("재고수량 부족 에러가 발생해야 한다.");
    }


    @Test
    public void 상품취소테스트() {
        // Given
        Member member = createMember();
        Book item = createBook();

        int orderCount = 3;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // When
        orderService.cancelOrder(orderId);

        // Then
        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("주문취소 후 상태가 같아야 한다.", OrderStatus.CANCEL, getOrder.getStatus());
        Assert.assertEquals("주문취소 후 재고가 같아야 한다.", 10, item.getStockQuantity());


    }


    // 샘플 상품 생성하기
    private Book createBook() {
        Book book = new Book();
        book.setName("JPA 중급");
        book.setPrice(35000);
        book.setStockQuantity(10);
        em.persist(book);

        return  book;
    }

    // 샘플 회원 생성하기
    private Member createMember() {
        Member member = new Member();
        member.setName("홍길동");
        member.setAddress(new Address("Wonju-City","Jumsil-Gil","26"));
        em.persist(member);

        return member;
    }

}