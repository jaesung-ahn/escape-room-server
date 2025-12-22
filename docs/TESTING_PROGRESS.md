# Acceptance 테스트 진행 현황

> Wiiee 프로젝트의 Acceptance 테스트 작성 진행 상황 및 상세 정보

## 📈 테스트 진행 현황

**전체 진행률**: 10/14 완료 (71%)

- ✅ **완료** (10): Image, User, Company, Content, Review, Gathering, Comment, GatheringRequest, GatheringFavorite, ContentFavorite
- ⬜ **미완료** (4): AdminUser (API 없음), ContentPrice (API 없음), Discount (API 없음), GatheringMember
- ⚠️ **참고**: GatheringMember는 참가 신청 승인 시 자동 생성되므로 GatheringRequestAcceptanceTest에서 간접적으로 테스트됨

**최근 업데이트**: 2025-12-22 - GatheringRequest 권한 검증 추가 완료

---

## 🎯 Acceptance 테스트 작성 권장 순서

### Phase 1: 기반 엔티티 (의존성 없음)
```
1. ✅ Image          - 이미지 업로드/조회 (선택적)
2. ✅ User           - 회원가입/로그인/프로필
3. ⬜ AdminUser      - 관리자 인증 (API 없음, 스킵)
```

### Phase 2: 업체 및 컨텐츠
```
4. ✅ Company        - 업체 CRUD
   └─ Depends: (optional) AdminUser, Image IDs

5. ✅ Content        - 테마/방 CRUD
   └─ Depends: Company (required)
```

### Phase 3: 컨텐츠 부가 기능
```
6. ⬜ ContentPrice   - 가격 정보 CRUD (API 없음, 스킵)
   └─ Depends: Content

7. ✅ Review         - 리뷰 작성/조회/수정
   └─ Depends: User, Content

8. ✅ ContentFavorite - 즐겨찾기 추가/제거 (ContentAcceptanceTest에 포함)
   └─ Depends: User, Content

9. ⬜ Discount       - 할인 정보 CRUD (API 없음, 스킵)
   └─ Depends: Content
```

### Phase 4: 동행 모집
```
10. ✅ Gathering     - 동행 모집 게시글 CRUD
    └─ Depends: User, Content
```

### Phase 5: 동행 부가 기능
```
11. ⚠️ GatheringMember   - 멤버 가입/탈퇴
    └─ Depends: User, Gathering
    └─ Note: 참가 신청 승인 시 자동 생성 (GatheringRequestAcceptanceTest에서 간접 테스트)

12. ✅ GatheringRequest  - 참가 요청/승인/거절/취소 (6개 테스트)
    └─ Depends: User, Gathering
    └─ Test: GatheringRequestAcceptanceTest
       ├─ 참가 신청 (승낙제 모집 방식)
       ├─ 참가서 상세 조회
       ├─ 참가서 수락 (호스트)
       ├─ 참가서 거절 (호스트)
       ├─ 참가서 취소 (신청자)
       └─ 인증 검증 (401/403)

13. ✅ Comment           - 댓글 작성/수정/삭제 (14개 테스트)
    └─ Depends: User, Gathering
    └─ Test: CommentAcceptanceTest
       ├─ 기본 CRUD (등록/조회/수정/삭제)
       ├─ 대댓글 계층 구조
       ├─ 권한 검증 (본인만 수정/삭제)
       ├─ Soft Delete 동작 확인
       └─ 완전한 예외 처리 (validation, 존재 여부 등)

14. ✅ GatheringFavorite - 동행 즐겨찾기 (GatheringAcceptanceTest에 포함)
    └─ Depends: User, Gathering
    └─ Test: GatheringAcceptanceTest
       ├─ 찜 등록
       └─ 찜 삭제
```

---

## 📝 완료된 테스트 상세 정보

### Gathering & GatheringRequest (동행 모집 & 참가 요청) - GatheringAcceptanceTest ✅

**파일 위치**: `api/src/test/java/com/wiiee/server/api/acceptance/gathering/GatheringAcceptanceTest.java`

**테스트 개수**: 18개

**API 엔드포인트**:

#### Gathering (동행 모집)
- `POST /api/gathering` - 동행 모집 등록
- `GET /api/gathering/{id}` - 동행 상세 조회
- `GET /api/gathering` - 동행 리스트 조회
- `PUT /api/gathering/{id}` - 동행 수정
- `DELETE /api/gathering/{id}` - 동행 삭제
- `GET /api/gathering/my-gatherings` - 내 동행 목록 조회
- `POST /api/gathering/favorite` - 동행 찜 등록
- `DELETE /api/gathering/favorite` - 동행 찜 삭제

#### GatheringRequest (참가 요청)
- `POST /api/gathering/apply` - 참가 신청
- `GET /api/gathering/requests/{requestId}` - 참가서 상세 조회
- `POST /api/gathering/confirm` - 참가서 수락/거절 (호스트만)
- `POST /api/gathering/cancel` - 참가서 취소 (신청자만)

**테스트 케이스**:

#### 1. Gathering CRUD (6개)
- ✅ `createGathering` - 동행 모집 등록
- ✅ `getGathering` - 동행 모집 상세 조회
- ✅ `getGatherings` - 동행 모집 리스트 조회
- ✅ `updateGathering` - 동행 모집 수정
- ✅ `deleteGathering` - 동행 모집 삭제
- ✅ `getMyGatherings` - 내 동행 목록 조회

