package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemFileList) throws Exception {

        //상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item); //상품 데이터 저장

        //이미지 등록
        for(int i = 0; i < itemFileList.size(); i++){
            ItemImg itemImg = new ItemImg();

            itemImg.setItem(item);
            if(i==0){
                itemImg.setRepimgYn("Y"); //대표이미지
            }else {
                itemImg.setRepimgYn("N"); //나머지 N으로 저장
            }
            //상품 이미지 정보 저장
            itemImgService.saveItemImg(itemImg, itemFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true) //더티채킹(변경감지(스크린샷))를 수행하지 않아 성능 향상
    public ItemFormDto getItemDtl(Long itemId) {
        //상풍 이미지 조회 등로순으로
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId); //오름차순으로

        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for(ItemImg itemImg : itemImgList){ //itemImg을 itemImgList로 만들어 리스트에 추가
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        //상품 아이디를 통해 상품 엔티티 조회 미존재 시 EntityNotFoundException 발생
        Item item = itemRepository.findById(itemId).orElseThrow(()->new EntityNotFoundException("Item not found"));

        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;

    }
    //end getItemDtl

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        //상품 등록 화면으로 전달받은 상품아이디로 상품 엔티티 조회(상품 수정)
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(()->new EntityNotFoundException());
        item.updateItem(itemFormDto); //itemFormDto 통해 상품 엔티티 업데이트

        //상품이미지 아이디 리스트 조회
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        //이미지 등록
        //updateItemImg 메소드에 상풍이미지 아이디와 상품이미지 파일정보를 파라미터로 전당
        for(int i = 0; i < itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPAge(itemSearchDto, pageable);
    }


}
