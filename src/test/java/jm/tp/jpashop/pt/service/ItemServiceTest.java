package jm.tp.jpashop.pt.service;

import jm.tp.jpashop.pt.model.item.Album;
import jm.tp.jpashop.pt.model.item.Book;
import jm.tp.jpashop.pt.model.item.Item;
import jm.tp.jpashop.pt.model.item.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired ItemService itemService;

    @Test
    @DisplayName("상품 등록")
    public void saveItem() throws Exception {
        // given
        Book book = Book.builder()
                .name("JPA1")
                .price(10000)
                .stockQuantity(20)
                .build();

        Album album = Album.builder()
                .name("2집")
                .price(10000)
                .stockQuantity(20)
                .artist("BTS")
                .etc("?")
                .build();

        Movie movie = Movie.builder()
                .name("매트릭스")
                .build();

        // when
        itemService.saveItem(book);
        itemService.saveItem(album);
        itemService.saveItem(movie);

        // then
        List<Item> items = itemService.findItems();
        assertThat(items).contains(book, album, movie);
        assertThat(items.size()).isEqualTo(7);
    }

    @Test
    @DisplayName("아이디로 상품 찾기")
    public void findOneTest() throws Exception {
        // given
        Item item = Book.builder()
                .name("테스트용")
                .price(10000000)
                .stockQuantity(0)
                .build();

        // when
        Long itemId = itemService.saveItem(item);

        // then
        assertThat(itemService.findOne(itemId)).isSameAs(item);
    }

}