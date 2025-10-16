#!/usr/bin/env bash
# Generates sample/k8s/sample.yaml from HTML files in sample/html
# Usage: bash sample/generate-sample-yaml.sh [source_html_dir] [output_yaml]
# Defaults (relative to this script's directory): source_html_dir=html, output_yaml=k8s/sample.yaml
set -euo pipefail

# Resolve directory of this script (supports symlinks)
SCRIPT_DIR="$(cd -- "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd -P)"

# Defaults are relative to the script location; user-provided args are used as-is
SRC_DIR=${1:-"${SCRIPT_DIR}/html"}
OUT_FILE=${2:-"${SCRIPT_DIR}/k8s/sample.yaml"}

if [[ ! -d "$SRC_DIR" ]]; then
  echo "Source directory not found: $SRC_DIR" >&2
  exit 1
fi

# Ensure output directory exists
mkdir -p "$(dirname "$OUT_FILE")"

{
  echo "apiVersion: demo.cechacek.net/v1alpha1"
  echo "kind: Website"
  echo "metadata:"
  echo "  name: sample"
  echo "  namespace: site"
  echo "spec:"
  echo "  html:"
  echo "    pages:"

  # List HTML files deterministically
  while IFS= read -r file; do
    base=$(basename "$file")
    echo "      ${base}: |"
    # Indent file content by 8 spaces
    sed 's/^/        /' "$file"
    # Ensure a separating newline between blocks
    echo ""
  done < <(find "$SRC_DIR" -maxdepth 1 -type f -name '*.html' -print | LC_ALL=C sort)
} > "$OUT_FILE"

echo "Generated $OUT_FILE from HTML files in $SRC_DIR"