package com.wiiee.server.push.service;

import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.push.repository.user.UserCustomRepositoryImpl;
import com.wiiee.server.push.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserCustomRepositoryImpl userCustomRepository;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> findAllForEvent() {
        return userCustomRepository.findAllForEvent();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
    }
}
