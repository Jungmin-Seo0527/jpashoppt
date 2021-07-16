package jm.tp.jpashop.pt.web.controller.dto;

import jm.tp.jpashop.pt.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static lombok.AccessLevel.PRIVATE;

@Getter @Setter @Builder
@AllArgsConstructor(access = PRIVATE)
public class MemberListForm {
    private Long id;
    private String name;
    private String city;
    private String street;
    private String zipcode;

    public static MemberListForm create(Member member) {
        return MemberListForm.builder()
                .id(member.getId())
                .name(member.getName())
                .city(member.getAddress().getCity())
                .street(member.getAddress().getStreet())
                .zipcode(member.getAddress().getEtc())
                .build();
    }
}
