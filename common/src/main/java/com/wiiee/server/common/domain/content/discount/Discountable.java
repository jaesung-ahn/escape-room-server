package com.wiiee.server.common.domain.content.discount;

import com.wiiee.server.common.domain.content.Content;

public interface Discountable {
    boolean isDiscount(Content content);
}
