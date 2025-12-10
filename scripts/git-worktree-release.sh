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