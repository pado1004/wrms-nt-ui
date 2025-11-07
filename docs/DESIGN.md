# 상담 관리 시스템 - 이관 및 에스컬레이션 설계

## 1. 개요

상담사가 상담을 등록하고, 미처리 시 다른 상담사에게 이관하거나, 최종적으로 관리자에게 에스컬레이션되는 워크플로우를 지원하는 시스템 설계.

---

## 2. 핵심 개념

### 2.1 상담 생명주기 (Counseling Lifecycle)

```
[등록] → [할당] → [처리중] → [완료]
           ↓         ↓
        [이관]   [에스컬레이션]
```

### 2.2 역할 (Roles)

- **상담사 (COUNSELOR)**: 일반 상담 처리
- **시니어 상담사 (SENIOR_COUNSELOR)**: 이관받은 상담 처리
- **관리자 (MANAGER)**: 에스컬레이션된 상담 처리
- **시스템 관리자 (ADMIN)**: 전체 시스템 관리

### 2.3 처리 시한 (SLA - Service Level Agreement)

| 우선순위 | 1차 응답 시간 | 최대 처리 시간 | 에스컬레이션 기준 |
|---------|--------------|---------------|------------------|
| 긴급     | 1시간        | 4시간         | 4시간 초과       |
| 높음     | 4시간        | 24시간        | 24시간 초과      |
| 보통     | 24시간       | 72시간        | 72시간 초과      |
| 낮음     | 72시간       | 1주일         | 1주일 초과       |

---

## 3. 상담 상태 (Counseling Status)

### 3.1 상태 정의

```java
public enum CounselingStatus {
    REGISTERED,        // 등록됨 (초기 상태)
    ASSIGNED,          // 할당됨 (상담사에게 배정)
    IN_PROGRESS,       // 처리중
    PENDING_INFO,      // 고객 정보 대기중 (고객 회신 대기)
    TRANSFERRED,       // 이관됨 (다른 상담사에게)
    ESCALATED,         // 에스컬레이션됨 (관리자에게)
    RESOLVED,          // 해결됨
    CLOSED,            // 종료됨
    CANCELLED          // 취소됨
}
```

### 3.2 상태 전이 다이어그램

```
                    [REGISTERED]
                         ↓
                    [ASSIGNED]
                         ↓
                  [IN_PROGRESS] ←──────┐
                    ↓    ↓    ↓         │
        ┌──────────┼────┼────┼─────┐   │
        ↓          ↓    ↓    ↓     ↓   │
[PENDING_INFO] [TRANSFERRED] [ESCALATED] │
        ↓                ↓         ↓     │
        └────────────────┴─────────┴─────┘
                         ↓
                    [RESOLVED]
                         ↓
                    [CLOSED]
```

---

## 4. 이관 (Transfer) 기능

### 4.1 이관 사유

```java
public enum TransferReason {
    WORKLOAD_DISTRIBUTION,  // 업무 분산
    SPECIALIST_REQUIRED,    // 전문가 필요
    UNAVAILABLE,            // 담당자 부재
    CUSTOMER_REQUEST,       // 고객 요청
    OTHER                   // 기타
}
```

### 4.2 이관 프로세스

```
1. 상담사 A가 이관 요청
   - 이관 사유 선택
   - 목표 상담사 선택 (또는 자동 할당)
   - 이관 메모 작성

2. 시스템 처리
   - 상담 상태 → TRANSFERRED
   - 상담사 변경 (A → B)
   - 이관 이력 기록
   - 알림 발송 (상담사 B에게)

3. 상담사 B가 인수
   - 상담 상태 → ASSIGNED
   - 처리 시작 → IN_PROGRESS
```

### 4.3 이관 횟수 제한

- 최대 이관 횟수: **3회**
- 3회 초과 시 자동으로 관리자에게 에스컬레이션

---

## 5. 에스컬레이션 (Escalation) 기능

