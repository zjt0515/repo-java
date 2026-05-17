// 构造超大数组/对象，快速耗尽堆内存
const arr = [];
while (true) {
    arr.push(new Array(10).fill('x'));  // 每轮约 8MB，迅速触发 OOM
}