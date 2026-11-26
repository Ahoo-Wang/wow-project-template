package me.ahoo.wow.template.server

import me.ahoo.wow.api.caller.Caller

/** 测试中的调用方：作者是 owner 级调用方，只触及自己的文章；编辑是带 editor 角色的租户级调用方。 */
object Callers {
    fun author(name: String): Caller = Caller.owner(name)

    val EDITOR: Caller = Caller.of(subject = "erin", roles = listOf(BlogAuthorization.EDITOR))
}
