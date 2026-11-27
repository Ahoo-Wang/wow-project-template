package me.ahoo.wow.template.api.article

import me.ahoo.wow.api.annotation.Summary

// 事件是已提交的事实：自包含、用过去式、永久保存。事件不与命令共享接口，各自独立演进
// （已发布的事件要改结构，只能加 @Revision 与 @Upgrade，不能原地修改）。

@Summary("An article was created")
data class ArticleCreated(
    val title: String,
    val body: String,
    val slug: String,
)

@Summary("An article's text was edited")
data class ArticleEdited(
    val title: String,
    val body: String,
)

@Summary("An article was submitted for review")
data object ArticleSubmitted

@Summary("An article was published")
data class ArticlePublished(
    val reviewId: String,
)

@Summary("An article was returned to its author")
data class ArticleReturned(
    val reviewId: String,
    val reason: String,
)
