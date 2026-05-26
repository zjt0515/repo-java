const fs = require('fs');

const input = fs.readFileSync(0, 'utf8').trim();

const nums = input.split(/\s+/).map(Number);

const a = nums[0];
const b = nums[1];

console.log(a + b);