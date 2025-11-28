package com.wiiee.server.admin.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class ReviewDetailForm extends DefaultForm{

    private String message;
    private String writer;
    private String contentName;
    private Double rating;
    private Integer joinNumber;
    private Boolean isApproval;
    private LocalDate realGatherDate;

    private List<String> reviewImages = new ArrayList<>();

//    @Setter
//    @Getter
//    public static class ReviewImageForm {
//        private String imgUrl;
//        private Long imgId;
//
//        public ReviewImageForm(String url, Long id) {
//            this.imgUrl = url;
//            this.imgId = id;
//        }
//    }
}
