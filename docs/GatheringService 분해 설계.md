# GatheringService 분해 설계

## 현재 상태 분석

**파일**: `api/src/main/java/com/wiiee/server/api/domain/gathering/GatheringService.java`
**현재 라인 수**: 545줄
**목표 라인 수**: GatheringService 150줄 이하

### 현재 메서드 목록

| 메서드 | 라인 | 책임 영역 |
|--------|------|----------|
| createNewGathering() | 66-81 | 핵심 CRUD |
| updateGathering() | 87-107 | 핵심 CRUD |
| getGatheringDetail() | 113-128 | 핵심 CRUD |
| findById() | 131-133 | 핵심 CRUD |
| getGatherings() | 136-153 | 핵심 CRUD |
| applyGathering() | 160-212 | 참가 신청 |
| sendGatheringRequestPush() | 217-257 | 푸시 알림 |
| getWaitingMember() | 260-284 | 멤버 조회 |
| getGatheringRequestDetail() | 287-308 | 참가 신청 |
| confirmGatheringRequest() | 317-360 | 참가 신청 |
| sendGatheringConfirmPush() | 365-397 | 푸시 알림 |
| cancelGatheringRequest() | 406-429 | 참가 신청 |
| getMyGatheringList() | 435-470 | 핵심 CRUD |
| completedGathering() | 476-480 | 상태 관리 |
| recruitingGathering() | 486-490 | 상태 관리 |
| checkGatheringHost() | 493-497 | 헬퍼 |
| cancelJoinGathering() | 503-523 | 멤버 관리 |
| deleteGathering() | 529-544 | 핵심 CRUD |

---

## 분해 설계

### 1. GatheringService (핵심 CRUD 유지)

**책임**: 동행모집 생성, 수정, 조회, 삭제, 상태 변경

**유지 메서드**:
- createNewGathering()
- updateGathering()
- getGatheringDetail()
- findById()
- getGatherings()
- getMyGatheringList()
- completedGathering()
- recruitingGathering()
- deleteGathering()

**의존성**:
- GatheringRepository
- UserService
- ContentService
- ImageService
- CommentService
- GatheringMemberService (신규)
- GatheringRequestService (신규)

**예상 라인 수**: ~150줄

---

### 2. GatheringRequestService (신규)

**책임**: 동행모집 참가 신청서 관리

**메서드**:
```java
public class GatheringRequestService {

    // 참가 신청
    GatheringRequest applyGathering(long gatheringId, long userId, String requestReason);

    // 참가서 상세 조회
    GatheringRequestDetailResDTO getGatheringRequestDetail(Long gatheringRequestId, Long userId);

    // 호스트 수락/거절
    GatheringRequest confirmGatheringRequest(GatheringConfirmReqDTO dto, Long userId);

    // 참가서 취소
    GatheringRequest cancelGatheringRequest(GatheringCancelReqDTO dto, long userId);

    // 승인된 참가서 조회 (내부용)
    GatheringRequest findApprovedGatheringRequest(Gathering gathering, User user);
}
```

**의존성**:
- GatheringRequestRepository
- GatheringRepository
- UserService
- GatheringNotificationService (푸시 알림)

**예상 라인 수**: ~150줄

---

### 3. GatheringMemberService (신규)

**책임**: 동행모집 멤버 관리

**메서드**:
```java
public class GatheringMemberService {

    // 대기 멤버 조회
    List<WaitingMemberModel> getWaitingMember(Gathering gathering);

    // 참여 취소
    void cancelJoinGathering(Long gatheringId, long userId);

    // 멤버 추가 (내부용)
    void addMember(Gathering gathering, User user);

    // 멤버 삭제 (내부용)
    void removeMember(Gathering gathering, GatheringMember member);

    // 멤버 전체 삭제 (내부용)
    void removeAllMembers(Gathering gathering);
}
```

**의존성**:
- GatheringRepository
- GatheringRequestRepository
- UserService
- ImageService

**예상 라인 수**: ~100줄

---

### 4. GatheringNotificationService (신규)

**책임**: 동행모집 관련 푸시 알림 (비동기)

**메서드**:
```java
@Service
public class GatheringNotificationService {

    @Async
    void sendGatheringRequestPush(Gathering gathering, GatheringRequest gatheringRequest);

    @Async
    void sendGatheringConfirmPush(Gathering gathering, GatheringRequest gatheringRequest,
                                   GatheringRequestStatus status);
}
```

**의존성**:
- OkHttpClient (또는 RestTemplate/WebClient로 교체 권장)
- Push API URL 설정

**예상 라인 수**: ~80줄

---

## 의존성 그래프

```
┌─────────────────────────────────────────────────────────────┐
│                     GatheringController                      │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐    ┌────────────────┐    ┌────────────────┐
│GatheringService│    │GatheringRequest│    │GatheringMember │
│   (핵심 CRUD)  │    │    Service     │    │    Service     │
└───────────────┘    └────────────────┘    └────────────────┘
        │                     │                     │
        │                     │                     │
        │                     ▼                     │
        │            ┌────────────────┐            │
        │            │  Gathering     │            │
        └───────────▶│  Notification  │◀───────────┘
                     │    Service     │
                     └────────────────┘
                             │
                             ▼
                     ┌────────────────┐
                     │   Push Server  │
                     └────────────────┘
```

---

## 구현 순서

1. **GatheringNotificationService** 생성
   - 푸시 알림 로직 추출
   - @Async 설정

2. **GatheringMemberService** 생성
   - 멤버 관련 로직 추출
   - getWaitingMember(), cancelJoinGathering() 이동

3. **GatheringRequestService** 생성
   - 참가 신청 관련 로직 추출
   - GatheringNotificationService 의존성 주입

4. **GatheringService 정리**
   - 분리된 서비스 의존성 주입
   - 기존 메서드에서 분리된 서비스 호출로 변경

---

## 주의사항

- 기존 Controller 메서드 시그니처 변경 최소화
- 트랜잭션 경계 유지
- 기존 인수 테스트 통과 필수
- 순환 의존성 방지

---

## 파일 생성 위치

```
api/src/main/java/com/wiiee/server/api/domain/gathering/
├── GatheringService.java (기존, 수정)
├── GatheringRequestService.java (신규)
├── GatheringMemberService.java (신규)
└── GatheringNotificationService.java (신규)
```

---

**작성일**: 2026-01-28
