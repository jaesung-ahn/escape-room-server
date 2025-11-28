package com.wiiee.server.api.domain.admin;

import com.wiiee.server.common.domain.admin.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminUser, Long> {
}
