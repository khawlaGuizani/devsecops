#!/usr/bin/env bash
# Simple commit-msg checker for Conventional-like pattern
msg=$(sed -n '1p' "$1")
if ! echo "$msg" | grep -Eq '^(feat|fix|chore|docs|refactor|test|style)(\(.+\))?: .{1,}'; then
  echo "Invalid commit message: $msg"
  echo "Expected: type(scope): subject (e.g. feat(api): add endpoint)"
  exit 1
fi
