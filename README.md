# Wow 项目模版

用于快速构建基于 [Wow 框架](https://github.com/Ahoo-Wang/Wow) 的项目模板。

## 模块介绍

| 模块                        | 说明                                                                                         |
|---------------------------|--------------------------------------------------------------------------------------------|
| api                       | API 层，定义聚合命令（Command）、领域事件（Domain Event）以及查询视图模型（Query View Model），这个模块充当了各个模块之间通信的“发布语言”。 |
| domain                    | 领域层，包含聚合根和业务约束的实现。聚合根：领域模型的入口点，负责协调领域对象的操作。业务约束：包括验证规则、领域事件的处理等。                           |
| server                    | 宿主服务，应用程序的启动点。负责整合其他模块，并提供应用程序的入口。涉及配置依赖项、连接数据库、启动 API 服务                                  |
| code-coverage-report      | 测试覆盖率，这个模块用于生成测试覆盖率报告，帮助开发团队了解项目测试的全面性和质量。                                                 |
| dependencies              | 依赖项管理，这个模块负责管理项目的依赖关系，确保各个模块能够正确地引用和使用所需的外部库和工具。                                           |
| bom                       | 项目的 BOM（Bill of Materials）                                                                 |
| gradle/libs.versions.toml | 依赖版本配置文件                                                                                   |

## IDEA 项目模板

[IDEA 项目模板](https://www.jetbrains.com/help/idea/saving-project-as-template.html)

[IDEA 配置目录](https://www.jetbrains.com/help/idea/directories-used-by-the-ide-to-store-settings-caches-plugins-and-logs.html#config-directory)

1. 项目模板目录：`<IDE config home>/projectTemplates`
2. 将模板压缩包放到项目模板
3. [使用模板创建项目](https://www.jetbrains.com/help/idea/saving-project-as-template.html#create-project-from-template)
4. 修改 `settings.gradle.kts` 文件，将 `rootProject.name` 修改为项目名称
5. 修改 `api/{package}/DemoService`
6. 修改 `domain/{package}/DemoBoundedContext`

## 生成 `server` 部署包

```shell
gradle server:installDist
```

## 验证测试覆盖率

```shell
gradle domain:jacocoTestCoverageVerification
```

> 查看测试覆盖率报告：`domain/build/reports/jacoco/test/html/index.html`
