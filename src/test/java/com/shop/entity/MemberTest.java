package com.shop.entity;

import com.shop.repository.MemberRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Log4j2
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("Auditing 테스트")
    @WithMockUser(username = "gildong", roles = "USER") //임시로 권한 부여
    @Commit
    public void test(){
        Member member = new Member();
        memberRepository.save(member);

        em.flush();
        em.flush();

        Member user = memberRepository.findById(member.getId()).get();

        log.info(user.getRegTime());
        log.info(user.getUpdateTime());
        log.info(user.getCreatedBy());
        log.info(user.getModifiedBy());

    }

}
