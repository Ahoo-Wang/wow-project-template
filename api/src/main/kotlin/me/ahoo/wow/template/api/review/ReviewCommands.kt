package me.ahoo.wow.template.api.review

import jakarta.validation.constraints.NotBlank
import me.ahoo.wow.api.annotation.CreateAggregate
import me.ahoo.wow.api.annotation.InternalCommand
import me.ahoo.wow.api.annotation.Summary

/** 审核 Saga 为提交的文章开启审核，审核 ID 由服务端生成：内部命令，客户端不能发送。 */
@CreateAggregate
@InternalCommand
@Summary("Open a review")
data class CreateReview(
    val articleId: String,
)

/** 编辑批准审核中的文章。 */
@Summary("Approve a review")
data class ApproveReview(
    val comment: String = "",
)

/** 编辑驳回审核中的文章，并说明理由。 */
@Summary("Reject a review")
data class RejectReview(
    @field:NotBlank val reason: String,
)

/** 审核到期后由它自己的定时命令使其过期：内部命令，客户端不能发送。 */
@InternalCommand
@Summary("Expire a review")
data object ExpireReview
