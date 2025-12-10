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