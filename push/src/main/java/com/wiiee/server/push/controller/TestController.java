package com.wiiee.server.push.controller;

import com.wiiee.server.push.service.FirebaseCloudMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/push")
@RestController
public class TestController {

    @Autowired
    private FirebaseCloudMessageService firebaseCloudMessageService;
//    @GetMapping("/test")
//    public String test() {
//
////        try {
////        firebaseCloudMessageService.sendMessageTo("eM-rGwTsS_uGtCkAxo27jr:APA91bEMYvpPcyqWDugM-HigzGEuTS3IODnosbbjQA1xlUG7FKoentwx95S835Yr1IMm7WyDYCyZROsqbq0I10iW_nXhkgm0lKkmD_4kwa7jGzWBS_ZXXB2PKlIEpVJe5n6fvE5OrwSi",
////                    "testTitle2222", "알림이 잘 가나용???");
////        } catch (IOException e) {
////            log.debug(String.valueOf("e = " + e));
////            throw new RuntimeException(e);
////        }
//        return "test call";
//    }

//    @Operation(summary = "특정 회원 한명 푸시 보내기", security = { })
//    @PostMapping(value = "sendOneUserPush", produces = APPLICATION_JSON_VALUE)
//    public ResponseEntity<PushResponseDTO> sendOneUserPush(@Validated @RequestBody PushRequestDTO requestDTO) {
//
//        log.debug(String.valueOf("requestDTO = " + requestDTO));
//
//        return ResponseEntity.ok().body(
//                PushResponseDTO.fromPushResponseDTO("0000", "푸시가 성공적으로 발송되었습니다.")
//        );
//    }
}
