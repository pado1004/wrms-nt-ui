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