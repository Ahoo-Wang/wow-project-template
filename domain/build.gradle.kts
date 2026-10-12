plugins {
    id("wow.library-conventions")
    id("wow.wow-compiler-conventions")
}

dependencies {
    api(project(":api"))
    // 领域层只依赖 Wow 核心模型，不依赖 Spring 宿主；Saga/Projection 由 server 端的 wow-spring 自动注册。
    api("me.ahoo.wow:wow-core")
    testImplementation("me.ahoo.wow:wow-test")
}

kover {
    reports {
        verify {
            rule {
                minBound(80)
            }
        }
    }
}
