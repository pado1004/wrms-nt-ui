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