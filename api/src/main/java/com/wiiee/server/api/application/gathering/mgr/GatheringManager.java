package com.wiiee.server.api.application.gathering.mgr;

import com.wiiee.server.api.application.exception.CustomException;
import com.wiiee.server.api.domain.code.GatheringErrorCode;
import com.wiiee.server.common.domain.gathering.GatheringInfo;
import com.wiiee.server.common.domain.gathering.GatheringStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class GatheringManager {

    /**
     * 동행모집 조건에 따른 상태 처리
     */
    public static GatheringStatus getGatheringStatusInfo(GatheringStatus gatheringStatus, GatheringInfo gatheringInfo, int currentMemberSize) {
        Integer maxPeople = gatheringInfo.getMaxPeople();
        if (gatheringInfo.getHopeDate() != null &&
                gatheringStatus.equals(GatheringStatus.RECRUITING)) {

            // 희망일이 1일 전인 경우 마감 임박으로 상태 변경
            // 2~3인 모집일 경우 -> 1자리 남았을 때부터 마감임박
            // 4~8인 모집일 경우 -> 2자리 남았을 때부터 마감임박
            List<Integer> peopleConditionList = Arrays.asList(2, 3);
            boolean isDeadlineImminent = false;
            int calculatedPeopleNum = maxPeople - currentMemberSize;
            if (peopleConditionList.contains(maxPeople) && calculatedPeopleNum == 1 ) {
                isDeadlineImminent = true;
            }

            peopleConditionList = Arrays.asList(4, 5, 6, 7, 8);
            if (peopleConditionList.contains(maxPeople) && (calculatedPeopleNum == 1 || calculatedPeopleNum == 2)) {
                isDeadlineImminent = true;
            }

            long countDays = LocalDate.now().until(gatheringInfo.getHopeDate(), ChronoUnit.DAYS);

            if (countDays > 0) { // 날짜 지난 경우 완료 상태로 변경
                return GatheringStatus.RECRUIT_COMPLETED;
            }
            else if (countDays == -1 || isDeadlineImminent) {
                return GatheringStatus.DEADLINE_IMMINENT;
            }
        }

        return gatheringStatus;
    }

    /**
     * 카카오 오픈 채팅 url 체크
     */
    public static boolean checkKakaoOpenUrl(String url) {
        if (url != null && !url.startsWith("https://open.kakao.com/")) {
            throw new CustomException(GatheringErrorCode.ERROR_NOT_ALLOWED_OPEN_CHAT_URL);
        }
        else return url != null && url.startsWith("https://open.kakao.com/");
    }
}
