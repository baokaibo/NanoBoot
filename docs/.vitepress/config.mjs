import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'NanoBoot Framework',
  description: 'A Spring Boot-like micro-framework for Java development',
  base: '/nanoboot/',
  locales: {
    root: {
      label: 'English',
      lang: 'en-US',
      title: 'NanoBoot Framework',
      description: 'A Spring Boot-like micro-framework for Java development',
      themeConfig: {
        nav: [
          { text: 'Home', link: '/' },
          { text: 'Guide', link: '/guide/getting-started' },
          { text: 'Modules', link: '/modules/core' },
          { text: 'API', link: '/api/' },
          { text: 'CLI', link: '/cli/introduction' },
          { text: '中文', link: '/zh/' }
        ],
        sidebar: {
          '/': [
            {
              text: 'Getting Started',
              items: [
                { text: 'Introduction', link: '/guide/introduction' },
                { text: 'Quick Start', link: '/guide/getting-started' },
                { text: 'Project Structure', link: '/guide/project-structure' }
              ]
            },
            {
              text: 'Essentials',
              items: [
                { text: 'Dependency Injection', link: '/guide/dependency-injection' },
                { text: 'Web Development', link: '/guide/web-development' },
                { text: 'Data Access', link: '/guide/data-access' },
                { text: 'WebSocket', link: '/guide/websocket' }
              ]
            },
            {
              text: 'Core Modules',
              items: [
                { text: 'Core', link: '/modules/core' },
                { text: 'Starter', link: '/modules/starter' },
                { text: 'Web', link: '/modules/web' },
                { text: 'Data', link: '/modules/data' },
                { text: 'WebSocket', link: '/modules/websocket' }
              ]
            },
            {
              text: 'CLI Tool',
              items: [
                { text: 'Introduction', link: '/cli/introduction' },
                { text: 'Installation', link: '/cli/installation' },
                { text: 'Usage', link: '/cli/usage' },
                { text: 'Commands', link: '/cli/commands' }
              ]
            },
            {
              text: 'API Reference',
              items: [
                { text: 'Annotations', link: '/api/annotations' },
                { text: 'Configuration', link: '/api/configuration' },
                { text: 'Utilities', link: '/api/utilities' }
              ]
            }
          ]
        }
      }
    },
    zh: {
      label: '中文',
      lang: 'zh-CN',
      title: 'NanoBoot 框架',
      description: '一个类似 Spring Boot 的 Java 微框架',
      themeConfig: {
        nav: [
          { text: '首页', link: '/zh/' },
          { text: '指南', link: '/zh/guide/getting-started' },
          { text: '模块', link: '/zh/modules/core' },
          { text: 'API', link: '/zh/api/' },
          { text: 'CLI', link: '/zh/cli/introduction' },
          { text: 'English', link: '/' }
        ],
        sidebar: {
          '/zh/': [
            {
              text: '入门指南',
              items: [
                { text: '简介', link: '/zh/guide/introduction' },
                { text: '快速开始', link: '/zh/guide/getting-started' },
                { text: '项目结构', link: '/zh/guide/project-structure' }
              ]
            },
            {
              text: '核心功能',
              items: [
                { text: '依赖注入', link: '/zh/guide/dependency-injection' },
                { text: 'Web 开发', link: '/zh/guide/web-development' },
                { text: '数据访问', link: '/zh/guide/data-access' },
                { text: 'WebSocket', link: '/zh/guide/websocket' }
              ]
            },
            {
              text: '核心模块',
              items: [
                { text: 'Core', link: '/zh/modules/core' },
                { text: 'Starter', link: '/zh/modules/starter' },
                { text: 'Web', link: '/zh/modules/web' },
                { text: 'Data', link: '/zh/modules/data' },
                { text: 'WebSocket', link: '/zh/modules/websocket' }
              ]
            },
            {
              text: 'CLI 工具',
              items: [
                { text: '简介', link: '/zh/cli/introduction' },
                { text: '安装', link: '/zh/cli/installation' },
                { text: '使用', link: '/zh/cli/usage' },
                { text: '命令', link: '/zh/cli/commands' }
              ]
            },
            {
              text: 'API 参考',
              items: [
                { text: '注解', link: '/zh/api/annotations' },
                { text: '配置', link: '/zh/api/configuration' },
                { text: '工具类', link: '/zh/api/utilities' }
              ]
            }
          ]
        }
      }
    }
  },
  head: [
    ['meta', { name: 'theme-color', content: '#3eaf7c' }],
    ['link', { rel: 'icon', href: '/favicon.ico' }]
  ]
})