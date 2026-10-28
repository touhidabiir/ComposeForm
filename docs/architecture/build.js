const fs = require('fs');
const path = require('path');
const {
  Document, Packer, Paragraph, TextRun, HeadingLevel, Table, TableRow, TableCell,
  WidthType, ShadingType, BorderStyle, ImageRun, AlignmentType, LevelFormat, LineRuleType,
  Bookmark, InternalHyperlink, Header, Footer, PageNumber, TabStopType, VerticalAlign,
} = require('docx');

const dir = __dirname;
const md = fs.readFileSync(path.join(dir, 'doc.md'), 'utf8').split('\n');

const CONTENT_W = 9746; // A4 width 11906 - 2 * 1080 margins
const BODY = 'Calibri';
const MONO = 'Consolas';
const C = {
  ink: '1F2A36', muted: '6B7680', brand: '1F4E79', brand2: '2E75B6', code: '8A2B0E',
  rule: 'C9D3DD', zebra: 'F4F7FA', calloutFill: 'EEF4FA', codeFill: 'F6F8FA',
};
const LAYERS = [
  ['ui', 'BBDEFB', '1565C0'], ['domain', 'C8E6C9', '2E7D32'], ['data', 'FFE0B2', 'E65100'],
  ['navigation', 'E1BEE7', '6A1B9A'], ['core', 'ECEFF1', '78909C'], [':app', '37474F', '263238'],
];

const slug = s => s.replace(/[`*]/g, '').toLowerCase().replace(/[^a-z0-9]+/g, '_').replace(/^_|_$/g, '').slice(0, 38);

// ---- pass 1: collect headings for numbering, the contents and cross-links
const headings = [];
{
  let h1 = 0, h2 = 0;
  for (const l of md) {
    if (l.startsWith('## Appendix')) { h1 = 'A'; h2 = 0; headings.push({ level: 1, text: l.slice(3), num: 'A', id: 'h_appendix' }); }
    else if (l.startsWith('## ')) { h1++; h2 = 0; headings.push({ level: 1, text: l.slice(3), num: `${h1}`, id: 'h_' + slug(l.slice(3)) }); }
    else if (l.startsWith('### ')) { h2++; headings.push({ level: 2, text: l.slice(4), num: `${h1}.${h2}`, id: 'h_' + `${h1}_${h2}_` + slug(l.slice(4)) }); }
  }
}
const byTitle = Object.fromEntries(headings.filter(h => h.level === 1).map(h => [h.text.replace(/`/g, ''), h]));
byTitle['Appendix'] = headings.find(h => h.id === 'h_appendix');

// ---- inline: **bold**, `code`, and "see <Section>" cross-links
function runs(text, opts = {}) {
  const out = [];
  const re = /(\*\*.+?\*\*|`[^`]+`|see (?:Build logic|Sharing data between features|Dependency rules|Appendix))/g;
  let last = 0, m;
  while ((m = re.exec(text))) {
    if (m.index > last) out.push(new TextRun({ text: text.slice(last, m.index), ...opts }));
    const tok = m[0];
    if (tok.startsWith('**')) out.push(...runs(tok.slice(2, -2), { ...opts, bold: true }));
    else if (tok.startsWith('`')) out.push(new TextRun({ text: tok.slice(1, -1), font: MONO, size: opts.size ? opts.size - 2 : 19, color: opts.color || C.code, bold: opts.bold }));
    else {
      const target = byTitle[tok.slice(4)];
      out.push(new TextRun({ text: 'see ', ...opts }));
      const txt = target.id === 'h_appendix' ? 'Appendix' : `§${target.num} ${target.text}`;
      out.push(new InternalHyperlink({ anchor: target.id, children: [new TextRun({ text: txt, style: 'Hyperlink', ...opts })] }));
    }
    last = m.index + tok.length;
  }
  if (last < text.length) out.push(new TextRun({ text: text.slice(last), ...opts }));
  return out;
}

const cellBorders = { top: { style: BorderStyle.SINGLE, size: 4, color: C.rule }, bottom: { style: BorderStyle.SINGLE, size: 4, color: C.rule }, left: { style: BorderStyle.NONE, size: 0, color: 'FFFFFF' }, right: { style: BorderStyle.NONE, size: 0, color: 'FFFFFF' } };

function colWidths(header) {
  let pct;
  if (header[0] === 'Plugin') pct = [0.42, 0.58];
  else if (header.length === 2) pct = [0.3, 0.7];
  else if (header[1] === 'May depend on') pct = [0.22, 0.46, 0.32];
  else if (header[0] === 'Situation') pct = [0.3, 0.38, 0.32];
  else pct = [0.25, 0.42, 0.33];
  const w = pct.map(p => Math.round(p * CONTENT_W));
  w[w.length - 1] = CONTENT_W - w.slice(0, -1).reduce((a, b) => a + b, 0);
  return w;
}

