import { createServer } from 'node:http';
import { createReadStream } from 'node:fs';
import { stat } from 'node:fs/promises';
import { extname, resolve, sep } from 'node:path';
import { fileURLToPath } from 'node:url';

const projectRoot = fileURLToPath(new URL('../', import.meta.url));
const root = resolve(projectRoot, process.argv[2] || '.');
const port = Number(process.env.PORT || 4173);
const types = { '.html': 'text/html; charset=utf-8', '.css': 'text/css; charset=utf-8', '.js': 'text/javascript; charset=utf-8', '.svg': 'image/svg+xml' };
const server = createServer(async (request, response) => {
  try {
    if (!['GET', 'HEAD'].includes(request.method)) {
      response.writeHead(405, { Allow: 'GET, HEAD' }); response.end(); return;
    }
    const pathname = decodeURIComponent(new URL(request.url, 'http://localhost').pathname);
    let file = resolve(root, '.' + pathname);
    if (file !== root && !file.startsWith(root + sep)) {
      response.writeHead(403); response.end('Forbidden'); return;
    }
    if (pathname.split('/').some(part => part.startsWith('.') && part !== '.nojekyll')) {
      response.writeHead(403); response.end('Forbidden'); return;
    }
    if (pathname.endsWith('/')) file = resolve(file, 'index.html');
    const metadata = await stat(file).catch(() => null);
    if (!metadata?.isFile()) {
      file = resolve(root, '404.html'); response.statusCode = 404;
    }
    response.setHeader('Content-Type', types[extname(file)] || 'application/octet-stream');
    response.setHeader('X-Content-Type-Options', 'nosniff');
    if (request.method === 'HEAD') { response.end(); return; }
    createReadStream(file).on('error', () => { response.statusCode = 500; response.end('Server error'); }).pipe(response);
  } catch { response.writeHead(400); response.end('Bad request'); }
});
server.listen(port, '127.0.0.1', () => console.log('VoxHub: http://localhost:' + port));
