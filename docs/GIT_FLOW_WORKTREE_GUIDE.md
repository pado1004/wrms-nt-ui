# Git Flow와 Git Worktree 활용 가이드

## 목차
1. [개요](#개요)
2. [Git Flow 브랜치 전략](#git-flow-브랜치-전략)
3. [Git Worktree 소개](#git-worktree-소개)
4. [Git Flow + Worktree 통합 전략](#git-flow--worktree-통합-전략)
5. [초기 셋업](#초기-셋업)
6. [실무 워크플로우](#실무-워크플로우)
7. [자동화 스크립트](#자동화-스크립트)
8. [모범 사례](#모범-사례)
9. [문제 해결](#문제-해결)

---

## 개요

이 문서는 Git Flow 브랜치 전략과 Git Worktree를 결합하여 효율적인 개발 환경을 구축하는 방법을 안내합니다.

### 왜 Git Worktree인가?

**기존 방식의 문제점:**
- 브랜치 전환 시 작업 중인 변경사항 stash 필요
- IDE 인덱싱 재실행으로 인한 시간 소요
- 여러 브랜치에서 동시 작업 불가
- 긴급 hotfix 시 현재 작업 중단 필요

**Worktree의 장점:**
- ✅ 각 브랜치를 독립된 디렉토리로 관리
- ✅ 브랜치 간 전환 없이 동시 작업 가능
- ✅ IDE에서 여러 프로젝트 창 동시 열기
- ✅ 빌드/테스트를 병렬로 실행 가능
- ✅ Stash 관리 부담 제거

---

## Git Flow 브랜치 전략

### 브랜치 유형

Git Flow는 다음 5가지 브랜치 유형을 사용합니다:

```
main (또는 master)
  │
  ├── develop
  │     │
  │     ├── feature/feature-name
  │     │
  │     ├── release/version-number
  │     │
  │     └── hotfix/version-number
```

#### 1. **main (master)**
- 프로덕션 배포 가능한 상태만 유지
- 태그를 통한 버전 관리
- 직접 커밋 금지

#### 2. **develop**
- 다음 릴리스를 위한 통합 브랜치
- 모든 feature 브랜치의 병합 지점
- 안정적인 개발 상태 유지

#### 3. **feature/**
- 새로운 기능 개발
- `develop`에서 분기하여 `develop`으로 병합
- 브랜치명: `feature/user-authentication`, `feature/order-management`

#### 4. **release/**
- 릴리스 준비 및 버그 수정
- `develop`에서 분기하여 `main`과 `develop`으로 병합
- 브랜치명: `release/1.0.0`, `release/2.1.0`

#### 5. **hotfix/**
- 프로덕션 긴급 수정
- `main`에서 분기하여 `main`과 `develop`으로 병합
- 브랜치명: `hotfix/critical-bug-fix`, `hotfix/security-patch`

---

## Git Worktree 소개

### 기본 개념

Git Worktree는 하나의 Git 저장소에서 여러 작업 디렉토리를 관리할 수 있게 해주는 기능입니다.

```
프로젝트 루트/
├── .git/                    # 메인 저장소
├── src/                     # 메인 작업 디렉토리 (main/develop)
│
├── worktrees/               # Worktree 디렉토리
│   ├── feature-auth/        # feature/user-authentication 브랜치
│   ├── feature-order/       # feature/order-management 브랜치
│   ├── release-1.0.0/       # release/1.0.0 브랜치
│   └── hotfix-critical/     # hotfix/critical-bug-fix 브랜치
```

### 기본 명령어

```bash
# Worktree 목록 확인
git worktree list

# 새 Worktree 추가
git worktree add <path> <branch>

# 브랜치 생성과 함께 Worktree 추가
git worktree add -b <branch> <path> <base-branch>

# Worktree 제거
git worktree remove <path>
# 또는
git worktree prune

# Worktree 이동 (브랜치 변경)
cd <worktree-path>
git checkout <branch>
```

---

## Git Flow + Worktree 통합 전략

### 디렉토리 구조 권장사항

```
wrms-nt-ui/                          # 메인 저장소
├── .git/
├── src/                              # develop 브랜치 (메인 작업)
│
└── worktrees/                        # 모든 Worktree를 관리하는 디렉토리
    ├── feature/                      # Feature 브랜치 Worktree
    │   ├── user-authentication/
    │   ├── order-management/
    │   └── counseling-system/
    │
    ├── release/                      # Release 브랜치 Worktree
    │   ├── 1.0.0/
    │   └── 2.0.0/
    │
    └── hotfix/                       # Hotfix 브랜치 Worktree
        └── critical-bug-fix/
```

### 브랜치별 Worktree 전략

#### 1. **Feature 브랜치**
- 각 feature는 독립된 Worktree로 관리
- 여러 feature를 동시에 개발 가능
- `worktrees/feature/` 하위에 배치

#### 2. **Release 브랜치**
- 릴리스 준비 기간 동안만 Worktree 생성
- 릴리스 완료 후 제거
- `worktrees/release/` 하위에 배치

#### 3. **Hotfix 브랜치**
- 긴급 수정 시 즉시 Worktree 생성
- 수정 완료 후 제거
- `worktrees/hotfix/` 하위에 배치

#### 4. **Main/Develop 브랜치**
- 메인 저장소 디렉토리에서 직접 작업
- Worktree 생성 불필요

---

## 초기 셋업

원격 저장소에 `main` 브랜치만 있는 상황에서 Git Flow와 Git Worktree 환경을 처음 설정하는 방법을 안내합니다.

### 전제 조건

- 원격 저장소에 `main` 브랜치만 존재
- 로컬에 저장소가 아직 클론되지 않았거나, 클론만 되어 있는 상태
- Git 2.5 이상 버전 (Worktree 기능 지원)

### Step 1: 저장소 클론

```bash
# 원격 저장소 클론
cd /Users/pado/IdeaProjects
git clone <repository-url> wrms-nt-ui
cd wrms-nt-ui

# 현재 브랜치 확인 (main 브랜치에 있어야 함)
git branch -a
```

### Step 2: Develop 브랜치 생성 및 설정

```bash
# main 브랜치에서 develop 브랜치 생성
git checkout -b develop

# develop 브랜치를 원격 저장소에 푸시
git push -u origin develop

# 현재 develop 브랜치에 있음 (다음 단계를 위해 확인)
git branch
```

**참고:** 원격 저장소의 기본 브랜치를 `develop`으로 변경하려면 GitHub/GitLab 등의 웹 인터페이스에서 설정을 변경하세요.

### Step 3: Worktree 디렉토리 구조 생성

**중요:** `worktrees/` 디렉토리는 단순한 디렉토리 구조이므로 어떤 브랜치에서 만들어도 상관없습니다. 하지만 일관성을 위해 **develop 브랜치에서 생성**하는 것을 권장합니다.

```bash
# 현재 develop 브랜치에 있는지 확인
git branch
# * develop 이 표시되어야 함

# worktrees 디렉토리 및 하위 디렉토리 생성
mkdir -p worktrees/feature
mkdir -p worktrees/release
mkdir -p worktrees/hotfix

# 디렉토리 구조 확인
tree worktrees -L 2
# 또는
ls -la worktrees/
```

**참고:**
- `worktrees/` 디렉토리는 Git이 관리하는 디렉토리가 아니라 단순한 폴더 구조입니다.
- 실제 Worktree는 `git worktree add` 명령어로 생성되며, 이때 Git이 자동으로 관리합니다.
- `worktrees/` 디렉토리를 `.gitignore`에 추가하지 않습니다 (Git이 Worktree를 추적해야 함).
- main 브랜치로 돌아갈 필요 없이 develop 브랜치에서 바로 생성하면 됩니다.

### Step 4: 기본 설정 확인

```bash
# 현재 브랜치 확인
git branch

# 원격 저장소 설정 확인
git remote -v

# Worktree 목록 확인 (현재는 메인 저장소만 표시됨)
git worktree list

# Git 버전 확인 (2.5 이상 필요)
git --version
```

### Step 5: 자동화 스크립트 준비 (선택사항)

자동화 스크립트를 사용할 계획이라면 스크립트 디렉토리를 생성하고 스크립트를 추가합니다:

```bash
# scripts 디렉토리 생성
mkdir -p scripts

# 스크립트 파일 생성 (자동화 스크립트 섹션 참조)
# 이후 자동화 스크립트 섹션의 스크립트들을 scripts/ 디렉토리에 추가

# 실행 권한 부여
chmod +x scripts/git-worktree-*.sh
```

### Step 6: IDE 프로젝트 설정

#### IntelliJ IDEA

1. **메인 프로젝트 열기:**
   - File → Open → `/Users/pado/IdeaProjects/wrms-nt-ui` 선택
   - `develop` 브랜치에서 작업하도록 설정

2. **추가 설정:**
   - Settings → Version Control → Git
   - Git executable 경로 확인
   - Auto-update 체크 (선택사항)

#### VS Code

1. **메인 워크스페이스 열기:**
   - File → Open Folder → `/Users/pado/IdeaProjects/wrms-nt-ui` 선택

2. **Git 설정 확인:**
   - Source Control 패널에서 Git이 정상적으로 인식되는지 확인

### 초기 셋업 완료 확인

다음 명령어로 초기 셋업이 올바르게 완료되었는지 확인합니다:

```bash
# 브랜치 목록 확인 (main, develop이 있어야 함)
git branch -a

# Worktree 목록 확인
git worktree list

# 원격 저장소 확인
git remote -v

# 디렉토리 구조 확인
ls -la worktrees/
```

**예상 출력:**
```
* develop
  main
  remotes/origin/develop
  remotes/origin/main

/Users/pado/IdeaProjects/wrms-nt-ui  [develop]
```

### 다음 단계

초기 셋업이 완료되면 다음 단계로 진행할 수 있습니다:

1. **Feature 개발 시작:** [Feature 개발 워크플로우](#1-feature-개발-워크플로우) 참조
2. **자동화 스크립트 사용:** [자동화 스크립트](#자동화-스크립트) 섹션의 스크립트 활용
3. **기존 프로젝트에 적용:** 이미 클론된 프로젝트라면 Step 2부터 진행

### 기존 프로젝트에 적용하는 경우

이미 로컬에 저장소가 있고 `main` 브랜치에서 작업 중인 경우:

```bash
# 현재 위치 확인
cd /Users/pado/IdeaProjects/wrms-nt-ui

# main 브랜치 최신화
git checkout main
git pull origin main

# develop 브랜치 생성 (main에서 분기)
# 이 명령어 실행 후 자동으로 develop 브랜치로 전환됨
git checkout -b develop

# develop 브랜치를 원격에 푸시
git push -u origin develop

# 현재 develop 브랜치에 있으므로 바로 worktrees 디렉토리 생성
# main 브랜치로 돌아갈 필요 없음
mkdir -p worktrees/{feature,release,hotfix}

# 현재 브랜치 확인 (develop 브랜치에 있어야 함)
git branch
```

**참고:** `git checkout -b develop` 명령어는 develop 브랜치를 생성하고 자동으로 해당 브랜치로 전환하므로, worktrees 디렉토리를 만들기 위해 main 브랜치로 돌아갈 필요가 없습니다.

---

## 실무 워크플로우

### 1. Feature 개발 워크플로우

#### Step 1: Feature 브랜치 및 Worktree 생성

```bash
# 메인 저장소에서 실행
cd /Users/pado/IdeaProjects/wrms-nt-ui

# develop 브랜치 최신화
git checkout develop
git pull origin develop

# Feature 브랜치와 Worktree 동시 생성
git worktree add -b feature/user-authentication \
  worktrees/feature/user-authentication develop
```

#### Step 2: Feature 개발

```bash
# Worktree 디렉토리로 이동
cd worktrees/feature/user-authentication

# IDE에서 프로젝트 열기 (IntelliJ IDEA)
# File → Open → worktrees/feature/user-authentication 선택

# 개발 작업 수행
# ... 코드 작성 ...

# 커밋
git add .
git commit -m "feat: 사용자 인증 기능 구현"

# (선택사항) 협업이 필요한 경우 원격에 푸시
# git push -u origin feature/user-authentication
```

**원격 푸시 여부 결정:**
- **로컬에서만 작업:** 원격에 푸시하지 않음 (혼자 작업하는 경우)
- **협업 필요:** 원격에 푸시하여 다른 개발자와 공유 (PR/MR 생성 등)

#### Step 3: Feature 완료 및 병합

**중요:** Worktree가 브랜치를 사용 중이면 브랜치를 삭제할 수 없습니다. 반드시 **Worktree를 먼저 제거**한 후 브랜치를 삭제해야 합니다.

**시나리오 1: 로컬에서만 작업한 경우 (일반적인 경우)**

Step 2에서 원격에 푸시하지 않은 경우입니다. 이 경우 원격 브랜치가 없으므로 원격 브랜치 삭제는 불필요합니다.

```bash
# Feature 브랜치를 develop에 병합
cd /Users/pado/IdeaProjects/wrms-nt-ui
git checkout develop
git merge feature/user-authentication

# develop 브랜치를 원격 저장소에 푸시
git push origin develop

# 1단계: Worktree 제거 (먼저 제거해야 브랜치 삭제 가능)
git worktree remove worktrees/feature/user-authentication

# 2단계: 로컬 브랜치 삭제 (Worktree 제거 후 가능)
git branch -d feature/user-authentication

# 원격 브랜치는 없으므로 삭제할 필요 없음
```

**시나리오 2: 원격에 푸시한 경우 (협업이 필요한 경우)**

Step 2에서 `git push -u origin feature/user-authentication`을 실행한 경우입니다. 이 경우 원격 브랜치를 삭제해야 합니다.

```bash
# Feature 브랜치를 develop에 병합
cd /Users/pado/IdeaProjects/wrms-nt-ui
git checkout develop
git merge feature/user-authentication

# develop 브랜치를 원격 저장소에 푸시
git push origin develop

# 1단계: Worktree 제거 (먼저 제거해야 브랜치 삭제 가능)
git worktree remove worktrees/feature/user-authentication

# 2단계: 로컬 브랜치 삭제 (Worktree 제거 후 가능)
git branch -d feature/user-authentication

# 3단계: 원격 브랜치 삭제 (원격에 푸시한 경우에만 필요)
git push origin --delete feature/user-authentication
```

**원격 브랜치 존재 여부 확인이 필요한 경우:**

원격에 푸시했는지 확실하지 않은 경우, 다음 명령어로 확인할 수 있습니다:

```bash
# 원격 브랜치 존재 여부 확인
if git ls-remote --heads origin feature/user-authentication | grep -q feature/user-authentication; then
  echo "원격 브랜치가 존재합니다. 삭제합니다."
  git push origin --delete feature/user-authentication
else
  echo "원격 브랜치가 없습니다. 삭제할 필요 없습니다."
fi
```

### 2. Release 준비 워크플로우

#### Step 1: Release 브랜치 및 Worktree 생성

```bash
cd /Users/pado/IdeaProjects/wrms-nt-ui
git checkout develop
git pull origin develop

# Release 브랜치와 Worktree 생성
git worktree add -b release/1.0.0 \
  worktrees/release/1.0.0 develop
```

#### Step 2: Release 작업

```bash
cd worktrees/release/1.0.0

# 버전 번호 업데이트
# build.gradle 또는 pom.xml 수정

# 버그 수정 및 문서 업데이트
# ... 작업 수행 ...

git add .
git commit -m "chore: 버전 1.0.0 릴리스 준비"
```

#### Step 3: Release 완료

**중요:** Worktree가 브랜치를 사용 중이면 브랜치를 삭제할 수 없습니다. 반드시 **Worktree를 먼저 제거**한 후 브랜치를 삭제해야 합니다.

```bash
# Release 브랜치를 main과 develop에 병합
cd /Users/pado/IdeaProjects/wrms-nt-ui

# main에 병합 및 태그 생성
git checkout main
git merge release/1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags

# develop에 병합
git checkout develop
git merge release/1.0.0
git push origin develop

# 1단계: Worktree 제거 (먼저 제거해야 브랜치 삭제 가능)
git worktree remove worktrees/release/1.0.0

# 2단계: 로컬 브랜치 삭제 (Worktree 제거 후 가능)
git branch -d release/1.0.0

# 3단계: (조건부) 원격 브랜치가 있는 경우에만 삭제
git push origin --delete release/1.0.0 2>/dev/null || echo "원격 브랜치가 없습니다."
```

**참고:** Release 브랜치는 일반적으로 원격에 푸시하여 팀과 공유하지만, 로컬에서만 작업한 경우 원격 브랜치가 없을 수 있습니다.

### 3. Hotfix 워크플로우

#### Step 1: Hotfix 브랜치 및 Worktree 생성

```bash
cd /Users/pado/IdeaProjects/wrms-nt-ui
git checkout main
git pull origin main

# Hotfix 브랜치와 Worktree 생성
git worktree add -b hotfix/critical-bug-fix \
  worktrees/hotfix/critical-bug-fix main
```

#### Step 2: Hotfix 작업

```bash
cd worktrees/hotfix/critical-bug-fix

# 긴급 수정 작업
# ... 버그 수정 ...

git add .
git commit -m "fix: 긴급 버그 수정"
```

#### Step 3: Hotfix 배포

**중요:** Worktree가 브랜치를 사용 중이면 브랜치를 삭제할 수 없습니다. 반드시 **Worktree를 먼저 제거**한 후 브랜치를 삭제해야 합니다.

```bash
cd /Users/pado/IdeaProjects/wrms-nt-ui

# main에 병합 및 태그 생성
git checkout main
git merge hotfix/critical-bug-fix
git tag -a v1.0.1 -m "Hotfix version 1.0.1"
git push origin main --tags

# develop에 병합
git checkout develop
git merge hotfix/critical-bug-fix
git push origin develop

# 1단계: Worktree 제거 (먼저 제거해야 브랜치 삭제 가능)
git worktree remove worktrees/hotfix/critical-bug-fix

# 2단계: 로컬 브랜치 삭제 (Worktree 제거 후 가능)
git branch -d hotfix/critical-bug-fix

# 3단계: (조건부) 원격 브랜치가 있는 경우에만 삭제
git push origin --delete hotfix/critical-bug-fix 2>/dev/null || echo "원격 브랜치가 없습니다."
```

**참고:** Hotfix 브랜치는 긴급 수정이므로 일반적으로 원격에 푸시하여 팀과 공유하지만, 로컬에서만 작업한 경우 원격 브랜치가 없을 수 있습니다.

### 4. 여러 Feature 동시 개발

```bash
# Feature 1: 사용자 인증
git worktree add -b feature/user-authentication \
  worktrees/feature/user-authentication develop

# Feature 2: 주문 관리
git worktree add -b feature/order-management \
  worktrees/feature/order-management develop

# Feature 3: 상담 시스템
git worktree add -b feature/counseling-system \
  worktrees/feature/counseling-system develop

# 각 Worktree에서 독립적으로 작업 가능
# IDE에서 3개의 프로젝트 창을 동시에 열 수 있음
```

---

## 자동화 스크립트

### 1. Feature Worktree 생성 스크립트

`scripts/git-worktree-feature.sh` 파일 생성:

```bash
#!/bin/bash

# Feature Worktree 생성 스크립트
# 사용법: ./scripts/git-worktree-feature.sh feature-name

FEATURE_NAME=$1

if [ -z "$FEATURE_NAME" ]; then
  echo "❌ 사용법: $0 <feature-name>"
  echo "예: $0 user-authentication"
  exit 1
fi

BRANCH_NAME="feature/$FEATURE_NAME"
WORKTREE_PATH="worktrees/feature/$FEATURE_NAME"

# develop 브랜치 최신화
echo "📥 develop 브랜치 최신화 중..."
git checkout develop
git pull origin develop

# Worktree 생성
echo "🌳 Worktree 생성 중: $BRANCH_NAME"
git worktree add -b "$BRANCH_NAME" "$WORKTREE_PATH" develop

echo "✅ Worktree 생성 완료!"
echo "📂 경로: $WORKTREE_PATH"
echo "🔀 브랜치: $BRANCH_NAME"
echo ""
echo "다음 명령어로 이동하세요:"
echo "  cd $WORKTREE_PATH"
```

### 2. Release Worktree 생성 스크립트

`scripts/git-worktree-release.sh`:

```bash
#!/bin/bash

# Release Worktree 생성 스크립트
# 사용법: ./scripts/git-worktree-release.sh 1.0.0

VERSION=$1

if [ -z "$VERSION" ]; then
  echo "❌ 사용법: $0 <version>"
  echo "예: $0 1.0.0"
  exit 1
fi

BRANCH_NAME="release/$VERSION"
WORKTREE_PATH="worktrees/release/$VERSION"

# develop 브랜치 최신화
echo "📥 develop 브랜치 최신화 중..."
git checkout develop
git pull origin develop

# Worktree 생성
echo "🌳 Release Worktree 생성 중: $BRANCH_NAME"
git worktree add -b "$BRANCH_NAME" "$WORKTREE_PATH" develop

echo "✅ Release Worktree 생성 완료!"
echo "📂 경로: $WORKTREE_PATH"
echo "🔀 브랜치: $BRANCH_NAME"
```

### 3. Hotfix Worktree 생성 스크립트

`scripts/git-worktree-hotfix.sh`:

```bash
#!/bin/bash

# Hotfix Worktree 생성 스크립트
# 사용법: ./scripts/git-worktree-hotfix.sh hotfix-name

HOTFIX_NAME=$1

if [ -z "$HOTFIX_NAME" ]; then
  echo "❌ 사용법: $0 <hotfix-name>"
  echo "예: $0 critical-bug-fix"
  exit 1
fi

BRANCH_NAME="hotfix/$HOTFIX_NAME"
WORKTREE_PATH="worktrees/hotfix/$HOTFIX_NAME"

# main 브랜치 최신화
echo "📥 main 브랜치 최신화 중..."
git checkout main
git pull origin main

# Worktree 생성
echo "🌳 Hotfix Worktree 생성 중: $BRANCH_NAME"
git worktree add -b "$BRANCH_NAME" "$WORKTREE_PATH" main

echo "✅ Hotfix Worktree 생성 완료!"
echo "📂 경로: $WORKTREE_PATH"
echo "🔀 브랜치: $BRANCH_NAME"
```

### 4. Worktree 정리 스크립트

`scripts/git-worktree-cleanup.sh`:

```bash
#!/bin/bash

# 완료된 Worktree 정리 스크립트
# 사용법: ./scripts/git-worktree-cleanup.sh <worktree-path>

WORKTREE_PATH=$1

if [ -z "$WORKTREE_PATH" ]; then
  echo "❌ 사용법: $0 <worktree-path>"
  echo "예: $0 worktrees/feature/user-authentication"
  exit 1
fi

# Worktree 제거
echo "🗑️  Worktree 제거 중: $WORKTREE_PATH"
git worktree remove "$WORKTREE_PATH"

echo "✅ Worktree 제거 완료!"
```

### 5. Worktree 목록 확인 스크립트

`scripts/git-worktree-list.sh`:

```bash
#!/bin/bash

# Worktree 목록 확인 스크립트

echo "🌳 현재 Worktree 목록:"
echo ""
git worktree list
echo ""

# 브랜치별 통계
echo "📊 브랜치별 통계:"
echo ""
git worktree list | grep -E "worktrees/(feature|release|hotfix)" | \
  awk '{print $1}' | \
  sed 's|.*worktrees/||' | \
  sort | \
  uniq -c | \
  awk '{printf "  %s: %d개\n", $2, $1}'
```

### 스크립트 실행 권한 부여

```bash
chmod +x scripts/git-worktree-*.sh
```

---

## 모범 사례

### 1. Worktree 디렉토리 구조

- ✅ `worktrees/` 디렉토리를 `.gitignore`에 추가하지 않음 (Worktree는 Git이 관리)
- ✅ 브랜치 타입별로 하위 디렉토리 구분 (`feature/`, `release/`, `hotfix/`)
- ✅ Worktree 경로명은 브랜치명과 일치시키기

### 2. IDE 설정

#### IntelliJ IDEA

- 각 Worktree를 별도의 프로젝트로 열기
- File → Open → Worktree 디렉토리 선택
- 여러 프로젝트 창을 동시에 열어서 작업 가능

#### VS Code

- 각 Worktree를 별도의 워크스페이스로 열기
- File → Add Folder to Workspace → Worktree 디렉토리 추가

### 3. 빌드 및 테스트

```bash
# 각 Worktree에서 독립적으로 빌드/테스트 실행
cd worktrees/feature/user-authentication
./gradlew build

cd ../order-management
./gradlew build

# 병렬 실행도 가능 (별도 터미널)
```

### 4. 브랜치 관리

- ✅ Feature 완료 후 즉시 Worktree 제거
- ✅ Release/Hotfix 완료 후 즉시 Worktree 제거
- ✅ 오래된 Worktree는 정기적으로 정리
- ✅ 원격 브랜치도 병합 후 삭제

### 5. 충돌 방지

- ✅ 각 Worktree는 독립된 디렉토리이므로 파일 시스템 레벨 충돌 없음
- ✅ 같은 파일을 수정하더라도 브랜치가 다르면 충돌 없음
- ⚠️ 병합 시에만 충돌 발생 가능 (일반적인 Git 병합과 동일)

### 6. 메모리 및 디스크 관리

- ⚠️ Worktree는 디스크 공간을 추가로 사용
- ⚠️ IDE에서 여러 프로젝트를 열면 메모리 사용량 증가
- ✅ 불필요한 Worktree는 즉시 제거하여 공간 확보

---

## 문제 해결

### 1. Worktree 제거 시 오류

**문제:**
```bash
$ git worktree remove worktrees/feature/user-authentication
fatal: 'worktrees/feature/user-authentication' is not a working tree
```

**해결:**
```bash
# 강제 제거
git worktree remove --force worktrees/feature/user-authentication

# 또는 수동으로 디렉토리 삭제 후 정리
rm -rf worktrees/feature/user-authentication
git worktree prune
```

### 2. 브랜치가 이미 존재하는 경우

**문제:**
```bash
$ git worktree add -b feature/user-auth worktrees/feature/user-auth develop
fatal: A branch named 'feature/user-auth' already exists.
```

**해결:**
```bash
# 기존 브랜치 사용
git worktree add worktrees/feature/user-auth feature/user-auth

# 또는 기존 브랜치 삭제 후 재생성
git branch -D feature/user-auth
git worktree add -b feature/user-auth worktrees/feature/user-auth develop
```

### 3. Worktree 경로가 이미 존재하는 경우

**문제:**
```bash
$ git worktree add worktrees/feature/user-auth feature/user-auth
fatal: 'worktrees/feature/user-auth' already exists
```

**해결:**
```bash
# 기존 디렉토리 확인 및 제거
ls -la worktrees/feature/user-auth
rm -rf worktrees/feature/user-auth
git worktree add worktrees/feature/user-auth feature/user-auth
```

### 4. 원격 브랜치와 동기화

```bash
# Worktree에서 원격 브랜치 최신화
cd worktrees/feature/user-authentication
git fetch origin
git merge origin/feature/user-authentication

# 또는 rebase
git rebase origin/feature/user-authentication
```

### 5. Worktree 목록에 없는 디렉토리 정리

```bash
# 사용하지 않는 Worktree 정리
git worktree prune

# 확인
git worktree list
```

### 6. IDE 인덱싱 문제

**문제:** IntelliJ IDEA에서 Worktree를 열었을 때 인덱싱이 느리거나 오류 발생

**해결:**
- File → Invalidate Caches / Restart
- 각 Worktree를 별도 프로젝트로 열기 (같은 프로젝트에 여러 모듈로 추가하지 않기)

---

## 요약

### 핵심 포인트

1. **Git Flow 브랜치 전략**을 따르면서 **Git Worktree**로 각 브랜치를 독립 디렉토리로 관리
2. **Feature/Release/Hotfix** 브랜치는 `worktrees/` 하위에 Worktree로 생성
3. **여러 브랜치를 동시에 작업** 가능하며 IDE에서 여러 프로젝트 창 사용 가능
4. **작업 완료 후 즉시 Worktree 제거**하여 디스크 공간 확보
5. **자동화 스크립트**를 활용하여 Worktree 생성/관리 효율화

### 권장 워크플로우

```
1. Feature 시작
   → 스크립트로 Worktree 생성
   → 개발 작업
   → develop에 병합
   → Worktree 제거

2. Release 준비
   → Release Worktree 생성
   → 버그 수정 및 문서 업데이트
   → main/develop에 병합 및 태그
   → Worktree 제거

3. Hotfix 긴급 수정
   → Hotfix Worktree 생성
   → 버그 수정
   → main/develop에 병합 및 태그
   → Worktree 제거
```

---

## 참고 자료

- [Git Worktree 공식 문서](https://git-scm.com/docs/git-worktree)
- [Git Flow 전략](https://nvie.com/posts/a-successful-git-branching-model/)
- [Git Worktree 실전 가이드](https://www.atlassian.com/git/tutorials/git-worktree)
