plugins {
    // me.ahoo.wow.api：引入 wow-api，并把下面声明的 domain 模块的契约编译为 `BlogContract`
    // 与状态视图（`ArticleStateView`、`ReviewStateView`）。
    id("wow.api-conventions")
}

dependencies {
    // 本模块承载其契约的 domain 模块。
    wowContract(project(":blog-domain"))
    // 命令的 Jakarta 约束，在准入时校验（`wow.command.invalid`，400）。
    api(libs.jakarta.validation.api)
    // 生成的契约代码中的查询字段（`BlogContract.Article.STATE__*`）。
    api("me.ahoo.wow:wow-query-api")
}
