/**
 * 上下文的发布语言（`<svc>-api`）：命令、事件与值类型，其他服务只依赖它。
 *
 * Wow 的 api 插件装配所声明 domain 模块的契约（META-INF/wow/contract.json），由契约生成 `<Context>Contract` 与
 * `<State>View`，作为模块的 `contract` 特性随 jar 一起发布。这是本项目唯一发布的制品。
 */
plugins {
    id("wow.kotlin-conventions")
    `java-library`
    id("me.ahoo.wow.api")
    id("org.jetbrains.dokka")
    id("wow.publish-conventions")
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    constraints {
        // Dokka 生成器（dokkaGenerate，在 Dokka Worker 中运行）的类路径：dokka-core/dokka-base 2.3.0 带入 jackson-core/databind 2.22.2，
        // 抬到修复版本（Jackson 2 的 BOM 让其余 Jackson 2 模块跟着对齐）。只是构建工具，不进发布的 api 制品。
        "dokkaHtmlGeneratorRuntime"(libs.findLibrary("jackson2-core").get())
        "dokkaHtmlGeneratorRuntime"(libs.findLibrary("jackson2-databind").get())
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenLibrary") {
            from(components["java"])
        }
    }
}
