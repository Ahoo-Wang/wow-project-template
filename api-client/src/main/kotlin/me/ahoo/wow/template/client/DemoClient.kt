package me.ahoo.wow.template.client

import me.ahoo.coapi.api.CoApi
import me.ahoo.wow.apiclient.query.ReactiveSnapshotQueryApi
import me.ahoo.wow.template.DemoService
import me.ahoo.wow.template.api.demo.DemoView
import org.springframework.web.service.annotation.HttpExchange

/**
 * 供其他服务调用的类型安全客户端：按需引入 `api-client`，契约方（`api`）不感知调用方基础设施。
 *
 * 使用方式：在调用方启用 `@EnableCoApi(clients = [DemoClient::class])`。
 */
@CoApi(serviceId = DemoService.SERVICE_NAME)
@HttpExchange(DemoService.DEMO_AGGREGATE_NAME)
interface DemoClient : ReactiveSnapshotQueryApi<DemoView>
