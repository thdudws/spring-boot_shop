package com.shop.repository;

import com.shop.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/*JpaRepository 기본적인 CRUD(생성, 읽기, 수정, 삭제) 작업을 자동으로 제공
복잡한 쿼리를 직접 작성하지 않고도 엔티티를 쉽게 처리*/
public interface PersonRepository extends JpaRepository<Person, String> {

}
