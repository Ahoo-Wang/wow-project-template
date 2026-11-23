/**
 * Spring Boot 宿主应用（server）：不发布，打包为可运行发行版。
 *
 * Wow 的 service 插件合并类路径上的 Manifest（build/wow/manifest.json）、生成 OpenAPI 文档，
 * 并在 `check` 中以提交的 manifest/baseline.json 校验契约兼容性（`updateWowManifestBaseline` 记录基线）。
 */
plugins {
    id("wow.kotlin-conventions")
    application
    // 打开 Spring 注解类（@Configuration 等）以支持 CGLIB 代理。
    id("org.jetbrains.kotlin.plugin.spring")
    id("me.ahoo.wow.service")
}
