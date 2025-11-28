package com.wiiee.server.api.application.gathering;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Max;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class GatheringUpdateRequestDTO {

    @Schema(description = "동행모집 아이디")
    Long gatheringId;
    @Schema(description = "동행모집 제목")
    String title;
    @Schema(description = "동행모집 내용")
    String information;
    @Schema(description = "시도 코드")
    Integer stateCode;
    @Schema(description = "시군구(작은 지역 단위)")
    Integer cityCode;
    @Schema(description = "동행모집 유형 코드")
    Integer recruitTypeCode;
    @Schema(description = "동행모집 최대 인원수(8명까지)")
    @Max(value = 8)
    Integer maxPeople;
    @Schema(description = "성별 코드")
    Integer genderTypeCode;
    @Schema(description = "일시 협의 여부")
    Boolean isDateAgreement;
    @Schema(description = "동행 희망일")
    LocalDate hopeDate;
    @Schema(description = "카카오 오픈채팅 url")
    String kakaoOpenChatUrl;

    @Schema(description = "나이 그룹값 코드 목록")
    List<Integer> ageGroupCodes;

    @Builder
    public GatheringUpdateRequestDTO(Long gatheringId, String title, String information, Integer stateCode, Integer cityCode, Integer recruitTypeCode, Integer maxPeople, Integer genderTypeCode, Boolean isDateAgreement, LocalDate hopeDate, String kakaoOpenChatUrl, List<Integer> ageGroupCodes) {
        this.gatheringId = gatheringId;
        this.title = title;
        this.information = information;
        this.stateCode = stateCode;
        this.cityCode = cityCode;
        this.recruitTypeCode = recruitTypeCode;
        this.maxPeople = maxPeople;
        this.genderTypeCode = genderTypeCode;
        this.isDateAgreement = isDateAgreement;
        this.hopeDate = hopeDate;
        this.kakaoOpenChatUrl = kakaoOpenChatUrl;
        this.ageGroupCodes = ageGroupCodes;
    }
}
