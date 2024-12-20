package com.shop.exception;

//주문 수량만큼 재고 감소, 주문 수량보다 재고 수량이 적을 떄 처리
public class OutOfStockException extends RuntimeException{
    public OutOfStockException(String message){
        super(message);
    }
}