### 5.1 에스컬레이션 조건

#### A. 자동 에스컬레이션 (System-triggered)

1. **시간 초과**
   - 처리 시한(SLA) 초과
   - 예: 긴급 상담이 4시간 초과

2. **이관 횟수 초과**
   - 3회 이상 이관된 상담

3. **고객 재문의**
   - 같은 고객이 같은 이슈로 3회 이상 상담 등록

#### B. 수동 에스컬레이션 (User-triggered)

1. **복잡한 이슈**
   - 상담사가 처리 불가 판단

2. **정책 결정 필요**
   - 환불, 보상 등 권한 필요

3. **VIP 고객**
   - 중요 고객 상담

### 5.2 에스컬레이션 레벨

```
Level 1: 일반 상담사 (COUNSELOR)
   ↓
Level 2: 시니어 상담사 (SENIOR_COUNSELOR)
   ↓
Level 3: 팀 관리자 (MANAGER)
   ↓
Level 4: 부서 책임자 (DIRECTOR)
```

### 5.3 에스컬레이션 프로세스

```
1. 에스컬레이션 트리거
   - 자동: 시스템이 조건 감지
   - 수동: 상담사가 버튼 클릭

2. 에스컬레이션 정보 수집
   - 에스컬레이션 사유
   - 현재까지 처리 내용
   - 예상 해결 방안
   - 우선순위 재평가

3. 시스템 처리
   - 상담 상태 → ESCALATED
   - 에스컬레이션 레벨 증가
   - 담당자 변경 (관리자 할당)
   - 우선순위 자동 상향 (보통 → 높음)
   - 에스컬레이션 이력 기록
   - 알림 발송 (관리자에게)
   - 원담당자에게도 알림 (진행상황 추적용)

4. 관리자 처리
   - 에스컬레이션된 상담 검토
   - 처리 또는 추가 에스컬레이션
   - 해결 시 → RESOLVED
```

---

## 6. 데이터 모델 설계

### 6.1 User (사용자/직원)

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL,  -- COUNSELOR, SENIOR_COUNSELOR, MANAGER, ADMIN
    department VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    max_concurrent_cases INT DEFAULT 10,  -- 동시 처리 가능한 최대 상담 수
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 6.2 Counseling (상담) - 확장

```sql
CREATE TABLE counseling (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    counseling_type VARCHAR(50),

    -- 상태 및 우선순위
    status VARCHAR(20) DEFAULT 'REGISTERED',
    priority VARCHAR(20) DEFAULT 'NORMAL',  -- URGENT, HIGH, NORMAL, LOW

    -- 담당자 정보
    counselor_id BIGINT,  -- 현재 담당 상담사
    assigned_by BIGINT,   -- 할당한 사람
    original_counselor_id BIGINT,  -- 최초 담당 상담사

    -- 에스컬레이션 정보
    escalation_level INT DEFAULT 0,  -- 0: 일반, 1: 시니어, 2: 관리자, 3: 임원
    escalated_at TIMESTAMP,
    escalated_by BIGINT,
    escalation_reason TEXT,

    -- 이관 정보
    transfer_count INT DEFAULT 0,  -- 이관 횟수
    last_transferred_at TIMESTAMP,

    -- SLA 정보
    sla_due_date TIMESTAMP,  -- 처리 기한
    first_response_at TIMESTAMP,  -- 최초 응답 시간
    resolved_at TIMESTAMP,  -- 해결 시간

    -- 내용
    content TEXT,
    resolution TEXT,  -- 해결 내용

    -- 메타데이터
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (counselor_id) REFERENCES user(id),
    FOREIGN KEY (assigned_by) REFERENCES user(id),
    FOREIGN KEY (original_counselor_id) REFERENCES user(id),
    FOREIGN KEY (escalated_by) REFERENCES user(id)
);
```

### 6.3 CounselingHistory (상담 이력)

