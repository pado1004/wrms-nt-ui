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