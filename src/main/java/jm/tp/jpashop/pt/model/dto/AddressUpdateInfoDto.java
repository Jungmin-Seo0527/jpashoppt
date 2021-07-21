package jm.tp.jpashop.pt.model.dto;

import jm.tp.jpashop.pt.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter @Setter @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class AddressUpdateInfoDto {

    private String city;
    private String street;
    private String etc;

    public static AddressUpdateInfoDto create(Address address) {
        return AddressUpdateInfoDto.builder()
                .city(address.getCity())
                .street(address.getStreet())
                .etc(address.getEtc())
                .build();
    }
}