```sql
CREATE TABLE counseling_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    counseling_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,  -- CREATED, ASSIGNED, STATUS_CHANGED, TRANSFERRED, ESCALATED, COMMENTED, RESOLVED, CLOSED

    -- 액션 정보
    from_status VARCHAR(20),
    to_status VARCHAR(20),
    from_user_id BIGINT,
    to_user_id BIGINT,

    -- 상세 정보
    comment TEXT,
    reason VARCHAR(50),  -- 이관/에스컬레이션 사유

    -- 메타데이터
    performed_by BIGINT NOT NULL,  -- 액션을 수행한 사용자
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (counseling_id) REFERENCES counseling(id),
    FOREIGN KEY (from_user_id) REFERENCES user(id),
    FOREIGN KEY (to_user_id) REFERENCES user(id),
    FOREIGN KEY (performed_by) REFERENCES user(id)
);
```

### 6.4 EscalationRule (에스컬레이션 규칙)

```sql
CREATE TABLE escalation_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,

    -- 트리거 조건
    trigger_type VARCHAR(50) NOT NULL,  -- TIME_EXCEEDED, TRANSFER_LIMIT, CUSTOMER_REOPEN
    priority VARCHAR(20),  -- 어떤 우선순위에 적용할지

    -- 시간 기반 규칙
    time_threshold_hours INT,  -- 몇 시간 초과 시

    -- 이관 횟수 기반 규칙
    transfer_limit INT,  -- 몇 회 이관 초과 시

    -- 에스컬레이션 대상
    escalate_to_level INT,  -- 몇 레벨로 에스컬레이션
    escalate_to_role VARCHAR(50),  -- 어떤 역할로 에스컬레이션

    -- 활성화 여부
    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 6.5 Notification (알림)

```sql
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    counseling_id BIGINT,

    type VARCHAR(50) NOT NULL,  -- ASSIGNED, TRANSFERRED, ESCALATED, OVERDUE, RESOLVED
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,

    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (counseling_id) REFERENCES counseling(id)
);
```

---

## 7. 주요 비즈니스 로직

### 7.1 상담 등록 시

```java
1. 상담 정보 입력
2. 우선순위 결정 (상담 유형 + 고객 이력 기반)
3. SLA 기한 계산
   - 긴급: 현재 시간 + 4시간
   - 높음: 현재 시간 + 24시간
   - 보통: 현재 시간 + 72시간
4. 상담사 자동 할당 (업무량 기반)
5. 알림 발송
6. 이력 기록: CREATED
```

### 7.2 상담 이관 시

```java
1. 이관 가능 여부 확인
   - 이관 횟수 < 3회
   - 대상 상담사의 업무량 확인

2. 이관 실행
   - transfer_count++
   - counselor_id 변경
   - status → TRANSFERRED
   - last_transferred_at 기록

3. 이관 횟수 확인
   - IF transfer_count >= 3
   - THEN 자동 에스컬레이션

4. 알림 발송
   - 대상 상담사: "새 상담이 할당되었습니다"
   - 이관한 상담사: "이관이 완료되었습니다"

5. 이력 기록: TRANSFERRED
```

### 7.3 자동 에스컬레이션 시

```java
1. 조건 체크 (배치 작업, 매 1시간마다)
   FOR EACH counseling IN active_counselings:
       IF (sla_due_date < NOW() AND status != RESOLVED):
           trigger_escalation(counseling, "SLA_EXCEEDED")

       IF (transfer_count >= 3):
           trigger_escalation(counseling, "TRANSFER_LIMIT")

2. 에스컬레이션 실행
   - escalation_level++
   - 관리자 자동 할당 (가용한 관리자 중)
   - status → ESCALATED
   - priority 자동 상향 (NORMAL → HIGH)
   - escalated_at, escalated_by 기록

3. 알림 발송
   - 관리자: "긴급 상담이 에스컬레이션되었습니다"
   - 원담당자: "담당 상담이 에스컬레이션되었습니다"

