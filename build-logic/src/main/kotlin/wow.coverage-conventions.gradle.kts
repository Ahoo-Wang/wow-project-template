/**
 * 根项目：聚合各模块的 Kover 覆盖率报告（上报 Codecov），与模块报告共用 [excludeWowGenerated] 过滤规则。
 */
plugins {
    id("org.jetbrains.kotlinx.kover")
}

repositories {
    mavenCentral()
}

kover {
    reports {
        filters {
            excludeWowGenerated()
        }
    }
}
