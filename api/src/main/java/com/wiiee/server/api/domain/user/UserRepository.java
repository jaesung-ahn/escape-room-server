package com.wiiee.server.api.domain.user;

import com.wiiee.server.common.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByProfile_Nickname(String nickname);
}
