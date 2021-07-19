package jm.tp.jpashop.pt.model.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@DiscriminatorValue("B")
@Getter @Setter(PRIVATE) @SuperBuilder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Book extends Item {

    private String author;
    private String isbn;

    public void update(String name, int price, int stockQuantity, String author, String isbn) {
        super.update(name, price, stockQuantity);
        setAuthor(author);
        setIsbn(isbn);

    }
}
