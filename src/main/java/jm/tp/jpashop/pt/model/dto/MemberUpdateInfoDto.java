package jm.tp.jpashop.pt.model.dto;

import jm.tp.jpashop.pt.model.Address;
import jm.tp.jpashop.pt.web.api.dto.MemberApiDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter @Setter(PRIVATE) @Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MemberUpdateInfoDto {

    private String name;
    private Address address;

    public static MemberUpdateInfoDto create(MemberApiDto dto) {
        return MemberUpdateInfoDto.builder()
                .name(dto.getName())
                .address(Address.builder()
                        .city(dto.getAddress().getCity())
                        .street(dto.getAddress().getStreet())
                        .etc(dto.getAddress().getEtc())
                        .build())
                .build();
    }
}
