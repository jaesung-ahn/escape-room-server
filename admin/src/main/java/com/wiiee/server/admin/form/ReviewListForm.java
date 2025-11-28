package com.wiiee.server.admin.form;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewListForm extends DefaultForm {

    private String message;
    private String writer;
    private String contentName;
    private Double rating;
    private Boolean isApproval;
}
