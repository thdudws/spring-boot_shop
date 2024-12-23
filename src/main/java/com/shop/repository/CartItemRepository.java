package com.shop.repository;

import com.shop.dto.CartDetailDto;
import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    //카트 아이디랑 상품 아이디를 이용해 상품이 장바구니에 들었는지 조회
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    /*  SELECT ci.cart_id, i.item_nm, i.price, ci.count, im.img_url
        FROM cart_item ci, item_img im
        JOIN item i
        WHERE ci.cart_id = 2 and ci.item_id = im.item_id and
        im.repimg_yn = 'Y'
        ORDER BY ci.reg_time DESC;
    */

    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

}
