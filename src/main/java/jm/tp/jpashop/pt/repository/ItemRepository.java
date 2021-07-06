package jm.tp.jpashop.pt.repository;

import jm.tp.jpashop.pt.model.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}