const readline = require('readline');
const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

rl.on('line', (line) => {
    // 精确分配 50MB 连续内存，并写入数据防止 V8 优化回收
    const mem = Buffer.alloc(50 * 1024 * 1024);
    for (let i = 0; i < mem.length; i += 4096) {
        mem[i] = i % 256;
    }
    
    // 挂载到全局，防止被 GC 回收
    global._hold = mem;
    
    const [a, b] = line.trim().split(' ').map(Number);
    console.log(a + b);
    rl.close();
});