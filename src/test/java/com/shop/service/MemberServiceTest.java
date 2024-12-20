package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional //test 이후 DB에서 제거
@Log4j2
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Member createMember(){
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .email("admin@admin.com")
                .name("홍길동")
                .address("서울시 마포구 합정동")
                .password("1234")
                .build();
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest(){

        Member member = createMember();

        Member saveMember = memberService.saveMember(member);

        assertEquals(saveMember.getEmail(), member.getEmail());
        assertEquals(saveMember.getName(), member.getName());
        assertEquals(saveMember.getAddress(), member.getAddress());
        assertEquals(saveMember.getPassword(), member.getPassword());
        assertEquals(saveMember.getRole(), member.getRole());
    }

}