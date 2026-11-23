package me.ahoo.wow.template.api.review

import me.ahoo.wow.api.annotation.Summary

@Summary("A review was opened")
data class ReviewCreated(
    val articleId: String,
)

@Summary("A review approved its article")
data class ReviewApproved(
    val articleId: String,
    val comment: String,
)

@Summary("A review rejected its article")
data class ReviewRejected(
    val articleId: String,
    val reason: String,
)

@Summary("A review expired undecided")
data class ReviewExpired(
    val articleId: String,
)
