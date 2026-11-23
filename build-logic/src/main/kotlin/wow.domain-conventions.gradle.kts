/**
 * 领域模型（domain）：可发布库 + Wow 插件。插件以 wow-compiler 运行 KSP（模型、处理器绑定、绑定索引与 Manifest 片段），
 * 并引入 wow-kernel 与同版本的 wow-bom。
 */
plugins {
    id("wow.library-conventions")
    id("me.ahoo.wow")
}
