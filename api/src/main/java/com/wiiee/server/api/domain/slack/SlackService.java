package com.wiiee.server.api.domain.slack;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.wiiee.server.api.config.properties.SlackProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class SlackService {

    private final SlackProperties slackProperties;

    public void sendSlackMessage(String contentName, String message){
        if (!slackProperties.enabled()) {
            log.info("[MOCK] Slack message would be sent - Content: {}, Message: {}",
                    contentName, message.substring(0, Math.min(7, message.length())) + "...");
            return;
        }

        try{

            MethodsClient methods = Slack.getInstance().methods(slackProperties.token());
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(slackProperties.channel().monitor())
                    .text("'" +contentName+ "'에 새로운 리뷰가 등록되었습니다. (<http://3.39.184.217:8080/admin/review/list|어드민 리뷰 링크>)\n"
                        +"● " + message.substring(0, 7) + "...")
                    .build();

            methods.chatPostMessage(request);

            log.info("success sendSlackMessage");
        } catch (SlackApiException | IOException e) {
            log.error(e.getMessage());
        }
    }
}
