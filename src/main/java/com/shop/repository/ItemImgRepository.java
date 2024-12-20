package com.shop.repository;

import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//상품 이미지 정보 저장
public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    List<ItemImg> findByItemIdOrderByIdDesc(Long id);

    List<ItemImg> findByItemIdOrderByIdAsc(Long id);

    //itemId 해당하는 대표이미지 반환
    ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);

}
