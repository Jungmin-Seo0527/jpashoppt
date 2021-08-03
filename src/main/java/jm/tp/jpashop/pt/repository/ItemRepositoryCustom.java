package jm.tp.jpashop.pt.repository;

import jm.tp.jpashop.pt.web.api.dto.ItemBuyersDto;

public interface ItemRepositoryCustom {

    ItemBuyersDto findItemBuyersById(Long id);
}
