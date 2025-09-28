# Api Client

## 快速开始

1. **进入客户端目录**：

   ```bash
   cd client
   ```

2. **安装依赖**：

   ```bash
   pnpm install
   ```

3. **启动 Server**（用于提供API文档）

4. **生成API客户端代码**：

   ```bash
   pnpm generate
   ```

5. **构建库**：

   ```bash
   pnpm build
   ```

6. **运行测试**：
   ```bash
   pnpm test
   ```

7. **发布**：
   ```bash
   pnpm release
   ```

## 开发指南

### 环境要求

- Node.js 18+
- pnpm

### 常用脚本

- `pnpm generate` - 从API文档生成客户端代码（需服务器运行）
- `pnpm build` - 构建TypeScript库
- `pnpm test` - 运行单元测试和覆盖率
- `pnpm lint` - ESLint代码检查和自动修复
- `pnpm format` - Prettier代码格式化
- `pnpm clean` - 清理构建产物和生成代码
- `pnpm release:patch` - 发布补丁版本