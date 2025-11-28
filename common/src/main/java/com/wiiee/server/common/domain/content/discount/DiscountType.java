package com.wiiee.server.common.domain.content.discount;

import com.wiiee.server.common.domain.EnumInterface;
import com.wiiee.server.common.domain.content.Content;

public enum DiscountType implements Discountable, EnumInterface {
    FEDERAL(0, "연방할인") {
        @Override
        public boolean isDiscount(Content content) {
            return false;
        }
    },
    FREE(1, "자유할인") {
        @Override
        public boolean isDiscount(Content content) {
            return false;
        }
    },
    EARLY_MORNING(2, "조조할인") {
        @Override
        public boolean isDiscount(Content content) {
            return false;
        }
    },
    STUDENT(3, "학생할인") {
        @Override
        public boolean isDiscount(Content content) {
            return false;
        }
    }
    ;

    DiscountType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private final Integer code;
    private final String name;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }
}