// Whole table on one page: rows can't split, and every paragraph except
// the last row's keeps with the next one.
function table(rows) {
  const widths = colWidths(rows[0]);
  return new Table({
    width: { size: CONTENT_W, type: WidthType.DXA },
    columnWidths: widths,
    rows: rows.map((cells, ri) => {
      const head = ri === 0, last = ri === rows.length - 1;
      return new TableRow({
        cantSplit: true,
        tableHeader: head,
        children: cells.map((c, ci) => new TableCell({
          width: { size: widths[ci], type: WidthType.DXA },
          borders: cellBorders,
          shading: { type: ShadingType.CLEAR, color: 'auto', fill: head ? C.brand : (ri % 2 === 0 ? C.zebra : 'FFFFFF') },
          margins: { top: 80, bottom: 80, left: 120, right: 120 },
          children: [new Paragraph({
            keepNext: !last, keepLines: true, spacing: { before: 0, after: 0 },
            children: runs(c, head ? { bold: true, size: 20, color: 'FFFFFF' } : { size: 20 }),
          })],
        })),
      });
    }),
  });
}

function codeBlock(lines, label) {
  const size = Math.max(...lines.map(l => l.length)) > 92 ? 15 : 16;
  const box = { style: BorderStyle.SINGLE, size: 4, color: 'D5DCE3', space: 4 };
  const out = [new Paragraph({
    keepNext: true, spacing: { before: 160, after: 40 },
    children: [new TextRun({ text: label.toUpperCase(), size: 15, bold: true, color: C.muted, characterSpacing: 20 })],
  })];
  lines.forEach((l, i) => out.push(new Paragraph({
    keepNext: i < lines.length - 1, keepLines: true,
    shading: { type: ShadingType.CLEAR, fill: C.codeFill, color: 'auto' },
    border: { left: { style: BorderStyle.SINGLE, size: 18, color: C.brand2, space: 6 }, right: box, top: i === 0 ? box : undefined, bottom: i === lines.length - 1 ? box : undefined },
    spacing: { before: 0, after: i === lines.length - 1 ? 180 : 0, line: 250, lineRule: LineRuleType.AUTO },
    indent: { left: 140, right: 60 },
    children: [new TextRun({ text: l.length ? l : ' ', font: MONO, size })],
  })));
  return out;
}

function callout(text) {
  return new Paragraph({
    shading: { type: ShadingType.CLEAR, fill: C.calloutFill, color: 'auto' },
    border: { left: { style: BorderStyle.SINGLE, size: 24, color: C.brand2, space: 8 } },
    indent: { left: 180, right: 120 },
    spacing: { before: 120, after: 180, line: 290, lineRule: LineRuleType.AUTO },
    keepLines: true,
    children: runs(text),
  });
}

function label(text) {
  return new Paragraph({
    keepNext: true, spacing: { before: 200, after: 80 },
    children: [new TextRun({ text: text.toUpperCase(), bold: true, size: 18, color: C.brand2, characterSpacing: 30 })],
  });
}

function legend() {
  const w = Math.floor(CONTENT_W / LAYERS.length);
  const widths = LAYERS.map((_, i) => i === LAYERS.length - 1 ? CONTENT_W - w * (LAYERS.length - 1) : w);
  const none = { style: BorderStyle.SINGLE, size: 12, color: 'FFFFFF' };
  return new Table({
    width: { size: CONTENT_W, type: WidthType.DXA }, columnWidths: widths,
    rows: [new TableRow({
      cantSplit: true,
      children: LAYERS.map(([name, fill, stroke], i) => new TableCell({
        width: { size: widths[i], type: WidthType.DXA },
        shading: { type: ShadingType.CLEAR, fill, color: 'auto' },
        borders: { top: none, bottom: { style: BorderStyle.SINGLE, size: 18, color: stroke }, left: none, right: none },
        verticalAlign: VerticalAlign.CENTER,
        margins: { top: 60, bottom: 60, left: 60, right: 60 },
        children: [new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 0, after: 0 }, children: [new TextRun({ text: name, font: MONO, size: 18, bold: true, color: name === ':app' ? 'FFFFFF' : stroke })] })],
      })),
    })],
  });
}

