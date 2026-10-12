plugins {
    id("wow.library-conventions")
    id("wow.wow-compiler-conventions")
}

dependencies {
    // 发布语言只依赖注解与契约类型，避免把基础设施传递给消费方。
    api("me.ahoo.wow:wow-api")
    api("jakarta.validation:jakarta.validation-api")
    api("com.fasterxml.jackson.core:jackson-annotations")
    api(libs.swagger.annotations)
}
