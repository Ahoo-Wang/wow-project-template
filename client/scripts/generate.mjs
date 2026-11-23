/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// 由服务的 Manifest 基线（server/manifest/baseline.json，服务端的 `check` 保证它与服务一致）生成类型化客户端，
// 写入 src/generated/<上下文>/：每个服务一次 wow-generator，各占一个目录。
//
//   pnpm generate
//
// 生成结果逐字节提交；src/generated.test.ts 在它与本脚本的输出不一致时失败。

import { execFileSync } from 'node:child_process';
import { rmSync } from 'node:fs';
import { join } from 'node:path';
import { fileURLToPath } from 'node:url';

const CLIENT = fileURLToPath(new URL('..', import.meta.url));
const CLI = join(CLIENT, 'node_modules', '@ahoo-wang', 'wow-generator', 'dist', 'cli.js');

/** 上下文名及其服务的 Manifest 基线。 */
export const SERVICES = {
  blog: join(CLIENT, '..', 'server', 'manifest', 'baseline.json'),
};

/** 把 `context` 的客户端写入 `output`。 */
export function generate(context, output) {
  execFileSync(
    process.execPath,
    [CLI, 'generate', '--input', SERVICES[context], '--output', output, '--strict', '--quiet'],
    { stdio: 'inherit' },
  );
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  for (const context of Object.keys(SERVICES)) {
    const output = join(CLIENT, 'src', 'generated', context);
    rmSync(output, { recursive: true, force: true });
    generate(context, output);
  }
}
