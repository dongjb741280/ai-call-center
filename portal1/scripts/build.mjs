import { cp, mkdir, rm } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import { resolve } from 'node:path';
const source = fileURLToPath(new URL('../', import.meta.url));
const destination = resolve(source, 'dist');
await rm(destination, { recursive: true, force: true });
await mkdir(destination, { recursive: true });
for (const entry of ['index.html', 'guide.html', '404.html', '.nojekyll', 'assets']) {
  await cp(resolve(source, entry), resolve(destination, entry), { recursive: true });
}
console.log('Static portal built in dist/ (no external dependencies)');