4. 이력 기록: ESCALATED
```

### 7.4 수동 에스컬레이션 시

```java
1. 상담사가 에스컬레이션 요청
   - 에스컬레이션 사유 입력
   - 현재까지 처리 내용 요약
   - 제안 해결 방안 (선택)

2. 에스컬레이션 실행
   - 자동 에스컬레이션과 동일
   - escalation_reason 추가 기록

3. 알림 및 이력 기록
```

---

## 8. 화면 설계

### 8.1 상담 목록 화면 (개선)

```
┌─────────────────────────────────────────────────────────┐
│  상담 목록                                     [내 상담만] │
├─────────────────────────────────────────────────────────┤
│  검색: [           ]  상태: [전체 ▼]  우선순위: [전체 ▼]│
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │ID │고객명│상담유형│상태│우선순위│담당자│기한│⚠️ │   │ │
│  ├────────────────────────────────────────────────────┤ │
│  │1  │김철수│제품문의│처리중│긴급 🔴│나   │1시간│⚠️│ │
│  │2  │이영희│불만접수│에스컬│높음 🟠│박팀장│-   │  │ │
│  │3  │박민수│기술지원│완료  │보통   │나   │-   │  │ │
│  │4  │최민지│환불요청│이관됨│높음 🟠│김대리│3시간│  │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  [+ 상담 등록]                                           │
└─────────────────────────────────────────────────────────┘

