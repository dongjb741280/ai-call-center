import test from 'node:test';
import assert from 'node:assert/strict';
import { readFile, access } from 'node:fs/promises';
import { resolve, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';
const root = fileURLToPath(new URL('../', import.meta.url));
for (const page of ['index.html', 'guide.html', '404.html']) {
  test(page + ': local links, assets, fragments and IDs are valid', async () => {
    const html = await readFile(resolve(root, page), 'utf8');
    const ids = [...html.matchAll(/\bid="([^"]+)"/g)].map(match => match[1]);
    assert.equal(new Set(ids).size, ids.length, 'Duplicate IDs');
    for (const [, reference] of html.matchAll(/(?:href|src)="([^"]+)"/g)) {
      if (/^(https?:|mailto:|data:)/.test(reference)) continue;
      const [pathname, fragment] = reference.split('#');
      const target = pathname ? resolve(dirname(resolve(root, page)), pathname) : resolve(root, page);
      await access(target);
      if (fragment) {
        const targetHtml = await readFile(target, 'utf8');
        assert.ok(targetHtml.includes('id="' + fragment + '"'), 'Missing fragment: ' + reference);
      }
    }
  });
}
test('deployment artifact includes only publishable static files', async () => {
  const build = await readFile(resolve(root, 'scripts/build.mjs'), 'utf8');
  assert.ok(build.includes("'assets'"));
  assert.ok(!build.includes("cp(source,"));
  const html = await readFile(resolve(root, 'index.html'), 'utf8');
  assert.ok(!html.includes('https://cdn.'));
});
