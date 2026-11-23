plugins {
    // me.ahoo.wow：以 wow-compiler 对聚合与处理器运行 KSP，并引入 wow-kernel。不依赖 Spring，也不依赖存储。
    id("wow.domain-conventions")
}

wow {
    // 生成的 `BlogContract` 所在的包，其他服务从这里导入：设定一次，新增聚合也不变。
    contractPackage = "me.ahoo.wow.template.api"
    // 承载本上下文的服务名：必须等于 server 的 `spring.application.name`。
    service = "blog-service"
}

dependencies {
    api(project(":blog-api"))
    // 文章状态上的 @QueryIndex：列出文章的查询走索引。
    implementation("me.ahoo.wow:wow-query-api")
}
