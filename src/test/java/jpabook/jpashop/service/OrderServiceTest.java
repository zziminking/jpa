package jpabook.jpashop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void 상품주문() {
        // given
        Member member = createMember();
        Item item = createBook("시골JPA", 15000, 20);
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getOrderStatus(), "주문 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItemList().size(), "주문한 상품 종류 수량 일치여부");
        assertEquals(15000*2, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량");
        assertEquals(18, item.getStockQuantity(), "주문 수량만큼 재고가 줄어야한다.");
    }



    @Test
    void 주문취소() {
        // given
        Member member = createMember();
        Book item = createBook("시골JPA", 10000, 10);

        int orderCount = 3;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getOrderStatus(), "주문 취소 시 상태는 CANCEL");
        assertEquals(10, item.getStockQuantity(), "주문 취소 시 재고 원복");
    }

    @Test
    void 상품주문_재고수량초과() {
        // given
        Member member = createMember();
        Item item = createBook("시골JPA", 12000, 10);

        int orderCount = 123;

        // when
        NotEnoughStockException notEnoughStockException = assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        });

        // then
//        fail("재고 수량 예외가 발생해야 한다.");
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("김지민1");
        member.setAdderss(new Address("서울","강남","123456"));
        em.persist(member);

        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }

}