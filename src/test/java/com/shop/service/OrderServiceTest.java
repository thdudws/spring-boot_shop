package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.OrderDto;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Log4j2
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    public Item saveItem(){ //주문 상품 저장
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember(){ //회원 정보 저장
        Member member = new Member();
        member.setEmail("test1@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("주문 테스트")
    public void order(){
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10); //주문 수량
        orderDto.setItemId(item.getId()); //주문 상품 객체에 저장

        Long orderId = orderService.order(orderDto, member.getEmail()); //주문 번호 orderId에 저장

        Order order = orderRepository.findById(orderId) //주문 정보 조회
                .orElseThrow(()-> new EntityNotFoundException());

        List<OrderItem> orderItems = order.getOrderItems();

        orderItems.forEach(orderItem -> log.info("-------------- = " + orderItem));

        int totalPrice = orderDto.getCount() * item.getPrice(); //총 가격

        assertEquals(totalPrice, order.getTotalPrice()); //총 가격과 DB 상품 가격 비교 같으면 종료

    }

}