package jm.tp.jpashop.pt.model;

import jm.tp.jpashop.pt.model.item.Item;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private Long id;
    private String name;
    private List<Item> items = new ArrayList<>();
    private Category parent;
    private List<Category> child = new ArrayList<>();
}
