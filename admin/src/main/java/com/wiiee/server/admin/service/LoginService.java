package com.wiiee.server.admin.service;

import com.wiiee.server.admin.repository.AdminUserRepository;
import com.wiiee.server.common.domain.admin.AdminUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class LoginService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

//    public LoginService(AdminUserRepository adminUserRepository) {
//        this.adminUserRepository = adminUserRepository;
//    }

//    @Override
//    public UserDetails loadUserByUsername(String adminEmail) throws UsernameNotFoundException {
//        return null;
//    }

    @Override
    public UserDetails loadUserByUsername(String adminEmail) throws UsernameNotFoundException {
        log.info("call loadUserByUsername : ");
        log.info("call adminEmail : " + adminEmail);
        //adminUser 정보 조회
        Optional<AdminUser> adminUser = adminUserRepository.findByAdminEmail(adminEmail);

        log.info("authAdmin email: {}", adminUser.map(u -> u.getAdminEmail()).orElse("not found"));

        if(adminUser.isPresent()) {
            log.info("authAdmin email: {}", adminUser.get().getAdminEmail());
            return adminUser.get();
        }
        return null;
    }
}
