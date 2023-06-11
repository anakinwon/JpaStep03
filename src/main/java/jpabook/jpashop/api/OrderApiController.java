package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /** 
     * 주문 조회 V1: 엔티티 직접 노출
     * 
     * */
    
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            // 일:다 관계로 이 부분이 추가됨. 프록시를 강제 초기화하기.
            // OrderItem과 Item 모두 컬렉션 초기화하여 사용.
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());
        }

        return all;
    }


    /**
     * 주문 조회 V2: 엔티티를 DTO로 변환
     *             엔티티를 외부에 노출하면 안된다.
     * */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;

    }

    // 아래와 같은 에러 발생 시..  @Data 혹은  @Getter 어노테이션 사용해서 해결함.
    // "status": 500,
    // "error": "Internal Server Error",
//    @Data
    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // DTO 내부에 엔티티가 존재하면 보안에 취약해 짐.
        // 엔티티와의 의존관계를 완전히 끊어줘야 함.
        //private List<OrderItem> orderItems;
        // 엔티티를 노출하지 않기 위해서 DTO를 추가 생성함.
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            // "orderItems": null 일 경우, 아이템 목록을 가져오기 위해서 초기화를 한다.
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            orderItems = order.getOrderItems();
            // 엔티티를 노출하지 않기 위해서 DTO를 추가 생성함.
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    // 아래와 같은 에러 발생 시..  @Data 혹은  @Getter 어노테이션 사용해서 해결함.
    // "status": 500,
    // "error": "Internal Server Error",
//    @Data
    @Getter
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }



    /**
     * 주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화
     *              성능은 보장할 수 있지만,
     *              컬렉션을 패치조인하면 페이징쿼리가 안된다는 단점이 존재 함.
     *              일대다는 패치조인으로 쓰면 위험하다.
     * */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }


    /**
     * 주문 조회 V3.1: 페이징 하면서 컬렉션을 패치조인하려면
     *              페이징 옵션 사용. offset / limit
     *              application.yml 에 기본값 설정하기 => default_batch_fetch_size: 10
     *
     *              xToOne 관계는 페치조인을 권장한다.
     *              컬렉션은 지연로딩으로 조회해야 한다.
     *              지연로딩 성능 최적화를 위해서 default_batch_fetch_size 옵션을 사용한다.
     *              쿼리 호출 수가 1 + N 에서   1 + 1 로 최적화 된다.
     * */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page( @RequestParam(value="offset", defaultValue="0") int offset
                                       , @RequestParam(value="limit", defaultValue="10") int limit
                                       ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }


    /**
     * 주문 조회 V4: JPA에서 DTO 직접 조회
     *
     * */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }


    /**
     * 주문 조회 V5: JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화
     *
     *
     * */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }


    /**
     * 주문 조회 V6: JPA에서 DTO로 직접 조회, 플랫 데이터 최적화
     *             상황에 따라서 V5보다 느릴 수 있다.
     *             애플리케이션에서 추가 작업이 많은 단점.
     *             페이지 처리가 안된다.
     * */
    @GetMapping("/api/v6/orders")
    //public List<OrderFlatDto> orderV6() {
    //    return orderQueryRepository.findAllByDto_flat();
    public List<OrderQueryDto> orderV6() {

        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());

    }

}
