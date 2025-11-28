package com.wiiee.server.admin.repository;

import com.wiiee.server.common.domain.admin.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByAdminEmail(String adminEmail);
}