범례:
🔴 긴급 (4시간 이내)
🟠 높음 (24시간 이내)
⚠️ SLA 초과 경고
```

### 8.2 상담 상세 화면 (신규)

```
┌─────────────────────────────────────────────────────────┐
│  상담 #1234                            [이관] [에스컬레이션] │
├─────────────────────────────────────────────────────────┤
│  고객명: 김철수                    우선순위: 긴급 🔴       │
│  연락처: 010-1234-5678            상태: 처리중            │
│  상담유형: 제품문의                                       │
│  담당자: 나 (홍길동)              SLA 기한: 1시간 남음 ⚠️  │
│  등록일: 2025-11-07 14:30                                │
│  ────────────────────────────────────────────────────── │
│  상담 내용:                                               │
│  신제품 ABC에 대한 문의입니다.                            │
│  가격과 재고 확인이 필요합니다.                           │
│  ────────────────────────────────────────────────────── │
│  처리 이력:                                               │
│  ┌──────────────────────────────────────────────────┐  │
│  │ 2025-11-07 14:30 홍길동 - 상담 등록              │  │
│  │ 2025-11-07 14:35 시스템 - 홍길동에게 할당        │  │
│  │ 2025-11-07 14:40 홍길동 - 처리 시작              │  │
│  │ 2025-11-07 15:00 홍길동 - 메모: 재고 확인중      │  │
│  └──────────────────────────────────────────────────┘  │
│                                                          │
│  처리 메모 추가:                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │                                                   │  │
│  └──────────────────────────────────────────────────┘  │
│  [메모 추가]  [상태 변경]                                │
│                                                          │
│  [해결 완료]  [취소]                                      │
└─────────────────────────────────────────────────────────┘
```

### 8.3 이관 Dialog

```
┌─────────────────────────────────────┐
│  상담 이관                           │
├─────────────────────────────────────┤
│  현재 담당자: 홍길동                 │
│  이관 대상: [김철수 ▼]              │
│              ┌────────────────────┐ │
│              │ 김철수 (3/10 건)   │ │
│              │ 이영희 (5/10 건)   │ │
│              │ 박민수 (7/10 건)   │ │
│              └────────────────────┘ │
│                                     │
│  이관 사유: [전문가 필요 ▼]         │
│                                     │
│  이관 메모:                          │
│  ┌─────────────────────────────┐   │
│  │기술적인 부분이 필요합니다.   │   │
│  │제품 담당자 확인 필요.        │   │
│  └─────────────────────────────┘   │
│                                     │
│  ⚠️ 경고: 이미 2회 이관되었습니다.  │
│  3회 초과 시 자동 에스컬레이션됩니다.│
│                                     │
│     [이관하기]  [취소]              │
└─────────────────────────────────────┘
```

### 8.4 에스컬레이션 Dialog

```
┌─────────────────────────────────────┐
│  에스컬레이션                        │
├─────────────────────────────────────┤
│  에스컬레이션 레벨:                  │
│  ● Level 1 → Level 2 (팀 관리자)    │
│  ○ Level 1 → Level 3 (부서 책임자)  │
│                                     │
│  에스컬레이션 사유: [복잡한 이슈 ▼] │
│                                     │
│  현재까지 처리 내용:                 │
│  ┌─────────────────────────────┐   │
│  │고객이 요청한 환불 금액이     │   │
│  │정책 범위를 초과합니다.       │   │
│  │관리자 승인이 필요합니다.     │   │
│  └─────────────────────────────┘   │
│                                     │
│  제안 해결 방안:                     │
│  ┌─────────────────────────────┐   │
│  │부분 환불로 타협 제안         │   │
│  │                              │   │
│  └─────────────────────────────┘   │
│                                     │
│  ⚠️ 에스컬레이션 시 우선순위가      │
│  자동으로 '높음'으로 변경됩니다.     │
│                                     │
│     [에스컬레이션]  [취소]          │
└─────────────────────────────────────┘
```

### 8.5 관리자 대시보드

```
┌─────────────────────────────────────────────────────────┐
│  관리자 대시보드                                   [새로고침]│
├─────────────────────────────────────────────────────────┤
│  ┌───────────────┐ ┌───────────────┐ ┌──────────────┐  │
│  │ 에스컬레이션   │ │ SLA 초과 위험  │ │ 처리중 상담  │  │
│  │      5건      │ │      3건      │ │     12건     │  │
│  │      🔴       │ │      ⚠️       │ │              │  │
│  └───────────────┘ └───────────────┘ └──────────────┘  │
│                                                          │
│  긴급 처리 필요 상담:                                     │
│  ┌────────────────────────────────────────────────────┐ │
│  │#123 │김철수│환불요청│에스컬레이션│1시간 전│홍길동  │ │
│  │#456 │이영희│불만접수│SLA 초과    │2시간 전│김철수  │ │
│  │#789 │박민수│기술지원│이관 3회    │5시간 전│이영희  │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  팀별 처리 현황:                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │팀명    │담당 상담│완료│처리중│지연│평균 처리시간   │ │
│  ├────────────────────────────────────────────────────┤ │
│  │영업팀  │   50   │ 35│  10 │ 5 │    2.5시간    │ │
│  │기술팀  │   30   │ 25│   3 │ 2 │    1.8시간    │ │
│  │CS팀    │   40   │ 30│   8 │ 2 │    3.2시간    │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

## 9. 구현 순서

### Phase 1: 기본 사용자 및 역할 관리
- [ ] User 엔티티 및 Repository
- [ ] 사용자 등록/조회 기능
- [ ] 역할(Role) 기반 접근 제어

### Phase 2: 상담 기능 확장
- [ ] Counseling 엔티티 확장 (우선순위, SLA, 에스컬레이션 필드)
- [ ] CounselingHistory 엔티티 및 Repository
- [ ] 상담 상세 화면
- [ ] 상태 변경 기능

### Phase 3: 이관 기능
- [ ] 이관 비즈니스 로직
- [ ] 이관 Dialog UI
- [ ] 이관 이력 추적
- [ ] 이관 횟수 제한 로직

### Phase 4: 에스컬레이션 기능
- [ ] 에스컬레이션 규칙(Rule) 엔티티
- [ ] 수동 에스컬레이션 UI 및 로직
- [ ] 자동 에스컬레이션 배치 작업
- [ ] 에스컬레이션 레벨 관리

