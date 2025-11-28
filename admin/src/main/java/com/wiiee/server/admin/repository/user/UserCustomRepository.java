package com.wiiee.server.admin.repository.user;

import com.wiiee.server.common.domain.user.User;

import java.util.List;

public interface UserCustomRepository {
    List<User> findAllByEnablePush(boolean eventCond);
}
