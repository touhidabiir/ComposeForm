#!/usr/bin/env bash
# Regenerates the diagrams and docs/architecture.docx from the sources in this folder.
# Needs Node with the `docx` and `playwright` packages resolvable (e.g. NODE_PATH), and network
# access for `npx @mermaid-js/mermaid-cli` on first run. Set CHROMIUM_PATH to use a specific
# Chromium for both mermaid-cli and rasterize.js.
set -euo pipefail
cd "$(dirname "$0")"

mmdc=(npx -y -p @mermaid-js/mermaid-cli mmdc)
if [[ -n "${CHROMIUM_PATH:-}" ]]; then
  printf '{"executablePath":"%s","args":["--no-sandbox"]}' "$CHROMIUM_PATH" > .puppeteer.json
  mmdc+=(-p .puppeteer.json)
fi

# Figure 2 renders fine directly.
"${mmdc[@]}" -i core.mmd -o core.png -s 2 -b white
# Figure 1 has dotted edges that mermaid-cli can draw solid; rasterize.js fixes them.
"${mmdc[@]}" -i slices.mmd -o .slices.svg -b white
node rasterize.js .slices.svg slices.png
rm -f .slices.svg .puppeteer.json

node build.js
