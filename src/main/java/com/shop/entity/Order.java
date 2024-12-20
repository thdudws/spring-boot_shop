package com.shop.entity;

import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태


    //@OneToMany(mappedBy = "order") //연관 관계의 주인 order-item이 주인 -> 그 안에 order를 표시
    //@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>(); //하나의 주문이 여러개의 주문 상품을 가지므로 list 사용

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem); //orderItems 주문 상품 정보 담기
        orderItem.setOrder(this);   //orderItem에 세팅
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member); //주문한 회원 정보 세팅
        for(OrderItem orderItem : orderItemList) { //여러 개 받게 리스트 형태로
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER); //주문 상태 ORDER로 세팅
        order.setOrderDate(LocalDateTime.now()); //현재 시간 주문 시간
        return order;
    }

    public int getTotalPrice() { //총 주문 금액
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    //상태 취소로
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCLE;

        for(OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

}
