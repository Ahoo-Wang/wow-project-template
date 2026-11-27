package me.ahoo.wow.template.domain.article

import me.ahoo.wow.api.annotation.AggregateRoot
import me.ahoo.wow.api.annotation.Description
import me.ahoo.wow.api.annotation.ErrorCode
import me.ahoo.wow.api.annotation.OnCommand
import me.ahoo.wow.api.decide.DecideScope
import me.ahoo.wow.api.decide.reject
import me.ahoo.wow.api.lifecycle.AggregateDeleted
import me.ahoo.wow.api.lifecycle.AggregateRestored
import me.ahoo.wow.api.lifecycle.TransferOwner
import me.ahoo.wow.api.lifecycle.TransferSpace
import me.ahoo.wow.template.api.article.ArticleCreated
import me.ahoo.wow.template.api.article.ArticleEdited
import me.ahoo.wow.template.api.article.ArticlePublished
import me.ahoo.wow.template.api.article.ArticleReturned
import me.ahoo.wow.template.api.article.ArticleStatus
import me.ahoo.wow.template.api.article.ArticleSubmitted
import me.ahoo.wow.template.api.article.CreateArticle
import me.ahoo.wow.template.api.article.DeleteArticle
import me.ahoo.wow.template.api.article.EditArticle
import me.ahoo.wow.template.api.article.PublishArticle
import me.ahoo.wow.template.api.article.RestoreArticle
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
 *
 * 生命周期：内置的 `DeleteAggregate` 与 `RestoreAggregate` 都不经过领域代码，前者不会释放 slug，后者不会重新预留，
 * 恢复出的文章可能与别人的文章同 slug。所以 `lifecycle` 去掉了这两个，改由领域命令 [DeleteArticle] 与 [RestoreArticle]
 * 删除和恢复；转移 owner 与空间仍是内置命令。
 */
@AggregateRoot(lifecycle = [TransferOwner::class, TransferSpace::class])
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

    /**
     * 释放 slug，并以生命周期事件 `AggregateDeleted` 删除文章：与内置删除效果相同，此后文章拒绝一切命令。
     * 释放随事件一起提交，追加失败则不生效。
     */
    @Suppress("UnusedParameter") // 命令没有字段：函数处理的是它的类型。
    fun onCommand(command: DeleteArticle, scope: DecideScope): AggregateDeleted {
        scope.release(SLUGS, state.slug)
        return AggregateDeleted()
    }

    /**
     * 恢复已删除的文章（`onDeleted = true`：已删除的聚合只接受内置恢复与这样标注的命令）：重新预留 slug，
     * 再以生命周期事件 `AggregateRestored` 恢复。预留随事件一起提交。
     *
     * 审核中删除（撤稿）的文章不能恢复：它的审核可能已经出了结果，而审核 Saga 接受了“文章已删除”的拒绝，
     * 恢复出来就会永远停在审核中。领域看不出聚合是否已删除，所以对审核中的文章总是拒绝，无论它是否已删除。
     * 对没有删除的草稿或已发布文章，`AggregateRestored` 不改变任何东西，内核不提交它。
     */
    @Suppress("UnusedParameter") // 命令没有字段：函数处理的是它的类型。
    @OnCommand(onDeleted = true)
    suspend fun onCommand(command: RestoreArticle, scope: DecideScope): AggregateRestored {
        if (state.status == ArticleStatus.IN_REVIEW) {
            reject(Rejections.WRONG_STATUS, "An article withdrawn from review cannot be restored.")
        }
        if (!scope.reserve(SLUGS, state.slug)) {
            reject(Rejections.SLUG_TAKEN, "Another article is at the slug \"${state.slug}\".")
        }
        return AggregateRestored()
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
