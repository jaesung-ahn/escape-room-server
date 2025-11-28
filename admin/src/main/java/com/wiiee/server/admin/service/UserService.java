package com.wiiee.server.admin.service;

import com.wiiee.server.admin.form.UserListForm;
import com.wiiee.server.admin.repository.user.UserCustomRepository;
import com.wiiee.server.admin.repository.user.UserRepository;
import com.wiiee.server.common.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;

    @Transactional
    public Optional<User> saveUser() {
//        KakaoUser kakaoUser =  getKakaoUser(accessToken);
//        Optional<User> user = userRepository.findByEmail(kakaoUser.getEmail());
//        if(user.isEmpty()) {
//            user = Optional.of(userRepository.save(User.of(kakaoUser.getEmail(), kakaoUser.getNickname())));
//        }

        Optional<User> user = Optional.of(userRepository.save(
                User.of("test@test.com", "test normal user")));

        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserListForm> findAllForForm() {
        List<UserListForm> userListForms = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            UserListForm userListForm = new UserListForm();
            userListForm.setId(user.getId());
            userListForm.setCreatedAt(user.getCreatedAt());
            userListForm.setEmail(user.getEmail());
            userListForm.setNickname(user.getProfile().getNickname());
            userListForm.setMemberType(user.getProfile().getMemberType());
            userListForm.setUserStatus(user.getProfile().getUserStatus());
            userListForms.add(userListForm);
        });
        return userListForms;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 유저"));
    }

    public int countAndroidEventUser() {
        boolean isPushEvent = true;
        List<User> userList = userCustomRepository.findAllByEnablePush(isPushEvent);
        return userList.size();
    }
}
