# UserService 분해 설계

## 현재 상태 분석

**파일**: `api/src/main/java/com/wiiee/server/api/domain/user/UserService.java`
**현재 라인 수**: 235줄
**목표 라인 수**: UserService 100줄 이하

### 현재 메서드 목록

| 메서드 | 라인 | 책임 영역 |
|--------|------|----------|
| kakaoLogin() | 54-101 | 인증 |
| create() | 198-201 | 인증 (일반 회원가입) |
| login() | 204-209 | 인증 (일반 로그인) |
| logout() | 224-228 | 인증 |
| updateUser() | 104-115 | 프로필 |
| updateUserSignupEtc() | 142-155 | 프로필 |
| updateSettingUserInfo() | 161-164 | 프로필 |
| getMyPage() | 167-185 | 프로필 |
| updateUserPushInfo() | 133-136 | 프로필 |
| updateUserPushNoti() | 213-220 | 프로필 |
| checkNickname() | 231-233 | 프로필 |
| findById() | 118-120 | 핵심 조회 |
| findByEmail() | 123-125 | 핵심 조회 |
| findAdminById() | 128-130 | 핵심 조회 |
| getMyWbtiRecommends() | 188-195 | 추천 |

---

## 분해 설계

### 1. UserService (핵심 조회 유지)

**책임**: 사용자 기본 조회

**유지 메서드**:
- findById()
- findByEmail()
- findAdminById()

**의존성**:
- UserRepository
- AdminRepository

**예상 라인 수**: ~50줄

---

### 2. AuthService (신규)

**책임**: 인증/인가 (로그인, 회원가입, 로그아웃)

**메서드**:
```java
public class AuthService {

    // 카카오 로그인 (회원가입 포함)
    UserWithTokenModel kakaoLogin(UserSnsRequestDTO requestDTO);

    // 일반 회원가입
    UserWithTokenModel signup(UserPostRequestDTO dto);

    // 일반 로그인
    UserWithTokenModel login(UserLoginRequestDTO dto);

    // 로그아웃
    void logout(Long userId);
}
```

**의존성**:
- UserRepository
- UserCustomRepository
- JwtTokenProvider
- PasswordEncoder
- KakaoApiService (필요 시)

**예상 라인 수**: ~100줄

---

### 3. UserProfileService (신규)

**책임**: 사용자 프로필 관리

**메서드**:
```java
public class UserProfileService {

    // 마이페이지 조회
    UserMypageResponseDTO getMyPage(Long userId);

    // 사용자 정보 수정
    void updateUser(Long authUserId, Long targetUserId, UserUpdateRequest request);

    // 회원가입 추가 정보 입력
    UserSignupEtcResponseDTO updateUserSignupEtc(Long authUserId, UserSignupEtcRequestDTO.UserSignupEtcRequest requestDTO);

    // 설정에서 회원 정보 변경
    void updateSettingUserInfo(Long userId, updateSettingUserInfoRequestDTO requestDTO);

    // 푸시 토큰/OS 정보 업데이트
    void updateUserPushInfo(Long id, UserPushInfoRequestDTO dto);

    // 푸시 알림 설정 변경
    void updateUserPushNoti(Long userId, UserPushNotiRequestDTO requestDTO);

    // 닉네임 중복 체크
    Boolean checkNickname(String nickname);
}
```

**의존성**:
- UserRepository
- UserService (findById)
- ImageService
- WbtiService
- GatheringCustomRepository
- ModelMapper

**예상 라인 수**: ~100줄

---

### 4. UserRecommendationService (신규)

**책임**: 사용자 기반 추천 로직

**메서드**:
```java
public class UserRecommendationService {

    // WBTI 기반 추천 목록
    List<RecommendationModel> getMyWbtiRecommends(User user, List<ContentSimpleModel> contentSimpleModelList);
}
```

**의존성**:
- (없음, User 엔티티 활용)

**예상 라인 수**: ~30줄

---

## 의존성 그래프

```
┌─────────────────────────────────────────────────────────────┐
│                      UserController                          │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐    ┌────────────────┐    ┌────────────────┐
│  AuthService  │    │UserProfileServ │    │UserRecommend   │
│   (인증)       │    │   ice (프로필)  │    │ationService    │
└───────────────┘    └────────────────┘    └────────────────┘
        │                     │
        │                     │
        └──────────┬──────────┘
                   ▼
           ┌────────────────┐
           │  UserService   │
           │  (핵심 조회)    │
           └────────────────┘
                   │
                   ▼
           ┌────────────────┐
           │ UserRepository │
           └────────────────┘
```

---

## 구현 순서

1. **UserRecommendationService** 생성
   - 가장 단순한 로직
   - getMyWbtiRecommends() 이동

2. **AuthService** 생성
   - 인증 관련 로직 추출
   - kakaoLogin(), signup(), login(), logout() 이동

3. **UserProfileService** 생성
   - 프로필 관련 로직 추출
   - UserService 의존성 주입

4. **UserService 정리**
   - 핵심 조회 메서드만 유지
   - 분리된 서비스에서 필요 시 UserService 주입받아 사용

---

## 주의사항

- 기존 Controller 메서드 시그니처 변경 최소화
- 트랜잭션 경계 유지
- 기존 인수 테스트 통과 필수
- 순환 의존성 방지 (AuthService, UserProfileService → UserService 단방향)

---

## 파일 생성 위치

```
api/src/main/java/com/wiiee/server/api/domain/user/
├── UserService.java (기존, 수정)
├── AuthService.java (신규)
├── UserProfileService.java (신규)
└── UserRecommendationService.java (신규)
```

---

**작성일**: 2026-01-29
