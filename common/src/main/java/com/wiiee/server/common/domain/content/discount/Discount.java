package com.wiiee.server.common.domain.content.discount;

import com.wiiee.server.common.domain.BaseEntity;
import com.wiiee.server.common.domain.content.Content;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "discount", indexes = {})
@Entity
public class Discount extends BaseEntity {

    @Id
    @Column(name = "discount_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private DiscountType discountType;

    private Integer amount;

    private Boolean isDuplicated;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
                referencedColumnName = "content_id")
    private Content content;
}
