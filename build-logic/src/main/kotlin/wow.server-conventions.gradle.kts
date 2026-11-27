/**
 * 宿主服务（`<svc>-server`）：Spring Boot 应用，打包为可运行发行版，不发布。
 *
 * Wow 的 service 插件合并类路径上的 Manifest（build/wow/manifest.json）、生成 OpenAPI 与 Helm values
 * （build/wow/helm/values.yaml），在 `check` 中以提交的 manifest/baseline.json 校验契约兼容性
 * （`updateWowManifestBaseline` 记录基线），并注册在真实存储上运行的 `integrationTest` 测试套件（不属于 `check`）。
 */
plugins {
    id("wow.kotlin-conventions")
    application
    // 打开 Spring 注解类（@Configuration 等）以支持 CGLIB 代理。
    id("org.jetbrains.kotlin.plugin.spring")
    id("me.ahoo.wow.service")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    // `wowServer<App>()`：以随机端口启动应用，用类型化客户端按调用方发送命令、读取状态。
    "testImplementation"("me.ahoo.wow:wow-test-spring-boot")
    // wow-bom 只约束 Wow 自己的模块，Testcontainers 取自己的 BOM。
    "integrationTestImplementation"(platform(libs.findLibrary("testcontainers-bom").get()))
}

tasks.named<JavaExec>("run") {
    // `./gradlew :<svc>-server:run` 是本机运行：总带上 dev profile（开发身份头、调试日志），可再用 --args 叠加其他 profile。
    systemProperty("spring.profiles.include", "dev")
}
