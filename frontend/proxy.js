// Proxy reverso simples (Node, zero dependências):
//  - serve o front compilado em Kotlin/JS na porta 3000
//  - repassa /transacoes* para o back Spring Boot na porta 8080
//  -> o browser fala só com :3000 (same-origin), então o CORS do Spring não bloqueia.
const http = require("http");
const fs = require("fs");
const path = require("path");

const FRONT_PORT = 3000;
const BACK_HOST = "localhost";
const BACK_PORT = 8080;
const BUNDLE_DIR = path.join(__dirname, "build", "kotlin-webpack", "js", "productionExecutable");
const INDEX_HTML = path.join(__dirname, "index.html");

const MIME = {
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".map": "application/json",
  ".json": "application/json",
  ".svg": "image/svg+xml",
  ".ico": "image/x-icon",
};

function serveStatic(req, res) {
  let urlPath = req.url.split("?")[0];
  if (urlPath === "/" || urlPath === "/index.html") {
    fs.readFile(INDEX_HTML, (err, data) => {
      if (err) { res.writeHead(404); res.end("index.html não encontrado"); return; }
      res.writeHead(200, { "Content-Type": MIME[".html"] });
      res.end(data);
    });
    return;
  }
  const filePath = path.join(BUNDLE_DIR, urlPath);
  if (!filePath.startsWith(BUNDLE_DIR)) {
    res.writeHead(403); res.end("Forbidden"); return;
  }
  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404, { "Content-Type": "text/plain" });
      res.end("Arquivo não encontrado. Rode 'gradlew browserProductionWebpack' primeiro.");
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    res.writeHead(200, { "Content-Type": MIME[ext] || "application/octet-stream" });
    res.end(data);
  });
}

function proxyToBack(req, res) {
  const options = {
    host: BACK_HOST,
    port: BACK_PORT,
    path: req.url,
    method: req.method,
    headers: req.headers,
  };
  const proxyReq = http.request(options, (proxyRes) => {
    res.writeHead(proxyRes.statusCode || 502, proxyRes.headers);
    proxyRes.pipe(res);
  });
  proxyReq.on("error", (e) => {
    res.writeHead(502, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ erro: "Backend indisponível", detalhe: String(e.message) }));
  });
  req.pipe(proxyReq);
}

const server = http.createServer((req, res) => {
  if (req.url.startsWith("/transacoes")) {
    proxyToBack(req, res);
  } else {
    serveStatic(req, res);
  }
});

server.listen(FRONT_PORT, () => {
  console.log(`Front (Kotlin/JS) em http://localhost:${FRONT_PORT}`);
  console.log(`Proxy /transacoes -> http://${BACK_HOST}:${BACK_PORT}`);
});
