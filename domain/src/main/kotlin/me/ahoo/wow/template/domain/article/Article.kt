package me.ahoo.wow.template.domain.article

import me.ahoo.wow.api.annotation.AggregateRoot
import me.ahoo.wow.api.annotation.Description
import me.ahoo.wow.api.annotation.ErrorCode
import me.ahoo.wow.api.decide.DecideScope
import me.ahoo.wow.api.decide.reject
import me.ahoo.wow.template.api.article.ArticleCreated
import me.ahoo.wow.template.api.article.ArticleEdited
import me.ahoo.wow.template.api.article.ArticlePublished
import me.ahoo.wow.template.api.article.ArticleReturned
import me.ahoo.wow.template.api.article.ArticleStatus
import me.ahoo.wow.template.api.article.ArticleSubmitted
import me.ahoo.wow.template.api.article.CreateArticle
import me.ahoo.wow.template.api.article.EditArticle
import me.ahoo.wow.template.api.article.PublishArticle
import me.ahoo.wow.template.api.article.ReturnArticle
import me.ahoo.wow.template.api.article.SubmitArticle

/**
 * 作者的一篇文章：
 *
 * ```text
 * DRAFT -> IN_REVIEW -> PUBLISHED
 *   ^          |
 *   +----------+  ReturnArticle：审核被驳回或超时
 * ```
 *
 * 聚合只做决策：基于命令与当前状态校验规则，返回事件；状态只由 [ArticleState] 的溯源函数修改。
 * 文章属于创建它的作者（默认的 `Ownership.OWNER`）：其他作者既读不到，也改不了（404）。
 * 删除与恢复是内置的生命周期命令（`DELETE /blog/article/{id}`），无需在此声明。
 */
@AggregateRoot
@Description("作者的一篇文章，从草稿到发布")
class Article(private val state: ArticleState) {

    /** slug 在租户内唯一：在决策时预留，预留随事件一起提交，追加失败则释放。 */
    suspend fun onCommand(command: CreateArticle, scope: DecideScope): ArticleCreated {
        if (!scope.reserve(SLUGS, command.slug)) {
            reject(Rejections.SLUG_TAKEN, "Another article is at the slug \"${command.slug}\".")
        }
        return ArticleCreated(title = command.title, body = command.body, slug = command.slug)
    }

    /** 文字没有变化就什么也不提交：返回 `null`。 */
    fun onCommand(command: EditArticle): ArticleEdited? {
        requireStatus(ArticleStatus.DRAFT, "be edited")
        if (command.title == state.title && command.body == state.body) {
            return null
        }
        return ArticleEdited(title = command.title, body = command.body)
    }

    @Suppress("UnusedParameter") // 命令没有字段：函数处理的是它的类型。
    fun onCommand(command: SubmitArticle): ArticleSubmitted {
        requireStatus(ArticleStatus.DRAFT, "be submitted")
        return ArticleSubmitted
    }

    fun onCommand(command: PublishArticle): ArticlePublished {
        requireStatus(ArticleStatus.IN_REVIEW, "be published")
        return ArticlePublished(reviewId = command.reviewId)
    }

    fun onCommand(command: ReturnArticle): ArticleReturned {
        requireStatus(ArticleStatus.IN_REVIEW, "be returned")
        return ArticleReturned(reviewId = command.reviewId, reason = command.reason)
    }

    private fun requireStatus(required: ArticleStatus, action: String) {
        if (state.status != required) {
            reject(Rejections.WRONG_STATUS, "The article is ${state.status}: only a $required article can $action.")
        }
    }

    companion object {
        /** slug 的预留命名空间：`<上下文>.<键>`，预留默认在租户内唯一。 */
        const val SLUGS = "blog.article-slug"
    }

    /**
     * 拒绝码（本地名）：编译器与内核将其命名为 `blog.article.<code>`，调用方从 `BlogContract.Article.Errors` 读取。
     * 与当前状态冲突用 409；违反请求规则用默认的 422。
     */
    object Rejections {
        @ErrorCode(description = "Another article of the tenant is at the slug.", status = 409)
        const val SLUG_TAKEN = "slug-taken"

        @ErrorCode(description = "The article's status does not allow the command.", status = 409)
        const val WRONG_STATUS = "wrong-status"
    }
}
