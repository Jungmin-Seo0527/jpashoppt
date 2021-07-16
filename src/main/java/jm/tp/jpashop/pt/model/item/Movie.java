package jm.tp.jpashop.pt.model.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@DiscriminatorValue("M")
@Getter @Setter @SuperBuilder
@NoArgsConstructor(access = PROTECTED) @AllArgsConstructor(access = PRIVATE)
public class Movie extends Item {

    private String director;
    private String actor;
}
