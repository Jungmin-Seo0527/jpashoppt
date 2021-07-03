package jm.tp.jpashop.pt.model;

import jm.tp.jpashop.pt.model.item.Item;

public class OrderItem {

    private Long id;
    private Item item;
    private Order order;
    private int orderPrice;
    private int count;
}
