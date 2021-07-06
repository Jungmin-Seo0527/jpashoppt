package jm.tp.jpashop.pt.model.item;

import jm.tp.jpashop.pt.exception.NotEnoughStockException;
import jm.tp.jpashop.pt.model.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    public void addStock(int addQuantity) {
        stockQuantity += addQuantity;
    }

    public void removeStock(int subQuantity) {
        int count = stockQuantity - subQuantity;
        if (count < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        stockQuantity = count;
    }
}
