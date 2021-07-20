package jm.tp.jpashop.pt.web.api.dto;

import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Data @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberApiDto {

    private String name;
    private Address address;

    public static MemberApiDto create(Member member) {
        return MemberApiDto.builder()
                .name(member.getName())
                .address(Address.builder()
                        .city(member.getAddress().getCity())
                        .street(member.getAddress().getStreet())
                        .etc(member.getAddress().getEtc())
                        .build())
                .build();
    }

    public Member toMemberEntity() {
        return Member.builder()
                .name(getName())
                .address(Address.builder()
                        .city(getAddress().getCity())
                        .street(getAddress().getStreet())
                        .etc(getAddress().getEtc())
                        .build())
                .build();
    }
}
