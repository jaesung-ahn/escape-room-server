package com.wiiee.server.common.domain.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiiee.server.common.domain.company.Company;
import com.wiiee.server.common.domain.company.CompanyBasicInfo;
import com.wiiee.server.common.domain.company.CompanyBusinessInfo;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin_user")
@Entity
public class AdminUser implements UserDetails {

    @Id
    @Column(name = "admin_user_id")
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    public String adminEmail;

    private String password;

    private String adminName;

    private String role;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @CreatedDate
    @Column(name = "created_at", columnDefinition = "timestamp with time zone", updatable = false)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "timestamp with time zone")
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        for(String role : role.split(",")){
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    @Builder(access = AccessLevel.PROTECTED)
    private AdminUser(Long id, String email, String password, String role) {
        this.id = id;
        this.adminEmail = email;
        this.password = password;
        this.role = role;
    }

    public static AdminUser of(String email, String password) {

        return AdminUser
                .builder()
                .email(email)
                .password(password)
                .role(Role.SUPER_ADMIN.name())
                .build();
    }

    public Company addCompany(CompanyBasicInfo companyBasicInfo, CompanyBusinessInfo companyBusinessInfo) {
        return new Company(this, companyBasicInfo, companyBusinessInfo);
    }

    @Override
    public String getUsername() {
        return this.adminName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
