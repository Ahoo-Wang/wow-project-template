# API Client

这是一个基于 TypeScript 的 API 客户端库，用于与服务端进行交互。客户端由 Wow 的 `wow-generator` 根据服务的 Manifest 基线（`server/manifest/baseline.json`）生成，运行在 `@ahoo-wang/wow-client` 之上：每个聚合一套命令、快照查询、事件查询与状态客户端，错误码与等待计划都有类型。

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

3. **生成 API 客户端代码**（读取提交的 Manifest 基线，无需启动服务端；服务端契约变化后先执行 `./gradlew server:updateWowManifestBaseline`）：

   ```bash
   pnpm generate
   ```

4. **构建库**：

   ```bash
   pnpm build
   ```

5. **运行测试**（可选）：

   ```bash
   pnpm test
   ```

6. **发布**（可选）：

   ```bash
   pnpm release
   ```

## 开发指南

### 环境要求

- Node.js 24 或更高版本
- pnpm 包管理器

### 常用脚本

- `pnpm generate` - 从服务端的 Manifest 基线生成客户端代码（提交 `src/generated` 与其中的 `.wow-generator.json`）
- `pnpm build` - 编译 TypeScript 并构建库文件
- `pnpm test` - 执行单元测试并生成覆盖率报告
- `pnpm lint` - 使用 ESLint 检查代码并自动修复问题
- `pnpm format` - 使用 Prettier 格式化代码
- `pnpm clean` - 清理构建产物和生成的客户端代码
- `pnpm release:patch` - 发布补丁版本更新
