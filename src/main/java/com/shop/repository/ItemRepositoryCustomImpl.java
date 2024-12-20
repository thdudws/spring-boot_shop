package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    //Querydsl 동적 쿼리 생성을 위해 jpaQueryFactory 클래스 사용
    private  JPAQueryFactory jpaQueryFactory;

    //jpaQueryFactory 생성자로 EntityManager 객체 넣기
    public ItemRepositoryCustomImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    //상품 판매 상태에 따른 조회
    private BooleanExpression searchSellStatusEq(ItemSellStatus itemSellStatus) {
        return itemSellStatus == null ? null : QItem.item.itemSellStatus.eq(itemSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();  //현재시간

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    //searchBy 값에 따라 검색어 포함 상품 반환
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" +searchQuery+ "%");
        }else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" +searchQuery+ "%");
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        /*log.info("-------------------------------------------------------------------");
        log.info("itemSearchDto : " + itemSearchDto);
        log.info("pageable : " + pageable);
        log.info(regDtsAfter(itemSearchDto.getSearchDateType()));
        log.info(searchSellStatusEq(itemSearchDto.getSearchSellStatus()));
        log.info(searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()));
        log.info(pageable.getOffset());
        log.info(pageable.getPageSize());
        log.info("-------------------------------------------------------------------");*/

        List<Item> content = jpaQueryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();   //조회 대상 리스트 반환

        Long total = jpaQueryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType())
                        , searchSellStatusEq(itemSearchDto.getSearchSellStatus())
                        , searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                ).fetchOne();   //조회건이 1건이면 해당 타입 반환 1건 이상은 에러

        log.info("total : " + total);

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" +searchQuery+ "%");
    }

    @Override
    public Page<MainItemDto> getMainItemPAge(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        /*QueryResults<MainItemDto> results = jpaQueryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();*/

        List<MainItemDto> content = jpaQueryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /*select count(*)
          from item_img.item_id = item.item_id
          where itemimg.repimgYn == 'Y' and
          item.itemNm.like '%스프링%';*/

        /*select count(*)
        from item_img img
        join item i
        on img.item_id = i.item_id
        where
        img.repimg_yn = 'Y'
        and i.item_nm like '%스프링%'*/

        long total = jpaQueryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
    }
}