// [pixel width, pixel height, display width in points], read from each PNG's IHDR header.
const pngSize = f => { const b = fs.readFileSync(path.join(dir, f)); return [b.readUInt32BE(16), b.readUInt32BE(20)]; };
const IMG = { 'slices.png': [...pngSize('slices.png'), 470], 'core.png': [...pngSize('core.png'), 430] };
function figure(file, caption) {
  const [pw, ph, w] = IMG[file];
  return [
    new Paragraph({
      alignment: AlignmentType.CENTER, keepNext: true,
      spacing: { before: 120, after: 60, line: 240, lineRule: LineRuleType.AUTO },
      children: [new ImageRun({ type: 'png', data: fs.readFileSync(path.join(dir, file)), transformation: { width: w, height: Math.round(w * ph / pw) }, altText: { title: caption, description: caption, name: file } })],
    }),
    new Paragraph({ alignment: AlignmentType.CENTER, keepNext: true, spacing: { after: 120 }, children: [new TextRun({ text: caption, italics: true, size: 18, color: C.muted })] }),
  ];
}

function backToTop() {
  return new Paragraph({
    alignment: AlignmentType.RIGHT, spacing: { before: 120, after: 0 },
    children: [new InternalHyperlink({ anchor: 'contents', children: [new TextRun({ text: '↑ Back to contents', size: 16, color: C.brand2 })] })],
  });
}

