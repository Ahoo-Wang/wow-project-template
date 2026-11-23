import kotlinx.kover.gradle.plugin.dsl.KoverReportFiltersConfig

/**
 * 覆盖率不统计的类：Wow 在构建期生成的代码，以及只承载编译期常量的拒绝码对象。
 *
 * - domain 的 KSP 输出：模型工厂（`articleModel()` 所在的 `ArticleModelKt`、`ArticleModel`）、
 *   处理器绑定（`articleReviewSagaHandlers()` 所在的 `*SubscriptionsKt` 及其 lambda 类）、
 *   状态查询字段（`*StateProperties`）与绑定索引（`*ModelProvider`）。
 * - api 的契约代码：`<Context>Contract` 与 `@StateView` 状态视图。
 * - 聚合内的 `object Rejections`：`@ErrorCode` 常量在编译期内联，对象本身从不实例化。
 * - `@BoundedContext` 标记对象：只为编译器声明上下文，没有代码。
 * - Spring Boot 应用的 `main`（`<Name>Server.kt` 的文件类 `<Name>ServerKt`）：由 CI 的发行版冒烟测试启动，而不是单元测试。
 *
 * 模块报告（wow.library-conventions）与聚合报告（wow.coverage-report-conventions，上报 Codecov）共用此规则。
 */
fun KoverReportFiltersConfig.excludeWowGenerated() {
    excludes {
        classes(
            "*ModelProvider",
            "*Model",
            "*ModelKt*",
            "*SubscriptionsKt*",
            "*StateProperties",
            "*Contract",
            "*Contract\$*",
            "*\$Rejections",
            "*ServerKt",
        )
        annotatedBy("me.ahoo.wow.api.read.StateView", "me.ahoo.wow.api.annotation.BoundedContext")
    }
}
