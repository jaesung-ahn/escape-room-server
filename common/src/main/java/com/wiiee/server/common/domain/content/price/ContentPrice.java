package com.wiiee.server.common.domain.content.price;

import com.wiiee.server.common.domain.content.Content;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "content_price", indexes = {})
@Entity
public class ContentPrice {

    @Id
    @Column(name = "content_price_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // 사람 수
    private Integer peopleNumber;

    // TODO: 자료형 변경 예정
    private Integer price;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            referencedColumnName = "content_id",
            nullable = false)
    private Content content;

    public ContentPrice(Integer peopleNumber, Integer price, Content content) {
        this.peopleNumber = peopleNumber;
        this.price = price;
        this.content = content;
    }

    public void updateContentPrice(Integer peopleNumber, Integer price) {
        this.peopleNumber = peopleNumber;
        this.price = price;
    }
}
