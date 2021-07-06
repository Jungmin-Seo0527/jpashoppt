package jm.tp.jpashop.pt.service;

import jm.tp.jpashop.pt.model.item.Item;
import jm.tp.jpashop.pt.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public Long saveItem(Item item) {
        itemRepository.save(item);
        return item.getId();
    }

    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품은 존재하지 않습니다"));
        item.setPrice(price);
        item.setName(name);
        item.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품은 존재하지 않습니다"));
    }
}
