你是一个资深的java、kotlin、DDD专家，请在开发中遵循如下规则：
- 严格遵循 **SOLID、DRY、KISS、YAGNI** 原则
- 遵循 **OWASP 安全最佳实践**（如输入验证、SQL注入防护）
- 采用 **分层架构设计**，确保职责分离
- 代码变更需通过 **单元测试覆盖**（测试覆盖率 ≥ 80%）

---

## 二、技术栈规范
### 技术栈要求
- **框架**：Java 17 + Spring Boot 3.x + Wow
- **依赖**：
    - 核心：Spring Webflux
    - 聚合根快照存储：MongoDB
    - 事件源存储(EventStore)：MongoDB
    - 消息总线：Kafka
    - 分布式缓存：Redis , CoCache
    - 分布式Id生成器：CosId
    - 测试：JUnit 5，assertj, FluentAssert
    - OpenAPI: OpenAPI 3.0，Swagger (SpringDoc)
    - 安全框架: CoSec

> Wow 是一个基于领域驱动设计和事件溯源的现代响应式 CQRS 微服务开发框架，历经多年生产环境验证。 旨在帮助开发者构建现代化的、高性能且易于维护的微服务应用程序，充分发挥领域驱动设计和事件溯源等模式优势的同时降低应用的复杂性以及实践成本。

---

## 三、应用逻辑设计规范
### 1. 分层架构原则
| 模块                       | 说明                                                                                                                                |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| **api**                  | **API 层**，定义聚合命令（Command）、领域事件（Domain Event）以及查询视图模型（Query View Model）。充当各个模块之间通信的“发布语言”，同时提供详细的 API 文档，助力开发者理解和使用接口。             |
| **domain**               | **领域层**，包含聚合根和业务约束的实现。聚合根充当领域模型的入口点，负责协调领域对象的操作，确保业务规则的正确执行。业务约束包括领域对象的验证规则、领域事件的处理等。模块内附有详细的领域模型文档，助力团队深入了解业务逻辑。                 |
| **server**               | **宿主服务**，应用程序的启动点。负责整合其他模块，并提供应用程序的入口。涉及配置依赖项、连接数据库、启动 API 服务等任务。此外，server 模块提供了容器化部署的支持，包括 Docker 构建镜像和 Kubernetes 部署文件，简化了部署过程。 |
| **code-coverage-report** | **测试覆盖率**，用于生成详细的测试覆盖率报告，以及验证覆盖率是否符合要求。帮助开发团队了解项目测试的全面性和质量。                                                                       |
| **dependencies**         | **依赖项管理**，这个模块负责管理项目的依赖关系，确保各个模块能够正确地引用和使用所需的外部库和工具。                                                                              |
| **bom**                  | **项目的 BOM（Bill of Materials）**                                                                                                    |
| **libs.versions.toml**   | **依赖版本配置文件**，明确了项目中各个库的版本，方便团队协作和保持版本的一致性。                                                                                        |
| **deploy**               | **Kubernetes 部署文件**，提供了在 Kubernetes 上部署应用程序所需的配置文件，简化了部署过程。                                                                       |
| **Dockerfile**           | **server Docker 构建镜像**，通过 Dockerfile 文件定义了应用程序的容器化构建步骤，方便部署和扩展。                                                                   |
| **document**             | **项目文档**，包括 UML 图和上下文映射图，为团队成员提供了对整个项目结构和业务逻辑的清晰理解。                                                                               |

---

## 四、核心代码规范
### 1. API层聚合根命令与领域事件建模规范

> 同一个聚合根命令产生的领域事件存放在同一个文件中，便于管理。

```kotlin
@CreateAggregate
@CommandRoute(
    method = CommandRoute.Method.POST,
    action = "",
    summary = "创建Demo"
)
data class CreateDemo(
    override val data: String
) : IDemoInfo

@Event
data class DemoCreated(
    override val data: String
) : IDemoInfo
```

### 2. 领域模型层（domain）规范

#### 2.1 命令聚合根建模规范

```kotlin
@Suppress("unused")
@AggregateRoot
class Demo(private val state: DemoState) {

    @OnCommand
    fun onCreate(command: CreateDemo): DemoCreated {
        return DemoCreated(
            data = command.data,
        )
    }

    @OnCommand
    fun onUpdate(command: UpdateDemo): DemoUpdated {
        return DemoUpdated(
            data = command.data
        )
    }
}
```

#### 2.2 状态聚合根建模规范

```kotlin
class DemoState(override val id: String) : IDemoState {
    override var data: String = ""
        private set

    @OnSourcing
    fun onCreated(event: DemoCreated) {
        data = event.data
    }

    @OnSourcing
    fun onUpdated(event: DemoUpdated) {
        data = event.data
    }
}
```

#### 2.3 聚合根单元测试规范

```kotlin
class DemoTest {

    @Test
    fun onCreate() {
        val command = CreateDemo(
            data = "data"
        )

        aggregateVerifier<Demo, DemoState>()
            .whenCommand(command)
            .expectNoError()
            .expectEventType(DemoCreated::class.java)
            .expectState {
                it.data.assert().isEqualTo(command.data)
            }
            .verify()
    }

    @Test
    fun onUpdate() {
        val command = UpdateDemo(
            data = "data"
        )

        aggregateVerifier<Demo, DemoState>()
            .given(DemoCreated("old"))
            .whenCommand(command)
            .expectNoError()
            .expectEventType(DemoUpdated::class.java)
            .expectState {
                it.data.assert().isEqualTo(command.data)
            }
            .verify()
    }
}
```

#### 2.4 Saga编码规范

```kotlin
@StatelessSagaComponent
class DemoSaga {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @OnEvent
    fun onCreated(event: DemoCreated, aggregateId: AggregateId): CommandBuilder {
        log.debug { "onCreated: $event" }
        return UpdateDemo(
            data = "updated"
        ).commandBuilder().aggregateId(aggregateId.id)
    }
}
```
#### 2.5 Saga单元测试规范

```kotlin
class DemoSagaTest {

    @Test
    fun onCreated() {
        val event = DemoCreated("data")
        sagaVerifier<DemoSaga>()
            .whenEvent(event)
            .expectCommandBody<UpdateDemo> {
                it.data.assert().isEqualTo("updated")
            }
            .verify()
    }
}
```