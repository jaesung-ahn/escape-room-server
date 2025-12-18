package com.wiiee.server.common.domain.company;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class CompanyBasicInfo {

    private String name;

    @Enumerated(value = EnumType.STRING)
    private State state;

    @Enumerated(value = EnumType.STRING)
    private City city;

    private String address;
    private String detailAddress;

    @Column(length = 600)
    private String notice;

    private String contact;

    private String url;

    private Boolean isOperated;

//    일, 월, 화, 수, 목, 금, 토
//    1   2   3   4   5   6  7
    @Type(ListArrayType.class)
    @Column(columnDefinition = "int[]")
    private List<Integer> businessDayCodes;

    // 연중 무휴
    private Boolean isAlwaysOperated;

    @Type(ListArrayType.class)
    @Column(columnDefinition = "bigint[]")
    private List<Long> imageIds = new ArrayList<>();

    public CompanyBasicInfo(String name, State state, City city, String address, String detailAddress, String notice, String contact, String url, Boolean isOperated, List<Integer> businessDayCodes, Boolean isAlwaysOperated, List<Long> imageIds) {
        this.name = name;
        this.state = state;
        this.city = city;
        this.address = address;
        this.detailAddress = detailAddress;
        this.notice = notice;
        this.contact = contact;
        this.url = url;
        this.isOperated = isOperated;
        this.businessDayCodes = businessDayCodes;
        this.isAlwaysOperated = isAlwaysOperated;
        this.imageIds = imageIds;
    }

    /**
     * 대표 이미지 ID를 반환합니다.
     * imageIds가 비어있거나 null인 경우 null을 반환하여
     * 호출자가 안전하게 처리할 수 있도록 합니다.
     *
     * @return 첫 번째 이미지 ID, 없으면 null
     */
    public Long getRepresentativeImageId() {
        if (imageIds == null || imageIds.isEmpty()) {
            return null;
        }
        return imageIds.get(0);
    }
}
