package jm.tp.jpashop.pt.web.controller;

import jm.tp.jpashop.pt.model.item.Book;
import jm.tp.jpashop.pt.service.ItemService;
import jm.tp.jpashop.pt.service.dto.UpdateBookDto;
import jm.tp.jpashop.pt.web.controller.dto.BookForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {

        model.addAttribute("form", BookForm.builder().build());
        return "item/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        itemService.saveItem(form.toBookEntity());
        return "redirect:/items";
    }

    @GetMapping("/items")
    public String itemList(Model model) {

        model.addAttribute("items", itemService.findItems()
                .stream()
                .map(BookForm::create)
                .collect(Collectors.toList()));

        return "item/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);
        model.addAttribute("form", BookForm.create(item));
        return "item/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute UpdateBookDto dto) {
        itemService.updateItem(itemId, dto);
        return "redirect:/items";
    }
}
