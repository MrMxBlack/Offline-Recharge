#!/usr/bin/env bash
set -euo pipefail

# Run this after choosing and reviewing the exact upstream commit.
# Example:
#   LLAMA_COMMIT=<40-char-commit> ./tools/pin_llama_cpp.sh
#
# The script intentionally refuses to float on "master".
: "${LLAMA_COMMIT:?Set LLAMA_COMMIT to an exact llama.cpp commit SHA}"

if [[ "${#LLAMA_COMMIT}" -ne 40 ]]; then
  echo "LLAMA_COMMIT must be a 40-character commit SHA."
  exit 1
fi

mkdir -p third_party
if [[ ! -d third_party/llama.cpp/.git ]]; then
  git clone https://github.com/ggml-org/llama.cpp.git third_party/llama.cpp
fi
git -C third_party/llama.cpp fetch --depth 1 origin "$LLAMA_COMMIT"
git -C third_party/llama.cpp checkout --detach "$LLAMA_COMMIT"

echo "Pinned llama.cpp at:"
git -C third_party/llama.cpp rev-parse HEAD
