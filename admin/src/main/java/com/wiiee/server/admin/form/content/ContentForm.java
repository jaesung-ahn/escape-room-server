package com.wiiee.server.admin.form.content;

import com.wiiee.server.admin.form.DefaultForm;
import com.wiiee.server.common.domain.content.Content;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Builder
@Setter
@Getter
public class ContentForm extends DefaultForm {

    private Long id;
    private Long companyId;

    private String companyName;

    @NotEmpty(message = "놀거리명은 필수 입니다")
    private String name;

    private boolean operated = true;

    private boolean displayNew = false;

    private String newDisplayExpirationDate;

    private int genreCode;

    private Integer minPeople;

    private Integer maxPeople;
    private Integer difficultyCode;
    private Integer activityLevelCode;
    private Integer escapeTypeCode;

    private Boolean noEscapeType = false;

    private Integer playTime;

    private String information;

    private List<ContentImageForm> contentImages;
    private List<ContentPriceForm> contentPrices;

    public ContentForm() {
    }

    public ContentForm(Long id, Long companyId, String companyName, String name, boolean operated, boolean displayNew, String newDisplayExpirationDate, int genreCode, Integer minPeople, Integer maxPeople, Integer difficultyCode, Integer activityLevelCode, Integer escapeTypeCode, Boolean noEscapeType, Integer playTime, String information, List<ContentImageForm> contentImages, List<ContentPriceForm> contentPrices) {
        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.name = name;
        this.operated = operated;
        this.displayNew = displayNew;
        this.newDisplayExpirationDate = newDisplayExpirationDate;
        this.genreCode = genreCode;
        this.minPeople = minPeople;
        this.maxPeople = maxPeople;
        this.difficultyCode = difficultyCode;
        this.activityLevelCode = activityLevelCode;
        this.escapeTypeCode = escapeTypeCode;
        this.noEscapeType = noEscapeType;
        this.playTime = playTime;
        this.information = information;
        this.contentImages = contentImages;
        this.contentPrices = contentPrices;
    }

    public static ContentForm fromContentSimpleForm(Content content) {
        return ContentForm.builder()
                .id(content.getId())
                .name(content.getContentBasicInfo().getName())
                .companyName(content.getCompany().getBasicInfo().getName())
                .build();
    }

    @Setter
    @Getter
    public static class ContentImageForm {
        private String imgUrl;

        private Long imgId;
        private boolean representativePhoto;

        public ContentImageForm(String url, Long id, boolean representativePhoto) {
            this.imgUrl = url;
            this.imgId = id;
            this.representativePhoto = representativePhoto;
        }
    }

    @Data
    public static class ContentPriceForm {
        private Long contentPriceId;
        private Integer peopleNumber;

        private Integer price;

        public ContentPriceForm(Integer peopleNumber, Integer price) {
            this.peopleNumber = peopleNumber;
            this.price = price;
        }

        public ContentPriceForm(Long contentPriceId, Integer peopleNumber, Integer price) {
            this.contentPriceId = contentPriceId;
            this.peopleNumber = peopleNumber;
            this.price = price;
        }
    }

}
