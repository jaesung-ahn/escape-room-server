package com.wiiee.server.push.constant;

import com.wiiee.server.common.domain.gathering.request.GatheringRequestStatus;
import com.wiiee.server.common.domain.push.PushType;

import java.util.HashMap;

public class PushMessageConst {

    public static final String TITLE = "title";
    public static final String PUSH_CONTENT = "push_content";

    public static HashMap<String, String> getPushMessageByType(PushType pushType, String title, String nickname, int confirmCode) {

        if (title != null && title.length() > 5) {
            title = title.substring(0, 5) + "...";
        }

        if (nickname != null && nickname.length() > 7) {
            nickname = nickname.substring(0, 7) + "...";
        }

        HashMap<String, String> pushMsgMap = new HashMap<>();
        if (pushType.equals(PushType.GATHERING_REQUEST)) {
            pushMsgMap.put(TITLE, "wiiee [동행신청]");
            pushMsgMap.put(PUSH_CONTENT, "\"" + title + "\"내 모집에 " + nickname + "님의 동행 신청이 들어왔습니다.");
        }
        else if (pushType.equals(PushType.GATHERING_CONFIRM)) {
            pushMsgMap.put(TITLE, "wiiee [신청답변]");
            if (confirmCode == GatheringRequestStatus.APPROVAL.getCode()) {
                pushMsgMap.put(PUSH_CONTENT, "\"" + title + "\" 동행 신청이 수락되었습니다.");
            } else if (confirmCode == GatheringRequestStatus.REJECT.getCode()) {
                pushMsgMap.put(PUSH_CONTENT, "\"" + title + "\" 동행 신청이 거절되었습니다.");
            }

        }
        return pushMsgMap;
    }
}
