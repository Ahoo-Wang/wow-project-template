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
 * 这里只补上内核不知道的业务角色：
 * - 只有编辑能裁决审核。
 * - Wow 的运维命令（快照重建、定时任务、对停放条目的操作）也只有编辑能发送。
 *
 * 其余命令一律放行：一个授权器只表态自己关心的命令。
 */
@Configuration(proxyBeanMethods = false)
class BlogAuthorization {
    @Bean
    fun blogAuthorizer(): CommandAuthorizer = CommandAuthorizer { caller, action ->
        when {
            action.isOperational -> requireEditor(caller, action)
            action.commandIs<ApproveReview>() || action.commandIs<RejectReview>() -> requireEditor(caller, action)
            else -> Authorization.Allowed
        }
    }

    companion object {
        /** 编辑的角色：开发 profile 下取自 `Wow-Roles` 请求头，cosec profile 下取自 CoSec 网关转发的身份。 */
        const val EDITOR = "editor"

        /** 调用方的角色所在的属性：Wow 的开发身份头与 CoSec 的调用方解析器都把角色放在这里。 */
        const val ROLES = "roles"

        private fun requireEditor(caller: Caller, action: CommandAction): Authorization =
            if (EDITOR in caller.attributes[ROLES].orEmpty()) {
                Authorization.Allowed
            } else {
                Authorization.Denied.of("Only an editor sends ${action.type.name}; ${caller.subject} is not one.")
            }
    }
}
