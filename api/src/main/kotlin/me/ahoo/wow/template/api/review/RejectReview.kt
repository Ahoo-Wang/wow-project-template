package me.ahoo.wow.template.api.review

import jakarta.validation.constraints.NotBlank
import me.ahoo.wow.api.annotation.CommandRoute
import me.ahoo.wow.api.annotation.Event
import me.ahoo.wow.api.annotation.Summary

@Summary("审核驳回")
@CommandRoute(
    action = "reject",
    appendIdPath = CommandRoute.AppendPath.ALWAYS,
)
data class RejectReview(
    @field:NotBlank
    val reason: String
)

@Event
data class ReviewRejected(
    val demoId: String,
    val reason: String
)
