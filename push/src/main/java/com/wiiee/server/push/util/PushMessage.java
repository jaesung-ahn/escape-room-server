package com.wiiee.server.push.util;

import com.wiiee.server.common.domain.push.PushType;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushMessage {

    public static String makePushBodyMessage(String pushContent, PushType pushType, Long id) {

        Map<String, Object> map = new HashMap();

        if (pushType.equals(PushType.EVENT_ALL_USER) ||
                pushType.equals(PushType.GATHERING_REQUEST) ||
                pushType.equals(PushType.GATHERING_CONFIRM)) {
            map.put("id", id);
        }
        map.put("text", pushContent);
        map.put("type", pushType.toString());
        JSONObject json =  new JSONObject(map);
        return json.toJSONString();
    }
}


