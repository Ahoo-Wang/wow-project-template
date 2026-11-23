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
