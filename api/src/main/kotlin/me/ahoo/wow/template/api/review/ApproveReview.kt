package me.ahoo.wow.template.api.review

import me.ahoo.wow.api.annotation.CommandRoute
import me.ahoo.wow.api.annotation.Event
import me.ahoo.wow.api.annotation.Summary

@Summary("审核通过")
@CommandRoute(
    action = "approve",
    appendIdPath = CommandRoute.AppendPath.ALWAYS,
)
data class ApproveReview(
    val comment: String = ""
)

@Event
data class ReviewApproved(
    val demoId: String,
    val comment: String
)
