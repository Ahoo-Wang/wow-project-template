/**
 * 需要 Wow 编译期元数据（聚合/限界上下文）的模块：api、domain。
 */
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    "ksp"(platform(project(":dependencies")))
    "ksp"("me.ahoo.wow:wow-compiler")
}
