plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.allopen)
    implementation(libs.ksp.gradle.plugin)
    // 把 Wow 的插件（me.ahoo.wow、me.ahoo.wow.api、me.ahoo.wow.service）放到约定插件的类路径上，
    // 由约定插件按 id 应用，版本取版本目录中的 `wow`。
    implementation(libs.wow.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
    implementation(libs.test.retry.gradle.plugin)
    constraints {
        // 插件的传递依赖：dokka-gradle-plugin（dokka-core）带入 jackson-core/databind 2.22.2，
        // kover-gradle-plugin（intellij-coverage-reporter）带入 freemarker 2.3.32，都有已修复的漏洞。约束随 build-logic 的
        // runtimeElements 传到使用约定插件的构建，抬高根构建的插件类路径；版本见版本目录的“构建工具传递依赖的安全下限”。
        implementation(libs.jackson2.core)
        implementation(libs.jackson2.databind)
        implementation(libs.freemarker)
    }
}
