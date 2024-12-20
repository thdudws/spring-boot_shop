package com.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity //클래스를 엔티티로 선언
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    @Id //기본키에 사용할 속성 지정
    @GeneratedValue(strategy = GenerationType.UUID) //기본키 생성 전략(기본키 생성 방법)
    private String userId;

    @Column(nullable = false, length = 30) //테이블에 매핑될 컬럼명
    private String userName;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @CreatedDate    //엔티티 생성 시 시간 자동 저장
    private LocalDateTime regDate;

}
