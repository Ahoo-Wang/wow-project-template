package me.ahoo.wow.template.api.review

import me.ahoo.wow.api.annotation.Summary

/**
 * 审核通过：`POST /demo/review/{id}/approve-review`。
 */
@Summary("审核通过")
data class ApproveReview(
    val comment: String = ""
)

@Summary("审核已通过")
data class ReviewApproved(
    val demoId: String,
    val comment: String
)
