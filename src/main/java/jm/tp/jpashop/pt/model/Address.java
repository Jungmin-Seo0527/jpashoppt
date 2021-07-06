package jm.tp.jpashop.pt.model;

import lombok.*;

import javax.persistence.Embeddable;

@Getter @Setter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

    private String city;
    private String street;
    private String etc;

}
