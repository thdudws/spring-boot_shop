package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@ToString
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);    //주문할 상품
        orderItem.setCount(count);  //수량
        orderItem.setOrderPrice(item.getPrice());   //현재 시간 기준으로 상품을 주문 가격으로 세팅

        item.removeStock(count);    //주문 수량만큼 상풍의 재고 수량 감소
        return orderItem;
    }

    public int getTotalPrice() {
        return orderPrice * count; //총 가격
    }

}
