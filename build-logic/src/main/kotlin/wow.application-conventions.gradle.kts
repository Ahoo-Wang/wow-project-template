/**
 * Spring Boot 宿主应用（server）：不发布，打包为可运行发行版。
 */
plugins {
    id("wow.kotlin-conventions")
    application
    // 打开 Spring 注解类（@Configuration 等）以支持 CGLIB 代理。
    id("org.jetbrains.kotlin.plugin.spring")
}
