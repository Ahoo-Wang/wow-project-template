package me.ahoo.wow.template.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Demo 服务的 Wow 宿主：宿主通过 domain 模块的绑定索引（KSP 生成的 `ModelProvider`）找到聚合模型与 Saga，
 * 在公开端口提供由 Manifest 派生的路由，在内部端口提供集群通道。无需组件扫描或控制器。
 */
@SpringBootApplication(proxyBeanMethods = false)
class Server

fun main(args: Array<String>) {
    runApplication<Server>(*args)
}
