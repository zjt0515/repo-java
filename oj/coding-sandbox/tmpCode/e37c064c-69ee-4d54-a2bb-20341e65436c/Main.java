const readline = require('readline');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.on('line', (line) => {
    const ch = line.trim();
    const height = 3;
    
    for (let i = 1; i <= height; i++) {
        const spaces = ' '.repeat(height - i);
        const chars = ch.repeat(2 * i - 1);
        console.log(spaces + chars);
    }
    
    rl.close();
});