package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.*;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email) {
        //장바구니에 담을 상품 엔티티 조회
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(()->new EntityNotFoundException());
        //현재 로그인한 회원 엔티티 조회
        Member member = memberRepository.findByEmail(email);

        //현재 로그인한 회원의 장바구니 엔티티 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null) {  //상품을 처음 장바구니에 담을 경우 해당 회원의 장바구니 엔티티 생성
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        //현재 상품이 장바구니에 담겼는지 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

       /* if(savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount()); //이미 담긴 상품일 경우 더해주기
            return savedCartItem.getId();
        }else {
            //CartItem 엔티티 생성
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem); //상품 저장
            return cartItem.getId();
        }*/

        if(savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
        }else {
            savedCartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(savedCartItem);
        }
        return savedCartItem.getId();
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        //현재 로그인한 회원의 장바구니 엔티티 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null) { //장바구니에 한 번도 안 담았으면 빈 리스트
            return cartDetailDtoList;
        }
        //장바구니 상품 조회
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        Member curMember = memberRepository.findByEmail(email); //회원 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()->new EntityNotFoundException());
        Member savedMember = cartItem.getCart().getMember(); //상품 저장한 회원 조회

        //로그인 회원과 저장한 회원 같은지
        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }

        return true;
    }

    //장바구니 수량 업데이트 메소드
    public void updateCartItemCount(Long cartItemId, int count) {
        log.info("---------------updateCartItemCount----------------");

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()->new EntityNotFoundException());

        cartItem.setCount(count);
    }

    //장바구니 상품 번호를 파라미터로 받아 삭제
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(()->new EntityNotFoundException());
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        List<OrderDto> orderDtoList = new ArrayList<>();
        //전달받은 주문 상품 번호로 orderDto를 만든다
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(()->new EntityNotFoundException());

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        //장바구니 상품을 주문하도록 주문 로직 호출
        Long orderId = orderService.orders(orderDtoList, email);

        //주문한 상품 장바구니에서 제거
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(()->new EntityNotFoundException());
            cartItemRepository.delete(cartItem);
        }
        return orderId;
    }


}
