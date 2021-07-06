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
        Book book = new Book();
        book.setName("JPA1");
        book.setPrice(10000);
        book.setStockQuantity(20);

        Album album = new Album();
        album.setName("2집");
        album.setPrice(10000);
        album.setStockQuantity(20);
        album.setArtist("BTS");
        album.setEtc("?");

        Movie movie = new Movie();
        movie.setName("메트릭스");

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
        Item item = new Book();
        item.setName("테스트용");
        item.setPrice(10000000);
        item.setStockQuantity(0);

        // when
        Long itemId = itemService.saveItem(item);

        // then
        assertThat(itemService.findOne(itemId)).isSameAs(item);
    }

}