#### 2. 참가 신청 (선착순 vs 승낙제) (2개)
- ✅ `applyGathering_firstCome` - 참가 신청 (선착순, 즉시 멤버 추가)
- ✅ `applyGathering_confirm` - 참가 신청 (승낙제, 참가서 생성)

#### 3. GatheringRequest 관리 (4개)
- ✅ `getGatheringRequestDetail` - 참가서 상세 조회
- ✅ `confirmGatheringRequest_approval` - 참가서 수락 (호스트)
- ✅ `confirmGatheringRequest_reject` - 참가서 거절 (호스트)
- ✅ `cancelGatheringRequest` - 참가서 취소 (신청자)

#### 4. Gathering 찜 기능 (2개)
- ✅ `addGatheringFavorite` - 동행 찜 등록
- ✅ `deleteGatheringFavorite` - 동행 찜 삭제

#### 5. 권한 검증 (2개)
- ✅ `getGatheringRequestDetail_forbidden` - 참가서 조회 권한 검증 (제3자 접근 차단)
- ✅ `confirmGatheringRequest_notHost` - 참가서 수락 권한 검증 (호스트만 가능)

#### 6. 예외 처리 (1개)
- ✅ `createGathering_unauthorized` - 인증 없이 등록 실패 (401/403)

**주요 특징**:
- **모집 방식 차이**:
  - `recruitTypeCode: 0` (승낙제) - GatheringRequest 생성 → 호스트 승인 필요
  - `recruitTypeCode: 1` (선착순) - 즉시 멤버로 추가
- **상태 관리**: `GatheringRequestStatus` 열거형
  - UNVERIFIED(0) - 호스트 확인 전
  - VERIFIED(1) - 호스트 확인 됨
  - APPROVAL(2) - 승인
  - REJECT(3) - 거절
  - CANCELED(4) - 요청자 취소(승인 전)
  - CANCELED_JOIN(5) - 요청자 참여 취소(승인 후)
- **테스트 통합**: Gathering과 GatheringRequest 테스트를 하나의 파일로 통합하여 중복 제거 (~200줄 감소)
- **권한 검증 완료**:
  - 참가서 조회: 호스트 또는 신청자만 가능 (에러 코드 8123)
  - 참가서 수락/거절: 호스트만 가능 (에러 코드 8124)

---

### Comment (댓글) - CommentAcceptanceTest ✅

**파일 위치**: `api/src/test/java/com/wiiee/server/api/acceptance/comment/CommentAcceptanceTest.java`

**테스트 개수**: 14개

**API 엔드포인트**:
- `POST /api/comment/gathering/{id}` - 댓글/대댓글 등록
- `GET /api/comment/gathering/{id}` - 댓글 목록 조회
- `PUT /api/comment/{id}` - 댓글 수정
- `DELETE /api/comment/{id}` - 댓글 삭제

**테스트 케이스**:

#### 1. 기본 CRUD (4개)
- ✅ `createComment` - 댓글 등록
- ✅ `getComments` - 댓글 목록 조회
- ✅ `updateComment` - 댓글 수정
- ✅ `deleteComment` - 댓글 삭제

#### 2. 대댓글 기능 (2개)
- ✅ `createReplyComment` - 대댓글 등록
- ✅ `getCommentsWithReplies` - 대댓글이 포함된 댓글 목록 조회
  - 부모 댓글의 `children` 배열 검증
  - `isParent` 플래그 확인

#### 3. 권한 검증 (2개)
- ✅ `updateComment_notOwner` - 다른 사용자의 댓글 수정 실패 (IllegalArgumentException)
- ✅ `deleteComment_notOwner` - 다른 사용자의 댓글 삭제 실패 (CustomException 7001)

#### 4. 예외 처리 (6개)
- ✅ `createComment_unauthorized` - 인증 없이 댓글 등록 실패 (401/403)
- ✅ `createComment_gatheringNotFound` - 존재하지 않는 동행에 댓글 등록 실패 (7002)
- ✅ `createComment_emptyMessage` - 빈 메시지로 댓글 등록 실패 (400, @NotBlank)
- ✅ `getComments_afterDelete` - 삭제된 댓글 조회 시 Soft Delete 확인 (deleted=true)
- ✅ `updateComment_notFound` - 존재하지 않는 댓글 수정 실패 (400/404)
- ✅ `deleteComment_notFound` - 존재하지 않는 댓글 삭제 실패 (400/404)

**서버 개선 사항**:
- ✅ `@Valid` 어노테이션 추가 → Request DTO validation 활성화
- ✅ Gathering 존재 여부 검증 로직 추가
- ✅ `ERROR_GATHERING_NOT_FOUND` 에러 코드 추가 (7002)

**특이사항**:
- Soft Delete 패턴 사용 (`deleted` 플래그, `deletedAt` 타임스탬프)
- 대댓글 계층 구조 (부모-자식 관계)
- Comment 엔티티에서 권한 검증 (`IllegalArgumentException`)
- CommentService에서 추가 권한 검증 (`CustomException`)

---
