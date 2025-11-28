package com.wiiee.server.push.repository.user;

import com.wiiee.server.common.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
