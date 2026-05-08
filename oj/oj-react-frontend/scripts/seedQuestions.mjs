const API_BASE = process.env.API_BASE || 'http://localhost:8121'
const OJ_COOKIE = process.env.OJ_COOKIE || ''

const questions = [
  {
    title: '两数之和',
    tags: ['数组', '哈希表'],
    content:
      '给定一个整数数组 nums 和一个整数 target，请找出数组中和为 target 的两个整数，并返回它们的下标。假设每组输入只对应一个答案。',
    judgeCase: [
      { input: '[2,7,11,15]\n9', output: '[0,1]' },
      { input: '[3,2,4]\n6', output: '[1,2]' },
    ],
    answer:
      '使用哈希表记录每个数字出现的位置，遍历时查找 target - nums[i] 是否已经出现。',
  },
  {
    title: '反转链表',
    tags: ['链表', '递归'],
    content:
      '给定单链表的头节点 head，请反转链表并返回反转后的头节点。',
    judgeCase: [
      { input: '[1,2,3,4,5]', output: '[5,4,3,2,1]' },
      { input: '[1,2]', output: '[2,1]' },
    ],
    answer:
      '维护 prev 和 current 两个指针，逐步改变 current.next 的指向直到链表遍历结束。',
  },
  {
    title: '有效的括号',
    tags: ['栈', '字符串'],
    content:
      '给定一个只包含括号字符的字符串，判断括号是否按正确顺序闭合。',
    judgeCase: [
      { input: '()[]{}', output: 'true' },
      { input: '(]', output: 'false' },
    ],
    answer:
      '使用栈保存左括号，遇到右括号时判断栈顶是否匹配。',
  },
  {
    title: '合并两个有序数组',
    tags: ['数组', '双指针'],
    content:
      '给定两个非递减数组 nums1 和 nums2，将 nums2 合并到 nums1 中，使合并后的数组仍然有序。',
    judgeCase: [
      { input: '[1,2,3,0,0,0]\n3\n[2,5,6]\n3', output: '[1,2,2,3,5,6]' },
      { input: '[1]\n1\n[]\n0', output: '[1]' },
    ],
    answer:
      '从两个数组的尾部开始比较，把较大的元素放到 nums1 的末尾，避免额外移动。',
  },
  {
    title: '最大子数组和',
    tags: ['数组', '动态规划'],
    content:
      '给定一个整数数组 nums，请找出一个具有最大和的连续子数组，并返回其最大和。',
    judgeCase: [
      { input: '[-2,1,-3,4,-1,2,1,-5,4]', output: '6' },
      { input: '[1]', output: '1' },
    ],
    answer:
      '令 dp 表示以当前元素结尾的最大子数组和，状态转移为 max(nums[i], dp + nums[i])。',
  },
  {
    title: '爬楼梯',
    tags: ['动态规划', '数学'],
    content:
      '每次可以爬 1 或 2 个台阶，给定台阶数 n，计算到达楼顶的方法数。',
    judgeCase: [
      { input: '2', output: '2' },
      { input: '3', output: '3' },
    ],
    answer:
      '第 n 阶的方法数等于第 n - 1 阶和第 n - 2 阶的方法数之和。',
  },
  {
    title: '二分查找',
    tags: ['数组', '二分查找'],
    content:
      '给定一个升序数组 nums 和目标值 target，如果 target 存在则返回下标，否则返回 -1。',
    judgeCase: [
      { input: '[-1,0,3,5,9,12]\n9', output: '4' },
      { input: '[-1,0,3,5,9,12]\n2', output: '-1' },
    ],
    answer:
      '维护左右边界，每次比较中点元素和 target，缩小查找区间。',
  },
  {
    title: '买卖股票的最佳时机',
    tags: ['数组', '贪心'],
    content:
      '给定数组 prices，其中 prices[i] 表示第 i 天股票价格，只允许完成一次买卖，返回最大利润。',
    judgeCase: [
      { input: '[7,1,5,3,6,4]', output: '5' },
      { input: '[7,6,4,3,1]', output: '0' },
    ],
    answer:
      '遍历价格时维护历史最低买入价，并用当前价格计算最大利润。',
  },
  {
    title: '删除排序数组中的重复项',
    tags: ['数组', '双指针'],
    content:
      '给定一个升序数组 nums，原地删除重复元素，使每个元素只出现一次，并返回新长度。',
    judgeCase: [
      { input: '[1,1,2]', output: '2' },
      { input: '[0,0,1,1,1,2,2,3,3,4]', output: '5' },
    ],
    answer:
      '使用慢指针指向去重后数组末尾，快指针扫描新元素。',
  },
  {
    title: '最长公共前缀',
    tags: ['字符串'],
    content:
      '给定一个字符串数组 strs，返回所有字符串的最长公共前缀；如果不存在公共前缀，返回空字符串。',
    judgeCase: [
      { input: '["flower","flow","flight"]', output: 'fl' },
      { input: '["dog","racecar","car"]', output: '' },
    ],
    answer:
      '以第一个字符串为前缀，逐个和后续字符串比较并缩短前缀。',
  },
  {
    title: '回文数',
    tags: ['数学'],
    content:
      '给定一个整数 x，如果 x 是回文整数，返回 true；否则返回 false。',
    judgeCase: [
      { input: '121', output: 'true' },
      { input: '-121', output: 'false' },
    ],
    answer:
      '负数不是回文；可以反转整数后半部分并与前半部分比较。',
  },
  {
    title: '合并两个有序链表',
    tags: ['链表', '递归'],
    content:
      '将两个升序链表合并为一个新的升序链表，并返回合并后的头节点。',
    judgeCase: [
      { input: '[1,2,4]\n[1,3,4]', output: '[1,1,2,3,4,4]' },
      { input: '[]\n[]', output: '[]' },
    ],
    answer:
      '使用虚拟头节点和尾指针，每次选择两个链表中较小的节点追加到结果链表。',
  },
  {
    title: '环形链表',
    tags: ['链表', '快慢指针'],
    content:
      '给定一个链表，判断链表中是否存在环。',
    judgeCase: [
      { input: '[3,2,0,-4]\n1', output: 'true' },
      { input: '[1,2]\n0', output: 'true' },
    ],
    answer:
      '使用快慢指针，如果存在环，快指针最终会追上慢指针。',
  },
  {
    title: '多数元素',
    tags: ['数组', '计数', '分治'],
    content:
      '给定大小为 n 的数组 nums，返回其中出现次数超过 n / 2 的多数元素。',
    judgeCase: [
      { input: '[3,2,3]', output: '3' },
      { input: '[2,2,1,1,1,2,2]', output: '2' },
    ],
    answer:
      '可以使用 Boyer-Moore 投票算法，维护候选值和计数器。',
  },
  {
    title: '移动零',
    tags: ['数组', '双指针'],
    content:
      '给定数组 nums，请将所有 0 移动到数组末尾，同时保持非零元素的相对顺序。',
    judgeCase: [
      { input: '[0,1,0,3,12]', output: '[1,3,12,0,0]' },
      { input: '[0]', output: '[0]' },
    ],
    answer:
      '使用慢指针放置非零元素，最后将剩余位置填充为 0。',
  },
  {
    title: '岛屿数量',
    tags: ['深度优先搜索', '广度优先搜索', '矩阵'],
    content:
      '给定由 1 和 0 组成的二维网格，计算岛屿数量。岛屿由水平或垂直相邻的陆地组成。',
    judgeCase: [
      { input: '[[1,1,1,1,0],[1,1,0,1,0],[1,1,0,0,0],[0,0,0,0,0]]', output: '1' },
      { input: '[[1,1,0,0,0],[1,1,0,0,0],[0,0,1,0,0],[0,0,0,1,1]]', output: '3' },
    ],
    answer:
      '遍历网格，每遇到一块未访问陆地就进行 DFS 或 BFS 标记整座岛屿。',
  },
  {
    title: '零钱兑换',
    tags: ['动态规划', '广度优先搜索'],
    content:
      '给定不同面额的硬币 coins 和总金额 amount，计算凑成总金额所需的最少硬币个数；无法凑成则返回 -1。',
    judgeCase: [
      { input: '[1,2,5]\n11', output: '3' },
      { input: '[2]\n3', output: '-1' },
    ],
    answer:
      '令 dp[i] 表示凑成金额 i 的最少硬币数，枚举硬币更新 dp[i]。',
  },
  {
    title: '课程表',
    tags: ['图', '拓扑排序', '深度优先搜索'],
    content:
      '给定课程总数和先修课程数组，判断是否可以完成所有课程。',
    judgeCase: [
      { input: '2\n[[1,0]]', output: 'true' },
      { input: '2\n[[1,0],[0,1]]', output: 'false' },
    ],
    answer:
      '将先修关系建图，使用入度队列做拓扑排序；若访问课程数等于总数则可完成。',
  },
  {
    title: '最长递增子序列',
    tags: ['动态规划', '二分查找'],
    content:
      '给定整数数组 nums，找到其中最长严格递增子序列的长度。',
    judgeCase: [
      { input: '[10,9,2,5,3,7,101,18]', output: '4' },
      { input: '[0,1,0,3,2,3]', output: '4' },
    ],
    answer:
      '维护 tails 数组，tails[i] 表示长度为 i + 1 的递增子序列的最小结尾值。',
  },
  {
    title: '最小路径和',
    tags: ['数组', '动态规划', '矩阵'],
    content:
      '给定一个包含非负整数的 m x n 网格，每次只能向下或向右移动一步，返回从左上角到右下角的最小路径和。',
    judgeCase: [
      { input: '[[1,3,1],[1,5,1],[4,2,1]]', output: '7' },
      { input: '[[1,2,3],[4,5,6]]', output: '12' },
    ],
    answer:
      '原地或使用 dp 数组累加从上方和左方转移来的较小路径和。',
  },
]

