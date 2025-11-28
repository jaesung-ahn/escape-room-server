package com.wiiee.server.admin.form;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
public class CompanyForm extends DefaultForm {

    @NotEmpty(message = "업체명은 필수 입니다")
    private String name;

    private String imageUrl;
    private Long imageId;

    // 사업자 번호
    @NotEmpty(message = "사업자 번호는 필수 입니다")
    private String businessNumber;

    // 사업자 등록증 이미지 url
    private String businessRegImageUrl;
    private Long businessRegImageId;

    private String address;

    private String detailAddress;

    // 업체가 속한 지역
    private int companyCityCode;

    // 운영요일
    private boolean mon = true;
    private boolean tue = true;
    private boolean wed = true;
    private boolean thu = true;
    private boolean fri = true;
    private boolean sat = true;
    private boolean sun = true;

    // 연중무휴
    private boolean alwaysOperated = false;

    // 업체 전화번호
    private String companyNumber;

    private String homepageUrl;

    // 공지사항
    private String notice;

    // 편의시설 정보 < 우선 생략
    // private String facilityInformation;

    // 대표자명
    private String representativeName;

    // 대표 연락처
    private String repContractNumber;

    // 담당자 연락처
    private String chargeContractNumber;

    // 정산 대상 은행
    private int companyBankCode;

    // 정산계좌
    private String settlementAccount;

    // 운영 상태(운영중, 운영 중지)
    private boolean operated;
}

