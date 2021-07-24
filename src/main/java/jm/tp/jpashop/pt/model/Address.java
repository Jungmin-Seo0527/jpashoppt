package jm.tp.jpashop.pt.model;


import jm.tp.jpashop.pt.model.dto.AddressUpdateInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;


@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Getter @Setter(PRIVATE)
@Builder
@Embeddable
@EqualsAndHashCode
@ToString
public class Address {

    private String city;
    private String street;
    private String etc;

    public void updateInfo(AddressUpdateInfoDto dto) {
        setCity(dto.getCity());
        setStreet(dto.getStreet());
        setEtc(dto.getEtc());
    }
}