const judgeConfig = {
  memoryLimit: 256,
  stackLimit: 128,
  timeLimit: 1000,
}

async function addQuestion(question, index) {
  const response = await fetch(`${API_BASE}/api/question/add`, {
    body: JSON.stringify({
      ...question,
      judgeConfig,
    }),
    headers: {
      'Content-Type': 'application/json',
      ...(OJ_COOKIE ? { Cookie: OJ_COOKIE } : {}),
    },
    method: 'POST',
  })

  const result = await response.json().catch(() => undefined)

  if (!response.ok || result?.code !== 0) {
    const message = result?.message || `${response.status} ${response.statusText}`
    throw new Error(`第 ${index + 1} 条「${question.title}」添加失败：${message}`)
  }

  return result.data
}

async function main() {
  console.log(`准备向 ${API_BASE}/api/question/add 添加 ${questions.length} 条题目`)

  const ids = []

  for (const [index, question] of questions.entries()) {
    const id = await addQuestion(question, index)
    ids.push(id)
    console.log(`已添加 ${index + 1}/${questions.length}: ${question.title} #${id}`)
  }

  console.log(`完成，共添加 ${ids.length} 条题目`)
}

main().catch((error) => {
  console.error(error.message)
  console.error('如果接口需要管理员登录态，请设置 OJ_COOKIE="浏览器中的 Cookie" 后重试。')
  process.exitCode = 1
})
