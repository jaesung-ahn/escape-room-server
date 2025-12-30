package com.wiiee.server.api.domain.security;

import com.wiiee.server.common.domain.user.Password;
import com.wiiee.server.common.domain.user.User;
import com.wiiee.server.common.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SecurityUser 단위 테스트")
class SecurityUserTest {

    @Test
    @DisplayName("USER 역할을 가진 사용자의 권한을 반환한다")
    void getAuthorities_user() {
        // given
        User user = User.of("user@example.com", "testUser");
        SecurityUser securityUser = new SecurityUser(user);

        // when
        Collection<? extends GrantedAuthority> authorities = securityUser.getAuthorities();

        // then
        assertThat(authorities).hasSize(1);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("ADMIN 역할을 가진 사용자의 권한을 반환한다")
    void getAuthorities_admin() {
        // given
        User adminUser = User.ofWithRole("admin@example.com", "adminUser", null, UserRole.ADMIN);
        SecurityUser securityUser = new SecurityUser(adminUser);

        // when
        Collection<? extends GrantedAuthority> authorities = securityUser.getAuthorities();

        // then
        assertThat(authorities).hasSize(1);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    @DisplayName("getUsername은 사용자의 이메일을 반환한다")
    void getUsername() {
        // given
        String email = "test@example.com";
        User user = User.of(email, "testUser");
        SecurityUser securityUser = new SecurityUser(user);

        // when & then
        assertThat(securityUser.getUsername()).isEqualTo(email);
    }

    @Test
    @DisplayName("계정 상태 관련 메서드들은 true를 반환한다")
    void accountStatus() {
        // given
        User user = User.of("test@example.com", "testUser");
        SecurityUser securityUser = new SecurityUser(user);

        // when & then
        assertThat(securityUser.isAccountNonExpired()).isTrue();
        assertThat(securityUser.isAccountNonLocked()).isTrue();
        assertThat(securityUser.isCredentialsNonExpired()).isTrue();
        assertThat(securityUser.isEnabled()).isTrue();
    }
}