### Phase 5: 알림 및 대시보드
- [ ] Notification 엔티티 및 기능
- [ ] 실시간 알림 (WebSocket 또는 Server-Sent Events)
- [ ] 관리자 대시보드
- [ ] SLA 모니터링

### Phase 6: 고급 기능
- [ ] SLA 정책 설정 UI
- [ ] 상담 통계 및 리포트
- [ ] 자동 할당 알고리즘 개선
- [ ] 고객 이력 기반 우선순위 자동 조정

---

## 10. 기술적 고려사항

### 10.1 자동 에스컬레이션 배치 작업

```java
@Scheduled(fixedRate = 3600000)  // 1시간마다
public void checkEscalationConditions() {
    // SLA 초과 체크
    // 이관 횟수 초과 체크
    // 자동 에스컬레이션 실행
}
```

### 10.2 실시간 알림

- Vaadin의 `@Push` 애노테이션 사용
- Server-Sent Events (SSE) 또는 WebSocket

### 10.3 동시성 처리

- 같은 상담을 여러 명이 동시에 처리하지 않도록
- Optimistic Locking (@Version 사용)

### 10.4 감사 로그 (Audit Log)

- 모든 중요 액션은 CounselingHistory에 기록
- 누가, 언제, 무엇을, 왜 했는지 추적 가능

---

## 11. 예시 시나리오

### 시나리오 1: 일반 상담 처리

```
1. 고객 김철수 전화 → 상담사 A가 상담 등록
2. 상담사 A에게 자동 할당 (상태: ASSIGNED)
3. 상담사 A가 처리 시작 (상태: IN_PROGRESS)
4. 30분 후 해결 완료 (상태: RESOLVED)
5. 시스템 자동 종료 (상태: CLOSED)
```

### 시나리오 2: 이관 후 처리

```
1. 고객 이영희 불만 접수 → 상담사 A에게 할당
2. 상담사 A 확인: 기술적 문제로 판단
3. 상담사 A → 기술팀 상담사 B에게 이관
   - 이관 사유: "전문가 필요"
   - transfer_count = 1
4. 상담사 B가 처리 완료
```

### 시나리오 3: 자동 에스컬레이션

```
1. 긴급 상담 등록 (SLA: 4시간)
2. 상담사 A에게 할당
3. 상담사 A가 처리 시작하나 복잡함
4. 상담사 A → 상담사 B에게 이관 (1회)
5. 상담사 B → 상담사 C에게 이관 (2회)
6. 상담사 C → 상담사 D에게 이관 (3회)
7. 시스템 자동 감지: 이관 3회 초과
8. 자동 에스컬레이션 → 팀 관리자에게
   - 알림: "상담 #123이 자동 에스컬레이션되었습니다"
   - 우선순위: 긴급 → 최긴급
```

### 시나리오 4: SLA 초과로 인한 에스컬레이션

```
1. 일반 상담 등록 (우선순위: 보통, SLA: 72시간)
2. 상담사 처리 지연
3. 72시간 경과
4. 배치 작업이 감지
5. 자동 에스컬레이션 → 관리자에게
   - 알림: "SLA 초과 상담이 있습니다"
   - 상태: ESCALATED
```

---

## 12. 다음 단계

이 설계를 기반으로 다음을 진행할 수 있습니다:

1. **설계 검토 및 피드백**
   - 추가/변경 필요한 개념 확인

2. **데이터베이스 스키마 구현**
   - 새로운 테이블 생성
   - 기존 테이블 확장

3. **도메인 모델 구현**
   - Entity, Repository, Service 작성

4. **UI 구현**
   - 화면별 순차 구현

5. **테스트 및 검증**
   - 단위 테스트
   - 통합 테스트

---

이 설계에 대해 피드백이나 수정이 필요한 부분이 있으신가요?
