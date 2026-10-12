plugins {
    alias(libs.plugins.ksp)
}
dependencies {
    api(platform(project(":dependencies")))
    ksp(platform(project(":dependencies")))
    // 发布语言只依赖注解与契约类型，避免把基础设施传递给消费方。
    api("me.ahoo.wow:wow-api")
    api("jakarta.validation:jakarta.validation-api")
    api("com.fasterxml.jackson.core:jackson-annotations")
    api(libs.swagger.annotations)
    ksp("me.ahoo.wow:wow-compiler")
}
