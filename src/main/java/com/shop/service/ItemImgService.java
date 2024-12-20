package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
//상품 이미지 업로드 상품 이미지 정보 저장
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private  final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes());

            imgUrl = "/images/item/" + imgName; //images/item/1422c025-3386-4c93-aa00-e16905996ed3.jpg

            //저장한 만큼만 이미지 저장
            //수정시 추가가 이미지 불가능
            //itemImg.updateItemImg(oriImgName, imgName, imgUrl);
            //itemImgRepository.save(itemImg);
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }
    //end saveItemImg

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {

        //itemImgFile null이 아니면 실행
        //기존 이미지는 지우고 새 이미지 저장
        if(!itemImgFile.isEmpty()){ //상풍이미지 수정한 경우 상품이미지 업데이트
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId) //상품이미지 아이디를 이용하여 기존의 상품이미지 엔티티 조회
                    .orElseThrow(()-> new EntityNotFoundException());

            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;

            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }
    //end updateItemImg

}
