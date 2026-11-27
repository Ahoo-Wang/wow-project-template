package me.ahoo.wow.template.domain.review

import kotlin.time.Duration

/**
 * 审核策略：领域服务。命令函数以参数接收它，生成的模型工厂（`reviewModel(reviewPolicy = ...)`）注入它，
 * 宿主由配置 `blog.review.timeout` 构造成 bean，测试直接传入。
 *
 * @property timeout 审核从开启到过期的时长。
 */
data class ReviewPolicy(val timeout: Duration) {
    init {
        require(timeout.isPositive()) { "A review's timeout must be positive, not $timeout." }
    }
}
