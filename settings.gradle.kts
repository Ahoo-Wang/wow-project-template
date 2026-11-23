pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "wow-project-template"

// 一个服务按角色分三个模块（Wow 的布局），目录名固定为 api、domain、server。
// 项目名以服务的短名为前缀：Manifest 片段（META-INF/wow/manifest/blog-domain.json）、绑定索引（BlogDomainModelProvider）
// 与发布的制品（blog-api）都取自项目名，与其他服务同在一条类路径上也不会冲突。改名时改这里的 `service`，以及各构建脚本中的项目引用（`project(":blog-domain")`）。
val service = "blog"
for (role in listOf("api", "domain", "server")) {
    include(":$service-$role")
    project(":$service-$role").projectDir = file(role)
}
