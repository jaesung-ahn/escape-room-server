# 엔티티 의존 관계 분석

> Wiiee 프로젝트의 엔티티 간 의존 관계를 분석하여 Acceptance 테스트 작성 순서를 정의합니다.

## 📊 엔티티 의존 관계 다이어그램

```
Level 0: 독립 엔티티 (의존성 없음)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
├── Image           # 이미지 저장소
├── User            # 일반 사용자
└── AdminUser       # 관리자 사용자

Level 1: 기본 엔티티
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
└── Company         # 방탈출 업체
    ├── AdminUser (nullable) - 담당 관리자
    └── imageIds: List<Long> - 업체 이미지 목록

Level 2: 컨텐츠
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
└── Content         # 방탈출 테마/방
    ├── Company (required) - 소속 업체
    └── imageIds: List<Long> - 컨텐츠 이미지 목록

Level 3: 컨텐츠 관련 기능
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
├── Review          # 사용자 리뷰
│   ├── User (required) - 작성자
│   ├── Content (required) - 대상 컨텐츠
│   └── imageIds: List<Long> - 리뷰 이미지
│
├── ContentPrice    # 가격 정보
│   └── Content (required)
│
├── ContentFavorite # 즐겨찾기
│   ├── User (required)
│   └── Content (required)
│
└── Discount        # 할인 정보
    └── Content (required)

Level 3: 소셜 기능
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
└── Gathering       # 동행 모집 게시글
    ├── Content (required) - 대상 컨텐츠
    ├── User (leader, required) - 방장
    └── ageGroupCodes: List<Integer> - 연령대 제한

Level 4: 동행 관련 기능
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
├── GatheringMember     # 동행 참여 멤버
│   ├── User (required)
│   └── Gathering (required)
│
├── GatheringRequest    # 동행 참가 요청
│   ├── User (required)
│   └── Gathering (required)
│
├── GatheringFavorite   # 동행 즐겨찾기
│   ├── User (required)
│   └── Gathering (required)
│
└── Comment             # 동행 댓글
    ├── User (required)
    └── Gathering (required)
```

## 🔑 주요 의존 관계 특징

### 1. Image 엔티티
- **관계 타입**: Loose Coupling (느슨한 결합)
- **저장 방식**: `List<Long> imageIds`로 ID만 저장
- **Foreign Key**: 없음
- **특징**: 실제 FK 관계가 없어 Image 생성 없이도 다른 엔티티 생성 가능

### 2. User 엔티티
- **참조하는 엔티티**: Review, Gathering (leader), GatheringMember, Comment 등
- **역할**: 시스템의 핵심 액터
- **필수 여부**: 대부분의 사용자 액션에서 필수

### 3. Company 엔티티
- **AdminUser 의존**: nullable (선택적)
- **Content와의 관계**: 1:N (한 업체는 여러 컨텐츠 보유)
- **특징**: AdminUser 없이도 생성 가능

### 4. Content 엔티티
- **중심 엔티티**: Review, ContentPrice, Discount, Gathering의 기준점
- **Company 의존**: 필수
- **역할**: 방탈출 테마/방 정보

### 5. Gathering 엔티티
- **소셜 기능의 중심**: Comment, GatheringMember 등이 의존
- **Content 의존**: 특정 방탈출 컨텐츠에 대한 동행 모집
- **User 의존**: 방장 (leader) 필수

## 📋 엔티티별 필수 의존성 요약

| 엔티티 | 필수 의존성 | 선택적 의존성 |
|--------|------------|--------------|
| Image | - | - |
| User | - | - |
| AdminUser | - | - |
| Company | - | AdminUser, Image IDs |
| Content | Company | Image IDs |
| Review | User, Content | Image IDs |
| ContentPrice | Content | - |
| ContentFavorite | User, Content | - |
| Discount | Content | - |
| Gathering | User, Content | - |
| GatheringMember | User, Gathering | - |
| GatheringRequest | User, Gathering | - |
| GatheringFavorite | User, Gathering | - |
| Comment | User, Gathering | - |

## 📈 테스트 진행 현황

**전체 진행률**: 7/14 완료 (50%)

- ✅ **완료** (7): Image, User, Company, Content, Review, Gathering, Comment
- ⬜ **미완료** (7): AdminUser (API 없음), ContentPrice (API 없음), Discount (API 없음), GatheringMember, GatheringRequest, GatheringFavorite, ContentFavorite (ContentAcceptanceTest에 포함됨)

**최근 업데이트**: 2025-12-19 - Comment 테스트 완료

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
11. ⬜ GatheringMember   - 멤버 가입/탈퇴
    └─ Depends: User, Gathering

12. ⬜ GatheringRequest  - 참가 요청/승인
    └─ Depends: User, Gathering

13. ✅ Comment           - 댓글 작성/수정/삭제 (14개 테스트)
    └─ Depends: User, Gathering
    └─ Test: CommentAcceptanceTest
       ├─ 기본 CRUD (등록/조회/수정/삭제)
       ├─ 대댓글 계층 구조
       ├─ 권한 검증 (본인만 수정/삭제)
       ├─ Soft Delete 동작 확인
       └─ 완전한 예외 처리 (validation, 존재 여부 등)

14. ⬜ GatheringFavorite - 동행 즐겨찾기
    └─ Depends: User, Gathering
```

---

## 📝 완료된 테스트 상세 정보

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
