package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.Map;

@Entity
@Table(name = "item")   //엔티티와 매핑할 테이블 지정
@Getter
@Setter
@ToString
public class Item extends BaseEntity {
    @Id
    @Column(name = "item_id") //기본키
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemNm; //싱품명

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    /*private static ModelMapper modelMapper = new ModelMapper();
    //itemImg 받아서 ItemFormDto로 변환
    public static void of(ItemFormDto itemFormDto) {
        modelMapper.map(itemFormDto, Item.class);
    }*/

    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber; //상품의 재고 수량에서 주문 후 남은 재고 수량 구하기

        if(restStock < 0) {  //상품 재고가 주문 수량보다 적을 경우 재고 부족 예외 발생
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량 : " + this.stockNumber +")");
        }
        this.stockNumber = restStock; //주문 후 남은 재고 수량 현재 재고 값으로 할당
    }

}
