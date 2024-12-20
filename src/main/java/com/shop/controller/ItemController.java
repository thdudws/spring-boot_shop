package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@Log4j2
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String newItem(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
        //유효성 체크, 입력창
        if(bindingResult.hasErrors()) {
            return "/item/itemForm";
        }

        //첫번째 상품 이미지 필수 체크
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력값입니다.");
            return "/item/itemForm";
        }

        try {
            //상품 저장 로직 호출, 매개변수로 itemImgFileList를 넘겨줌
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }

        return "redirect:/"; //상품 정상 등록되면 메인페이지로 이동
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        }catch (EntityNotFoundException e ){
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "/item/itemForm";
        }

        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {
        if(bindingResult.hasErrors()) {
            return "/item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품이미지는 필수 입력 값 입니다.");
            return "/item/itemForm";
        }

        try{
            itemService.updateItem(itemFormDto, itemImgFileList); //상품 수정 로직 호출
        }catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
            return "/item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = {"/admin/items/", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto,
                             @PathVariable("page") Optional<Integer> page, Model model){

        log.info("------------itemManage-------------");

        //0 -> 조회할 페이지 번호 3 -> 한번에 가지고 올 데이터 수 / 페이지번호 없으면 0페이지 조회
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        //조회 조건과 페이징 정보를 파라미터로 넘겨서 Page(Item) 객체를 반환받는다
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        //조회한 데이터, 정보 뷰에 전달
        model.addAttribute("items", items);
        //페이지전환 시 기존 검색 조건 유지한 채 이돌할 수 있도록 뷰에 전달
        model.addAttribute("itemSearchDto", itemSearchDto);
        //페이지번호 최대 개수
        model.addAttribute("maxPage", 5);

        return "item/itemMng";

    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }


}
