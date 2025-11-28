package com.wiiee.server.admin.service;

import com.wiiee.server.admin.repository.AdminUserRepository;
import com.wiiee.server.common.domain.admin.AdminUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
//@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;

    @Autowired
    public AdminUserService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Transactional
    public Optional<AdminUser> saveAdminUser() {

        log.debug("saveAdminUser() ");

        String encodedPassword = new BCryptPasswordEncoder().encode("wiiee1234!");

        Optional<AdminUser> adminUser = Optional.of(
                adminUserRepository.save(AdminUser.of("wiiee_admin@wiiee.co.kr", encodedPassword))
        );

        return adminUser;
    }

//    public AdminUser getAdminUser() {
//        return adminUser;
//    }
}
