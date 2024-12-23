package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //FetchType.LAZY -> 필요한 시점에만 연관데이터 조회 초기 로딩 시 성능 최적화
    @OneToOne(fetch = FetchType.LAZY) //Member 테이블 사용되기 직전 select 지연로딩
    //@OneToOne(fetch = FetchType.EAGER) //일대일 즉시로딩, 따로 옵션 없을때 기본 설정
    @JoinColumn(name = "member_id") //Member table에 있는 member_id를 외래키 설정해라
    private Member member;

    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }

}
