package com.wiiee.server.api.application.gathering.comment;

import lombok.Getter;

import jakarta.validation.constraints.NotBlank;

@Getter
public class CommentPutRequestDTO {

    @NotBlank(message = "메세지를 입력하세요.")
    private String message;

    protected CommentPutRequestDTO() {
    }

    public CommentPutRequestDTO(String message) {
        this.message = message;
    }

}