// ---- title block + contents
const children = [
  new Paragraph({ spacing: { before: 0, after: 60 }, children: [new TextRun({ text: 'ARCHITECTURE', bold: true, size: 18, color: C.brand2, characterSpacing: 60 })] }),
  new Paragraph({ spacing: { after: 80 }, children: [new TextRun({ text: 'ComposeForm Architecture', bold: true, size: 52, color: C.ink })] }),
  new Paragraph({ spacing: { after: 120 }, children: [new TextRun({ text: 'Feature-sliced modules for an Android app with hundreds of screens and APIs', size: 24, color: C.muted })] }),
  new Paragraph({
    border: { bottom: { style: BorderStyle.SINGLE, size: 18, color: C.brand, space: 8 } }, spacing: { after: 320 },
    children: [new TextRun({ text: 'Touhid Abir  ·  October 2026', size: 19, color: C.muted })],
  }),
  new Paragraph({ spacing: { after: 120 }, children: [new Bookmark({ id: 'contents', children: [new TextRun({ text: 'Contents', bold: true, size: 28, color: C.brand })] })] }),
  ...headings.map(h => new Paragraph({
    indent: { left: h.level === 1 ? 0 : 440 },
    spacing: { before: h.level === 1 ? 80 : 0, after: 30 },
    children: [new InternalHyperlink({ anchor: h.id, children: [new TextRun({ text: `${h.num}${h.level === 1 && h.num !== 'A' ? '.' : ''}  ${h.text.replace(/`/g, '')}`, size: h.level === 1 ? 22 : 20, bold: h.level === 1, color: h.level === 1 ? C.ink : C.brand2 })] })],
  })),
  new Paragraph({ spacing: { after: 0 }, pageBreakBefore: false, children: [] }),
];

// ---- body
const CALLOUT_LEADS = ['**What counts as a feature:**', '**Enforced by the build.**', '**Use cases only when needed.**'];
let orderedInstance = 0, hi = 0, i = 4; // skip title, byline
let firstH1 = true;
while (i < md.length) {
  const line = md[i];
  if (line.startsWith('```')) {
    const lang = line.slice(3).trim();
    const buf = [];
    i++;
    while (!md[i].startsWith('```')) buf.push(md[i++]);
    i++;
    const lbl = lang === 'kotlin' ? 'Kotlin' : (buf[0].startsWith('plugins') ? 'Kotlin' : 'Structure');
    children.push(...codeBlock(buf, lbl));
    continue;
  }
  const dia = line.match(/^\[\[DIAGRAM:(.+?)\|(.+)\]\]$/);
  if (dia) {
    children.push(...figure(dia[1], dia[2]));
    if (dia[1] === 'slices.png') children.push(legend(), new Paragraph({ spacing: { after: 120 }, children: [] }));
    i++;
    continue;
  }
  if (line.startsWith('|')) {
    const rows = [];
    while (i < md.length && md[i].startsWith('|')) {
      const cells = md[i].split('|').slice(1, -1).map(s => s.trim());
      if (!cells.every(c => /^-+$/.test(c))) rows.push(cells);
      i++;
    }
    children.push(table(rows), new Paragraph({ spacing: { before: 0, after: 100 }, children: [] }));
    continue;
  }
  const next = md.slice(i + 1).find(l => l.trim() !== '') || '';
  if (line.startsWith('## ')) {
    const h = headings[hi++];
    if (!firstH1 && h.id !== 'h_appendix') children.push(backToTop());
    firstH1 = false;
    children.push(new Paragraph({
      heading: HeadingLevel.HEADING_1, keepNext: true, pageBreakBefore: h.id === 'h_appendix',
      children: [new Bookmark({ id: h.id, children: [new TextRun({ text: `${h.num}  `, color: C.brand2 }), ...runs(h.text, { color: C.brand })] })],
    }));
  } else if (line.startsWith('### ')) {
    const h = headings[hi++];
    children.push(new Paragraph({
      heading: HeadingLevel.HEADING_2, keepNext: true,
      children: [new Bookmark({ id: h.id, children: [new TextRun({ text: `${h.num}  `, color: C.muted }), ...runs(h.text, { color: C.ink })] })],
    }));
  } else if (line.startsWith('- ')) {
    children.push(new Paragraph({ numbering: { reference: 'bullets', level: 0 }, spacing: { after: 70 }, children: runs(line.slice(2)) }));
  } else if (/^\d+\. /.test(line)) {
    if (/^1\. /.test(line)) orderedInstance++;
    children.push(new Paragraph({ numbering: { reference: 'numbers', level: 0, instance: orderedInstance }, spacing: { after: 70 }, children: runs(line.replace(/^\d+\. /, '')) }));
  } else if (line.trim() === '') {
    // spacing handles gaps
  } else if (/^\*\*[^*]+\*\*$/.test(line.trim())) {
    children.push(label(line.trim().slice(2, -2).replace(/:$/, '')));
  } else if (CALLOUT_LEADS.some(p => line.startsWith(p))) {
    children.push(callout(line));
  } else {
    const keep = line.trim().endsWith(':') || next.startsWith('[[DIAGRAM');
    children.push(new Paragraph({ keepNext: keep, spacing: { after: 140 }, children: runs(line) }));
  }
  i++;
}

const doc = new Document({
  creator: 'Touhid Abir',
  title: 'ComposeForm Architecture',
  description: 'Feature-sliced module architecture for ComposeForm',
  styles: {
    default: { document: { run: { font: BODY, size: 22, color: C.ink }, paragraph: { spacing: { line: 288, lineRule: LineRuleType.AUTO } } } },
    paragraphStyles: [
      { id: 'Heading1', name: 'Heading 1', basedOn: 'Normal', next: 'Normal', quickFormat: true, run: { size: 34, bold: true, color: C.brand }, paragraph: { spacing: { before: 280, after: 140 }, keepNext: true, outlineLevel: 0, border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: C.rule, space: 4 } } } },
      { id: 'FooterText', name: 'Footer Text', basedOn: 'Normal', run: { size: 16, color: C.muted } },
      { id: 'Heading2', name: 'Heading 2', basedOn: 'Normal', next: 'Normal', quickFormat: true, run: { size: 26, bold: true, color: C.ink }, paragraph: { spacing: { before: 260, after: 100 }, keepNext: true, outlineLevel: 1 } },
    ],
    characterStyles: [{ id: 'Hyperlink', name: 'Hyperlink', basedOn: 'DefaultParagraphFont', run: { color: C.brand2, underline: { type: 'single', color: C.brand2 } } }],
  },
  numbering: {
    config: [
      { reference: 'bullets', levels: [{ level: 0, format: LevelFormat.BULLET, text: '▪', alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 520, hanging: 260 } }, run: { color: C.brand2 } } }] },
      { reference: 'numbers', levels: [{ level: 0, format: LevelFormat.DECIMAL, text: '%1.', alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 520, hanging: 300 } }, run: { bold: true, color: C.brand2 } } }] },
    ],
  },
  sections: [{
    properties: { page: { margin: { top: 1080, bottom: 1080, left: 1080, right: 1080, header: 500, footer: 500 } }, titlePage: true },
    headers: {
      default: new Header({ children: [new Paragraph({ alignment: AlignmentType.RIGHT, border: { bottom: { style: BorderStyle.SINGLE, size: 4, color: C.rule, space: 4 } }, children: [new TextRun({ text: 'ComposeForm Architecture', size: 16, color: C.muted })] })] }),
      first: new Header({ children: [new Paragraph({ children: [] })] }),
    },
    footers: {
      default: new Footer({ children: [new Paragraph({ style: 'FooterText', alignment: AlignmentType.CENTER, children: [new TextRun({ children: ['Page ', PageNumber.CURRENT, ' of ', PageNumber.TOTAL_PAGES], size: 16, color: C.muted })] })] }),
      first: new Footer({ children: [new Paragraph({ style: 'FooterText', alignment: AlignmentType.CENTER, children: [new TextRun({ children: ['Page ', PageNumber.CURRENT, ' of ', PageNumber.TOTAL_PAGES], size: 16, color: C.muted })] })] }),
    },
    children,
  }],
});

Packer.toBuffer(doc).then(buf => {
  fs.writeFileSync(process.argv[2] || path.join(dir, '..', 'architecture.docx'), buf);
  console.log('written', buf.length);
});
