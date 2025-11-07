# WRMS NT UI - Counseling Management System

Spring Boot, Spring Data JDBC, Vaadin Flow를 기반으로 한 상담 관리 시스템입니다.

## 기술 스택

- **Spring Boot 3.2.5**: 백엔드 프레임워크
- **Spring Data JDBC**: 데이터 액세스 레이어
- **Vaadin Flow 24.3**: UI 프레임워크
- **H2 Database**: 개발용 인메모리 데이터베이스
- **Java 17**: 프로그래밍 언어
- **Maven**: 빌드 도구

## 프로젝트 구조

```
wrms-nt-ui/
├── src/main/java/com/wrms/nt/
│   ├── WrmsNtApplication.java        # 메인 애플리케이션
│   ├── domain/                       # 도메인 모듈
│   │   ├── counseling/               # 상담 모듈
│   │   │   ├── entity/               # 엔티티
│   │   │   ├── repository/           # 리포지토리
│   │   │   └── service/              # 서비스
│   │   ├── customer/                 # 고객 모듈
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── common/                   # 공통 모듈
│   └── views/                        # Vaadin 뷰
│       ├── MainLayout.java           # 메인 레이아웃
│       ├── counseling/               # 상담 화면
│       ├── customer/                 # 고객 화면
│       └── dashboard/                # 대시보드
└── src/main/resources/
    ├── application.properties        # 설정 파일
    └── db/migration/                 # 데이터베이스 스크립트
        ├── schema.sql                # 스키마 정의
        └── data.sql                  # 초기 데이터
```

## 시작하기

### 사전 요구사항

- JDK 17 이상
- Maven 3.6 이상

### 실행 방법

1. 프로젝트 클론 또는 다운로드

2. 프로젝트 루트 디렉토리에서 실행:
```bash
mvn spring-boot:run
```

3. 브라우저에서 접속:
```
http://localhost:8080
```

4. H2 데이터베이스 콘솔 (개발용):
```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:file:./data/wrmsnt;AUTO_SERVER=TRUE`
- Username: `sa`
- Password: (비어있음)

### 빌드

```bash
# 개발 모드
mvn clean install

# 프로덕션 모드
mvn clean install -Pproduction
```

## 주요 기능

### 현재 구현된 기능

- 대시보드 화면
- 상담 목록 조회
- 고객 목록 조회
- 검색 기능
- 기본 레이아웃 및 네비게이션

### 향후 구현 예정 기능

- 상담 등록/수정/삭제
- 고객 등록/수정/삭제
- 상담 상세 화면
- 파일 첨부 기능
- 통계 및 리포트
- 사용자 인증 및 권한 관리
- 알림 기능

## 데이터베이스

### 테이블 구조

#### counseling (상담)
- id: 상담 ID
- customer_name: 고객명
- contact: 연락처
- counseling_type: 상담 유형
- status: 상태 (PENDING, IN_PROGRESS, COMPLETED)
- counselor_id: 상담사 ID
- content: 상담 내용
- created_at: 등록일
- updated_at: 수정일

#### customer (고객)
- id: 고객 ID
- name: 고객명
- email: 이메일
- phone: 전화번호
- address: 주소
- created_at: 등록일
- updated_at: 수정일

## 개발 가이드

### 새로운 모듈 추가하기

1. `domain/` 아래에 새 모듈 디렉토리 생성
2. `entity/`, `repository/`, `service/` 패키지 생성
3. 엔티티, 리포지토리, 서비스 클래스 작성
4. `views/` 아래에 해당 모듈의 뷰 생성

### Vaadin 컴포넌트

- Grid: 데이터 테이블 표시
- Form: 입력 폼
- Dialog: 모달 창
- Button: 버튼
- TextField: 텍스트 입력

## 라이선스

이 프로젝트는 학습 및 개발 목적으로 생성되었습니다.

## 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 등록해주세요.
