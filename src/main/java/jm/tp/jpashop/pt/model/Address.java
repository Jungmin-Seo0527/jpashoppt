package jm.tp.jpashop.pt.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;


@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Getter @Setter(PRIVATE)
@Builder
@Embeddable
public class Address {

    private String city;
    private String street;
    private String etc;
}