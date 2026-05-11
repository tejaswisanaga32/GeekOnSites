const http = require('http');
const fs = require('fs');
const path = require('path');
const https = require('https');
const url = require('url');

const port = 3001;
const backendUrl = process.env.REACT_APP_API_URL || 'https://geekonsites-backend.onrender.com';

const mimeTypes = {
  '.html': 'text/html',
  '.js': 'text/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.wav': 'audio/wav',
  '.mp4': 'video/mp4',
  '.woff': 'application/font-woff',
  '.ttf': 'application/font-ttf',
  '.eot': 'application/vnd.ms-fontobject',
  '.otf': 'application/font-otf',
  '.wasm': 'application/wasm',
  '.avif': 'image/avif',
  '.webp': 'image/webp'
};

// Proxy function to forward API requests to backend
function proxyRequest(req, res, parsedUrl) {
  const targetUrl = backendUrl + parsedUrl.pathname + parsedUrl.search;
  console.log(`Proxying API request to: ${targetUrl}`);
  
  const options = {
    method: req.method,
    headers: {
      ...req.headers,
      host: new URL(backendUrl).host
    }
  };
  
  // Remove host header to avoid conflicts
  delete options.headers['host'];
  
  const proxyReq = https.request(targetUrl, options, (proxyRes) => {
    res.writeHead(proxyRes.statusCode, proxyRes.headers);
    proxyRes.pipe(res);
  });
  
  proxyReq.on('error', (err) => {
    console.error('Proxy error:', err);
    res.writeHead(500, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: 'Backend service unavailable' }));
  });
  
  req.pipe(proxyReq);
}

const server = http.createServer((req, res) => {
  // Enable CORS
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }

  // Parse the URL and handle file paths
  const parsedUrl = new URL(req.url, `http://localhost:${port}`);
  
  // Handle API requests - proxy to backend
  if (parsedUrl.pathname.startsWith('/api/')) {
    proxyRequest(req, res, parsedUrl);
    return;
  }
  
  let filePath = '.' + parsedUrl.pathname;
  
  if (filePath === './') {
    filePath = './index.html';
  }

  // Handle URL encoded spaces in filenames
  filePath = decodeURIComponent(filePath);

  const extname = String(path.extname(filePath)).toLowerCase();
  const mimeType = mimeTypes[extname] || 'application/octet-stream';

  console.log(`Serving: ${filePath} as ${mimeType}`);

  fs.readFile(filePath, (error, content) => {
    if (error) {
      console.log(`Error reading ${filePath}:`, error.code);
      if (error.code === 'ENOENT') {
        // File not found, try to serve index.html for SPA routing
        fs.readFile('./index.html', (error, content) => {
          if (error) {
            console.log('Index.html not found either');
            res.writeHead(404, { 'Content-Type': 'text/html' });
            res.end('<h1>404 Not Found</h1><p>File not found: ' + filePath + '</p>', 'utf-8');
          } else {
            console.log('Serving index.html for SPA routing');
            res.writeHead(200, { 'Content-Type': 'text/html' });
            res.end(content, 'utf-8');
          }
        });
      } else {
        console.log('Server error:', error);
        res.writeHead(500);
        res.end('Sorry, check with the site admin for error: ' + error.code + ' ..\n');
      }
    } else {
      console.log(`Successfully serving ${filePath}`);
      res.writeHead(200, { 'Content-Type': mimeType });
      res.end(content);
    }
  });
});

server.listen(port, () => {
  console.log(`Server running at http://localhost:${port}/`);
  console.log(`Frontend accessible at: http://localhost:${port}`);
  console.log(`API requests proxied to: ${backendUrl}`);
  console.log(`Images should load from: http://localhost:${port}/images/`);
});
