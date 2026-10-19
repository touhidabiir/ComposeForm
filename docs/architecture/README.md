# Architecture write-up sources

`docs/architecture.docx` is generated from the files here. Edit these, then regenerate it, rather than editing the docx by hand.

| File | What it is |
| --- | --- |
| `doc.md` | The text. Markdown subset read by `build.js`: `##`/`###` headings, paragraphs, bullet and numbered lists, tables, fenced code blocks, and `[[DIAGRAM:file.png\|caption]]` lines. "see Build logic", "see Dependency rules", "see Sharing data between features" and "see Appendix" become cross-links. |
| `slices.mmd`, `core.mmd` | Mermaid sources for Figure 1 and Figure 2. |
| `slices.png`, `core.png` | The rendered figures `build.js` embeds. Regenerate them after editing an `.mmd`. |
| `build.js` | Builds the docx with the `docx` npm package: numbered headings, a linked contents list, tables that never split across pages, the layer colour legend. |
| `rasterize.js` | Turns Figure 1's SVG into a PNG, fixing dotted edges mermaid-cli draws solid. |
| `render.sh` | Runs all of the above. |

## Regenerate

```
NODE_PATH=/path/to/node_modules ./docs/architecture/render.sh
```

`NODE_PATH` must resolve `docx` and `playwright`. If only the text changed, `node docs/architecture/build.js` alone is enough; it writes `docs/architecture.docx` (or the path given as its first argument).

`CLAUDE.md` stays the source of truth for the codebase; update this write-up when the architecture itself changes, not for every file move.
