package com.shop.controller;

import com.shop.dto.ItemDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
@RequestMapping("/thymeleaf")
public class ThymeleafExController {

    @GetMapping(value = "/ex01")
    public String ex01(Model model) {
        model.addAttribute("data", "타임리프 예제입니다.");
        return "thymeleafEx/thymeleafEx01";
    }

    @GetMapping(value = "ex03")
    public String ex03(Model model) {
        List<ItemDto> itemDtoList = new ArrayList<>();

        for(int i = 1; i<=10; i++) {
            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명" + i);
            itemDto.setItemNm("테스트 상품" + i);
            itemDto.setPrice(1000*i);
            itemDto.setRegTime(LocalDateTime.now());
            itemDtoList.add(itemDto);
        }
            model.addAttribute("itemDtoList", itemDtoList);
            return "thymeleafEx/thymeleafEx03";

    }

    @GetMapping(value = "/ex04")
    public String ex04(Model model) {
        List<ItemDto> itemDtoList = new ArrayList<>();

        for(int i = 1; i<=10; i++) {
            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명" + i);
            itemDto.setItemNm("테스트 상품" + i);
            itemDto.setPrice(1000*i);
            itemDto.setRegTime(LocalDateTime.now());
            itemDtoList.add(itemDto);
        }
        model.addAttribute("itemDtoList", itemDtoList);
        return "thymeleafEx/thymeleafEx04";
    }

    @GetMapping(value = "/ex07")
    public String ex07(Model model) {
        return "thymeleafEx/thymeleafEx07";
    }
}
