/**
 * 发布语言（api）：可发布库 + Wow 的 api 插件。插件装配所声明 domain 模块的契约（META-INF/wow/contract.json），
 * 由契约编译 `<Context>Contract` 与 `<State>View`，作为模块的 `contract` 特性发布。此处不运行 KSP。
 */
plugins {
    id("wow.library-conventions")
    id("me.ahoo.wow.api")
}
