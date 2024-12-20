package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {  //상품 데이터 정보 전당 클래스

    private Long id;

    @NotBlank(message = "상품명은 필수 입력값 입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력값 입니다.")
    private Integer price;

    @NotBlank(message = "상세설명은 필수 입력값 입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력값 입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    //수정 시 상품 이미지 정보 저장
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    //상품 이미지 index(수정 시 이미지 아이디 담아두는 용)
    private List<Long> itemImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    //ItemFormDto -> Item 로 변환
    public Item createItem() {
        return modelMapper.map(this, Item.class);
    }

    //ItemFormDto -> ItemFormDto 로 변환
    public static ItemFormDto of(Item item) {
        return modelMapper.map(item, ItemFormDto.class);
    }

}
