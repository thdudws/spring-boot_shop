package com.shop.repository;

import com.shop.entity.Person;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void insertPerson() {
        Person person = Person.builder()
                .userName("test")
                .age(27)
                .phone("010-2222-4444")
                .address("Seoul, South Korea")
                .regDate(LocalDateTime.now())
                .build();

        personRepository.save(person);
    }

}