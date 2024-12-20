package com.shop.repository;

import com.shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /*select * from orders o
    join member m
    on m.member_id = o.member_id
    where m.email like '%admin@admin.com%'
    order by o.order_date desc;
    * */

    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc "
    )
    //현재 로그인한 사용자 주문 데이터를 페이징 조건에 맞춰 조회
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    /*select count(*) from member
    join orders
    on orders.member_id = member.member_id
    where member.email like '%admin@admin.com%';
    * */

    @Query("select count(o) from Order o " +
            "where o.member.email = :email"
    )
    Long countOrders(@Param("email") String email); //로그인한 회원의 주문 개수 확인

}
