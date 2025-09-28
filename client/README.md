# API Client

这是一个基于 TypeScript 的 API 客户端库，用于与服务端进行交互。它通过 OpenAPI 规范自动生成客户端代码，简化服务开发者的集成工作。

## 快速开始

按照以下步骤快速设置和使用 API 客户端：

1. **进入客户端目录**：

   ```bash
   cd client
   ```

2. **安装依赖**：

   ```bash
   pnpm install
   ```

3. **启动服务端**（提供 API 文档）：

4. **生成 API 客户端代码**：

   ```bash
   pnpm generate
   ```

5. **构建库**：

   ```bash
   pnpm build
   ```

6. **运行测试**（可选）：

   ```bash
   pnpm test
   ```

7. **发布**（可选）：

   ```bash
   pnpm release
   ```

## 开发指南

### 环境要求

- Node.js 18 或更高版本
- pnpm 包管理器

### 常用脚本

- `pnpm generate` - 从运行中的服务端 API 文档生成客户端代码
- `pnpm build` - 编译 TypeScript 并构建库文件
- `pnpm test` - 执行单元测试并生成覆盖率报告
- `pnpm lint` - 使用 ESLint 检查代码并自动修复问题
- `pnpm format` - 使用 Prettier 格式化代码
- `pnpm clean` - 清理构建产物和生成的客户端代码
- `pnpm release:patch` - 发布补丁版本更新
