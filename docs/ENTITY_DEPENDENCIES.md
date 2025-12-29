# 엔티티 의존 관계 분석

> Wiiee 프로젝트의 엔티티 간 의존 관계를 분석합니다.

## 엔티티 의존 관계 다이어그램

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

## 주요 의존 관계 특징

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

## 엔티티별 필수 의존성 요약

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
