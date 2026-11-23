import kotlinx.kover.gradle.plugin.dsl.KoverReportFiltersConfig

/**
 * 覆盖率不统计的类：Wow 在构建期生成的代码，以及只承载编译期常量的拒绝码对象。
 *
 * - domain 的 KSP 输出：模型对象（`DemoModel`）、状态查询字段（`*StateProperties`）与绑定索引（`*ModelProvider`）。
 *   生成的顶层函数（`demoModel()`、`demoReviewSagaHandlers()` 所在的 `*Kt` 文件类）Kover 无法按类名排除；
 *   它们不在仓库中，Codecov 也不统计。
 * - api 的契约代码：`<Context>Contract` 与 `@StateView` 状态视图。
 * - 聚合内的 `object Rejections`：`@ErrorCode` 常量在编译期内联，对象本身从不实例化。
 *
 * 模块报告（wow.library-conventions）与聚合报告（wow.coverage-report-conventions，上报 Codecov）共用此规则。
 */
fun KoverReportFiltersConfig.excludeWowGenerated() {
    excludes {
        classes(
            "*ModelProvider",
            "*Model",
            "*StateProperties",
            "*Contract",
            "*Contract\$*",
            "*\$Rejections",
        )
        annotatedBy("me.ahoo.wow.api.read.StateView")
    }
}
