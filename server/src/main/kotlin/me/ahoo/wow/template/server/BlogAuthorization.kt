package me.ahoo.wow.template.server

import me.ahoo.wow.api.caller.Caller
import me.ahoo.wow.runtime.admission.Authorization
import me.ahoo.wow.runtime.admission.CommandAction
import me.ahoo.wow.runtime.admission.CommandAuthorizer
import me.ahoo.wow.runtime.admission.commandIs
import me.ahoo.wow.template.api.review.ApproveReview
import me.ahoo.wow.template.api.review.RejectReview
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 谁能发送哪些命令。
 *
 * 内核已经把每个调用方限制在自己的作用域内：作者只能读写自己的文章（Ownership.OWNER）。
 * 这里只补上内核不知道的业务角色，每条规则一个授权器：
 * - [reviewAuthorizer]：只有编辑能裁决审核；其余命令它不表态（放行）。
 * - [operationalAuthorizer]：Wow 的运维命令（定时任务、快照重建、停放条目的操作与读取）只有编辑能发送。
 *   运维命令只由声明了裁决它们的授权器（`CommandAuthorizer.operational`）决定；没有这样的授权器，公开端口一律 403。
 *
 * 角色取自调用方（`Caller.hasRole`）：开发 profile 下来自 `Wow-Roles` 请求头，cosec profile 下来自 CoSec 网关转发的身份。
 */
@Configuration(proxyBeanMethods = false)
class BlogAuthorization {
    @Bean
    fun reviewAuthorizer(): CommandAuthorizer = CommandAuthorizer { caller, action ->
        if (action.commandIs<ApproveReview>() || action.commandIs<RejectReview>()) {
            requireEditor(caller, action)
        } else {
            Authorization.Allowed
        }
    }

    @Bean
    fun operationalAuthorizer(): CommandAuthorizer = CommandAuthorizer.operational { caller, action ->
        requireEditor(caller, action)
    }

    companion object {
        /** 编辑的角色。 */
        const val EDITOR = "editor"

        private fun requireEditor(caller: Caller, action: CommandAction): Authorization =
            if (caller.hasRole(EDITOR)) {
                Authorization.Allowed
            } else {
                Authorization.Denied.of("Only an editor sends ${action.type.name}; ${caller.subject} is not one.")
            }
    }
}
