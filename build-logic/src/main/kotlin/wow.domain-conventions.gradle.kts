/**
 * 领域模型（`<svc>-domain`）：聚合、状态、错误码与处理器。不依赖 Spring 与任何存储，也不发布：其他服务只依赖 api。
 *
 * Wow 插件以 wow-compiler 运行 KSP，生成模型工厂、处理器绑定、绑定索引与 Manifest 片段，并引入 wow-kernel。
 */
plugins {
    id("wow.kotlin-conventions")
    id("me.ahoo.wow")
}

dependencies {
    // aggregateSpec / sagaSpec：在内存中对生成的模型运行领域测试。
    "testImplementation"("me.ahoo.wow:wow-test")
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
