package me.ahoo.wow.template.api.article

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import me.ahoo.wow.api.annotation.CreateAggregate
import me.ahoo.wow.api.annotation.InternalCommand
import me.ahoo.wow.api.annotation.Summary

/**
 * 作者创建一篇草稿。[slug] 是文章的地址，在租户内唯一：由文章在决策时预留。
 *
 * 命令体只含业务数据：作者就是调用方（文章的 owner），租户取自调用方，ID 来自路由
 * （`POST /blog/article/{id}/create-article`）或由服务端生成（`POST /blog/article/create-article`）。
 * 命令的形状由约束在准入时校验，早于聚合决策。
 */
@CreateAggregate
@Summary("Create an article")
data class CreateArticle(
    @field:NotBlank @field:Size(max = TITLE_MAX) val title: String,
    @field:NotBlank val body: String,
    @field:Pattern(regexp = SLUG_PATTERN) val slug: String,
) {
    companion object {
        const val TITLE_MAX = 200

        /** 以单个连字符连接的小写单词，如地址中的 `hello-wow`。 */
        const val SLUG_PATTERN = "^[a-z0-9]+(-[a-z0-9]+)*$"
    }
}

/** 作者修改草稿的文字。slug 不变：它是文章的地址。 */
@Summary("Edit an article")
data class EditArticle(
    @field:NotBlank @field:Size(max = CreateArticle.TITLE_MAX) val title: String,
    @field:NotBlank val body: String,
)

/**
 * 作者删除文章：释放它的 slug，删除不可恢复。审核中的文章也可以删除，相当于撤稿；审核结果随后到达时，
 * 审核 Saga 接受“文章已删除”的拒绝。
 */
@Summary("Delete an article")
data object DeleteArticle

/** 作者提交草稿以供审核。 */
@Summary("Submit an article for review")
data object SubmitArticle

/** 审核 Saga 发布审核通过的文章：内部命令，客户端不能发送。 */
@InternalCommand
@Summary("Publish an article")
data class PublishArticle(
    val reviewId: String,
)

/** 审核被驳回或超时，审核 Saga 把文章退回作者：内部命令，客户端不能发送。 */
@InternalCommand
@Summary("Return an article to its author")
data class ReturnArticle(
    val reviewId: String,
    val reason: String,
)
