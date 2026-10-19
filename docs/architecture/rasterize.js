// Turns a Mermaid SVG into a 2x PNG, after fixing dotted edges: mermaid-cli sometimes writes a
// broken stroke-dasharray on a dotted edge (e.g. "0 0 465 2"), which renders it as a solid line.
// Usage: node rasterize.js in.svg out.png
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

(async () => {
  const [input, output] = process.argv.slice(2);
  let svg = fs.readFileSync(input, 'utf8');
  svg = svg.replace(/<path [^>]*flowchart-link[^>]*>/g, tag => {
    if (!tag.includes('edge-pattern-dotted')) return tag;
    return tag.replace(/stroke-dasharray:[^;"]*;?/g, '').replace('style="', 'style="stroke-dasharray: 2 2;');
  });
  const [, , w, h] = svg.match(/viewBox="([\d.\-]+) ([\d.\-]+) ([\d.]+) ([\d.]+)"/).slice(1).map(Number);
  const fixed = path.join(path.dirname(path.resolve(output)), '.fixed.svg');
  fs.writeFileSync(fixed, svg);

  const browser = await chromium.launch(process.env.CHROMIUM_PATH ? { executablePath: process.env.CHROMIUM_PATH, args: ['--no-sandbox'] } : {});
  const page = await browser.newPage({ viewport: { width: Math.ceil(w), height: Math.ceil(h) }, deviceScaleFactor: 2 });
  await page.goto('file://' + fixed);
  await page.screenshot({ path: output });
  await browser.close();
  fs.unlinkSync(fixed);
})();
