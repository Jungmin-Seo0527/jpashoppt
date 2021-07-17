package jm.tp.jpashop.pt.web.controller.dto;

import jm.tp.jpashop.pt.model.item.Book;
import jm.tp.jpashop.pt.model.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter @Setter @Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class BookForm {

    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String author;
    private String isbn;

    public Book toBookEntity() {
        return Book.builder()
                .id(id)
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .author(author)
                .isbn(isbn)
                .build();
    }

    public static BookForm create(Item book) {
        return BookForm.builder()
                .id(book.getId())
                .name(book.getName())
                .price(book.getPrice())
                .stockQuantity(book.getStockQuantity())
                .build();
    }
}
