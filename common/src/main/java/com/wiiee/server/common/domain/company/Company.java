package com.wiiee.server.common.domain.company;

import com.wiiee.server.common.domain.BaseEntity;
import com.wiiee.server.common.domain.admin.AdminUser;
import com.wiiee.server.common.domain.content.Content;
import com.wiiee.server.common.domain.content.ContentBasicInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "company")
@Entity
public class Company extends BaseEntity {

    @Id
    @Column(name = "company_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "admin_user_id",
            referencedColumnName = "admin_user_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private AdminUser adminUser;

    @Embedded
    private CompanyBasicInfo basicInfo;

    @Embedded
    private CompanyBusinessInfo businessInfo;

    @OneToMany(mappedBy = "company", cascade = REMOVE)
    private Set<Content> contents = new HashSet<>();

    public Company(AdminUser adminUser, CompanyBasicInfo basicInfo, CompanyBusinessInfo businessInfo) {
        this.adminUser = adminUser;
        this.basicInfo = basicInfo;
        this.businessInfo = businessInfo;
    }

    public Company(CompanyBasicInfo basicInfo, CompanyBusinessInfo businessInfo) {
        this.basicInfo = basicInfo;
        this.businessInfo = businessInfo;
    }

    public void updateCompany(CompanyBasicInfo basicInfo, CompanyBusinessInfo businessInfo) {
        this.basicInfo = basicInfo;
        this.businessInfo = businessInfo;
    }

    public Content addContent(ContentBasicInfo contentBasicInfo) {
        Content contentToAdd = new Content(this, contentBasicInfo);
        contents.add(contentToAdd);
        return contentToAdd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return id.equals(company.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
