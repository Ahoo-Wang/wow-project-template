package me.ahoo.wow.template.api.review

import jakarta.validation.constraints.NotBlank
import me.ahoo.wow.api.annotation.Summary

/**
 * 审核驳回：`POST /demo/review/{id}/reject-review`。
 */
@Summary("审核驳回")
data class RejectReview(
    @field:NotBlank
    val reason: String
)

@Summary("审核已驳回")
data class ReviewRejected(
    val demoId: String,
    val reason: String
)
