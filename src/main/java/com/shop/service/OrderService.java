package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId()) //주문할 상품 조회
                .orElseThrow(()-> new EntityNotFoundException("Item not found"));
        Member member = memberRepository.findByEmail(email); //이메일로 회원 조회

        List<OrderItem> orederItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount()); //주문 상품 주문 수량으로 주문 상품 엔티티 생성
        orederItemList.add(orderItem);

        //회원 정보와 주문할 리스트 정보로 주문 엔티티 생성
        Order order = Order.createOrder(member, orederItemList);
        orderRepository.save(order); //생성한 주문 엔티티 저장(DB order 테이블에 저장)

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        //페이징 조건 이용 주문 먹럭 조회
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totoalCount = orderRepository.countOrders(email); //유저의 주문 총 개수

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for(Order order : orders) { //리스트 순회 구매 이력 페이지에 전달 dto 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for(OrderItem orderItem : orderItems) {
                //대표 이미지 조회
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }
        //페이지 구현 객체 생성 반환
        return new PageImpl<>(orderHistDtos, pageable, totoalCount);
    }

    @Transactional(readOnly = true)
    //주문 취소 시 본인이 맞는지 검증
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new EntityNotFoundException());
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }

        return true;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new EntityNotFoundException());
        order.cancelOrder(); //주문 취소 상태 변경시 트랜잭션 끝날 때 update 쿼리 실행
    }

}
