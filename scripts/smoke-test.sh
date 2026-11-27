#!/usr/bin/env bash
#
# 冒烟测试：对一个正在运行、启用了 dev profile 的博客服务发送真实命令，确认它能工作。
# CI 用它测试构建出的镜像；本地可以测 `./gradlew :blog-server:run` 启动的服务：
#
#   scripts/smoke-test.sh                        # 默认 http://localhost:8080
#   scripts/smoke-test.sh http://localhost:18080
#
# 依赖 curl 与 jq。每次运行用新的文章 ID 与 slug，可以对同一个服务重复运行。
set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
TIMEOUT="${SMOKE_TIMEOUT:-90}"
RUN="$(date +%s)-$$"
ARTICLE="smoke-${RUN}"
JSON=(-H 'Content-Type: application/json')

fail() {
  echo "smoke test failed: $*" >&2
  exit 1
}

command -v jq > /dev/null || fail "jq is required"

echo "waiting for ${BASE_URL}/actuator/health (up to ${TIMEOUT}s)"
for ((i = 1; i <= TIMEOUT; i++)); do
  if curl -fs "${BASE_URL}/actuator/health" | jq -e '.status == "UP"' > /dev/null 2>&1; then
    echo "healthy after ${i}s"
    break
  fi
  [[ ${i} -eq ${TIMEOUT} ]] && fail "the service is not healthy after ${TIMEOUT}s"
  sleep 1
done

# 作者创建并提交文章。等待计划跟着审核 Saga，一直等到审核写入快照，回答中的 ref 就是这次审核。
curl -fsS -X POST "${BASE_URL}/blog/article/${ARTICLE}/create-article" "${JSON[@]}" \
  -d "{\"title\":\"Smoke\",\"body\":\"The service serves real commands.\",\"slug\":\"${ARTICLE}\"}" > /dev/null \
  || fail "create-article"
REVIEW="$(curl -fsS -X POST "${BASE_URL}/blog/article/${ARTICLE}/submit-article" "${JSON[@]}" \
  -H 'Wow-Wait: handled(ArticleReviewSaga.onArticleSubmitted)>snapshot' -d '{}' | jq -r .ref.id)" \
  || fail "submit-article"
[[ -n "${REVIEW}" && "${REVIEW}" != "null" ]] || fail "submit-article answered no review id"

curl -fsS "${BASE_URL}/blog/article/${ARTICLE}/state" | jq -e '.state.status == "IN_REVIEW"' > /dev/null \
  || fail "the article is not IN_REVIEW"
curl -fsS "${BASE_URL}/blog/review/${REVIEW}/state" | jq -e ".state.status == \"PENDING\" and .state.articleId == \"${ARTICLE}\"" > /dev/null \
  || fail "the review is not PENDING for ${ARTICLE}"

status() { curl -s -o /dev/null -w '%{http_code}' "$@"; }

# 内部命令没有公开路由；匿名调用方不是编辑，不能裁决审核；命令体必须是 JSON。
[[ "$(status -X POST "${BASE_URL}/blog/article/${ARTICLE}/publish-article" "${JSON[@]}" -d '{"reviewId":"x"}')" == 404 ]] \
  || fail "an internal command has a public route"
[[ "$(status -X POST "${BASE_URL}/blog/review/${REVIEW}/approve-review" "${JSON[@]}" -d '{}')" == 403 ]] \
  || fail "a caller who is not an editor decided a review"
[[ "$(status -X POST "${BASE_URL}/blog/article/${ARTICLE}-x/create-article" -H 'Content-Type: text/plain' -d 'x')" == 415 ]] \
  || fail "a command body that is not JSON was not refused"

echo "smoke test passed (article ${ARTICLE}, review ${REVIEW})"
