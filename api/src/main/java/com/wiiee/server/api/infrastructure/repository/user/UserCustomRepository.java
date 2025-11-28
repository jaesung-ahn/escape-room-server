package com.wiiee.server.api.infrastructure.repository.user;

import com.wiiee.server.api.application.user.UserSnSRequest;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.wbti.Wbti;

import java.util.Optional;

public interface UserCustomRepository {

    Optional<User> findByUserGetRequestDTO(UserSnSRequest userSnSRequest);

    void saveWbti(Long userId, Wbti wbti);
}
