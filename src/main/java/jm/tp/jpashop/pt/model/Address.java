package jm.tp.jpashop.pt.model;

import lombok.*;

import javax.persistence.Embeddable;

import static lombok.AccessLevel.*;

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
