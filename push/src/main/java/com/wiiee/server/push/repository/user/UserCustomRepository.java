package com.wiiee.server.push.repository.user;

import com.wiiee.server.common.domain.user.User;

import java.util.List;

public interface UserCustomRepository {
    List<User> findAllForEvent();
}
