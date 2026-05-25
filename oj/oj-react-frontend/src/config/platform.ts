const platformName = 'CodeForge'
const platformDescription = '在线代码评测平台'
const platformTrainingDescription =
  '通过持续的题目训练，帮助用户不断锻造编程能力，提升算法思维与工程实践水平。'

export const platformConfig = {
  auth: {
    loginDescription: `使用你的 ${platformName} 账号进入控制台`,
  },
  description: platformDescription,
  footer: {
    copyrightName: platformName,
    description: platformDescription,
  },
  header: {
    // subtitle: 'Practice',
  },
  home: {
    heroBadge: platformName,
    heroDescription: platformTrainingDescription,
    heroTitle: [''],
  },
  legal: {
    termsDescription: `使用 ${platformName} 即表示你同意遵守平台的基本使用规则，并对自己的账号和提交内容负责。`,
  },
  meta: {
    description: platformTrainingDescription,
    title: platformName,
  },
  name: platformName,
} as const
