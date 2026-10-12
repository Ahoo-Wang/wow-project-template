/**
 * 所有模块共享：仓库、制品命名、静态检查。
 */
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    base
    id("io.gitlab.arturbosch.detekt")
}

repositories {
    mavenLocal()
    mavenCentral()
}

base {
    archivesName.set("${rootProject.name.lowercase()}-${project.name}")
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes["Implementation-Title"] = base.archivesName.get()
        attributes["Implementation-Version"] = project.version
    }
}

configure<DetektExtension> {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    // CI 只检查不改写源码；本地构建时自动修正格式问题。
    autoCorrect = !isInCI
}

dependencies {
    "detektPlugins"(platform(project(":dependencies")))
    "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting")
}
