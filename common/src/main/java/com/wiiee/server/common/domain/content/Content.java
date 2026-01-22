package com.wiiee.server.common.domain.content;

import com.wiiee.server.common.domain.BaseEntity;
import com.wiiee.server.common.domain.company.Company;
import com.wiiee.server.common.domain.content.discount.Discount;
import com.wiiee.server.common.domain.content.price.ContentPrice;
import com.wiiee.server.common.domain.content.review.Review;
import com.wiiee.server.common.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "content", indexes = {
    @Index(name = "idx_content_company_operated", columnList = "company_id, is_operated")
})
@Entity
public class Content extends BaseEntity {

    @Id
    @Column(name = "content_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "company_id",
            referencedColumnName = "company_id",
            nullable = false)
    private Company company;

    @Embedded
    private ContentBasicInfo contentBasicInfo;

    @OneToMany(mappedBy = "content", cascade = {PERSIST, REMOVE})
    private List<ContentPrice> contentPrices = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = {PERSIST, REMOVE})
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "content", cascade = {PERSIST, REMOVE})
    private List<Discount> discounts = new ArrayList<>();

    public Content(Company company, ContentBasicInfo contentBasicInfo) {
        this.company = company;
        this.contentBasicInfo = contentBasicInfo;
    }

    public void updateContent(ContentBasicInfo contentBasicInfo) {
        this.contentBasicInfo = contentBasicInfo;
    }

    public ContentPrice addPrice(Integer number, Integer value) {
        final var priceToAdd = new ContentPrice(number, value, this);
        contentPrices.add(priceToAdd);
        return priceToAdd;
    }

    public Review addReview(User writer, String message, Double rating, Integer joinNumber, List<Long> imageIds, LocalDate realGatherDate) {
        final var reviewToAdd = new Review(message, writer, this, rating, joinNumber, imageIds, realGatherDate);
        reviews.add(reviewToAdd);
        return reviewToAdd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return id.equals(content.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
