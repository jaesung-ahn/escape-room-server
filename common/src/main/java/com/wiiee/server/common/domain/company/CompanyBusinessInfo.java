package com.wiiee.server.common.domain.company;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class CompanyBusinessInfo {

    // 사업자 등록증 사진 id
    private Long registrationImageId;

    // 사업자 번호
    private String businessNumber;

    // 대표자명
    private String representativeName;

    // 대표 연락처
    private String repContractNumber;

    // 담당자 연락처
    private String chargeContractNumber;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "bank")
    private Bank bank;

    // 정산계좌
    private String account;

    public CompanyBusinessInfo(Long registrationImageId, String businessNumber, String representativeName, String repContractNumber, String chargeContractNumber, Bank bank, String account) {
        this.registrationImageId = registrationImageId;
        this.businessNumber = businessNumber;
        this.representativeName = representativeName;
        this.repContractNumber = repContractNumber;
        this.chargeContractNumber = chargeContractNumber;
        this.bank = bank;
        this.account = account;
    }
}
