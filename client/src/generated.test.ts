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

// @vitest-environment node

/*
 * src/generated/<context>/ 是 wow-generator 由服务的 Manifest 基线逐字节生成的结果。服务端的 `check` 保证基线与服务一致，
 * 所以服务的路由或数据形状一变，这里就失败，直到重新生成（pnpm generate）。
 */

import { mkdtempSync, readdirSync, readFileSync, rmSync } from "node:fs";
import { tmpdir } from "node:os";
import { join, relative } from "node:path";
import { afterAll, describe, expect, it } from "vitest";
// @ts-expect-error The script is plain JavaScript.
import { generate, SERVICES } from "../scripts/generate.mjs";

function files(directory: string): Map<string, string> {
  return new Map(
    readdirSync(directory, { recursive: true, withFileTypes: true })
      .filter((entry) => entry.isFile())
      .map((entry) => {
        const path = join(entry.parentPath, entry.name);
        return [relative(directory, path), readFileSync(path, "utf8")];
      }),
  );
}

/** 每个用例在独立进程中运行一次 wow-generator，在繁忙的 CI 机器上可能超过 vitest 默认的 5 秒。 */
const GENERATE_TIMEOUT = 30_000;

describe("the generated clients", () => {
  const output = mkdtempSync(join(tmpdir(), "wow-template-client-generated-"));
  afterAll(() => rmSync(output, { recursive: true, force: true }));

  it.each(Object.keys(SERVICES))(
    "of %s are what wow-generator writes from its Manifest baseline",
    (context) => {
      generate(context, join(output, context));
      const expected = files(join(output, context));
      const actual = files(join(import.meta.dirname, "generated", context));
      expect([...actual.keys()].sort()).toEqual([...expected.keys()].sort());
      for (const [path, content] of expected)
        expect(actual.get(path), path).toBe(content);
    },
    GENERATE_TIMEOUT,
  );
});
