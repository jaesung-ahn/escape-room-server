package com.wiiee.server.api.application.company;

import com.wiiee.server.common.domain.common.City;
import com.wiiee.server.common.domain.common.State;
import com.wiiee.server.common.domain.company.Bank;
import com.wiiee.server.common.domain.company.CompanyBasicInfo;
import com.wiiee.server.common.domain.company.CompanyBusinessInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.List;

@Value
public class CompanyPostRequestDTO {
    // TODO: 실제 구현에서는 Principal로 받아서 진행
    Long adminId;

    String name;
    Integer stateCode;
    Integer cityCode;
    String address;
    String detailAddress;
    String notice;
    String contact;
    String url;
    Boolean isOperated;
    List<Integer> businessDayCodes;
    Boolean isAlwaysOperated;
    List<Long> imageIds;

    Long registrationImageId;
    String businessNumber;
    String representativeName;
    String repContractNumber;
    String chargeContractNumber;
    Integer bankCode;
    String account;

    public CompanyBasicInfo toCompanyBasicInfo() {
        return new CompanyBasicInfo(
                name,
                State.valueOf(stateCode),
                City.valueOf(cityCode),
                address,
                detailAddress,
                notice,
                contact,
                url,
                isOperated,
                businessDayCodes,
                isAlwaysOperated,
                imageIds);
    }

    public CompanyBusinessInfo toCompanyBusinessInfo() {
        return new CompanyBusinessInfo(
                registrationImageId,
                businessNumber,
                representativeName,
                repContractNumber,
                chargeContractNumber,
                Bank.valueOf(bankCode),
                account);
    }
}
