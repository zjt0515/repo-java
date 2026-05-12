const fs = require('fs');

const ch = fs.readFileSync(0, 'utf8').trim()[0];

console.log(`  ${ch}`);
console.log(` ${ch.repeat(3)}`);
console.log(ch.repeat(5));