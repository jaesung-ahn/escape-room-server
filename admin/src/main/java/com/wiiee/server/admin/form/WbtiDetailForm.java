package com.wiiee.server.admin.form;

import com.wiiee.server.common.domain.wbti.Wbti;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class WbtiDetailForm {

    public Long id;
    public LocalDateTime createdAt;

    private String name;

    private Long wbtiImageId;
    private String wbtiImageUrl;

    private String tags;
    private String descriptions;

    private List<Integer> wbtiPartnerList = new ArrayList<>();
    private List<WbtiPartnerForm> wbtiPartnerForms = new ArrayList<>();

    public static WbtiDetailForm fromWbtiSimpleForm(Wbti wbti) {

        return WbtiDetailForm.builder()
                .id(wbti.getId())
                .name(wbti.getName())
                .wbtiImageId(wbti.getWbtiImageId())
                .tags(wbti.getTags())
                .descriptions(wbti.getDescriptions())
                .wbtiPartnerList(null)
                .wbtiPartnerForms(
                        wbti.getWbtiPartners().stream().map(
                                w -> WbtiPartnerForm.fromWbtiPartnerForm(w)
                        ).collect(Collectors.toList())
                )
                .createdAt(wbti.getCreatedAt())
                .build();
    }

    @Builder(access = AccessLevel.PROTECTED)
    @Setter
    @Getter
    public static class WbtiPartnerForm {
        private Long wbtiId;
        private String wbtiName;

        public WbtiPartnerForm(Long wbtiId, String wbtiName) {
            this.wbtiId = wbtiId;
            this.wbtiName = wbtiName;
        }

        public static WbtiPartnerForm fromWbtiPartnerForm(Wbti wbti) {
            return WbtiPartnerForm.builder()
                    .wbtiId(wbti.getId())
                    .wbtiName(wbti.getName())
                    .build();
        }
    }
}
