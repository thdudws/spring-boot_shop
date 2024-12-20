package com.shop.dto;

import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto { //상품 저장 후 상품 이미지에 대한 데이터 전달할 클래스

    private Long id;

    private String imgName; //이미지 파일명

    private String oriImgName; //원본 이미지 파일명

    private String imgUrl;  //이미지 조회 경로

    private String repimgYn; //대표이미지

    //멤버 변수
    private static ModelMapper modelMapper = new ModelMapper();

    //ItemImg 받아서 자료형과 이름이 같을 때 ItemImgDto로 변환
    //static으로 선언해 객체 생성 안해도 호출가능
    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg, ItemImgDto.class);
    }

}
