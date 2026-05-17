const arr = [];
while (true) {
    arr.push(new Array(1000000).fill('x'));  // 每轮约 8MB，迅速触发 OOM
}