import fs from 'fs';

const htmlPath = './target/classes/index.html';
let content = fs.readFileSync(htmlPath, 'utf-8');
content = content.replace(/"\.\/assets\//g, '"../assets/');
fs.writeFileSync(htmlPath, content);
