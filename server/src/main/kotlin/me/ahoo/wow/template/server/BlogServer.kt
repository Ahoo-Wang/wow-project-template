package me.ahoo.wow.template.server

import me.ahoo.wow.template.domain.review.ReviewPolicy
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.Duration
import kotlin.time.toKotlinDuration

/**
 * 博客服务的 Wow 宿主。宿主从 blog-domain 的绑定索引（KSP 生成的 `BlogDomainModelProvider`）找到聚合模型与 Saga，
 * 在公开端口提供由 Manifest 派生的路由，在内部端口提供集群通道：没有组件扫描，也没有控制器。
 *
 * 应用只提供领域需要的服务（[ReviewPolicy]）与授权规则（[BlogAuthorization]）。
 */
@SpringBootApplication(proxyBeanMethods = false)
class BlogServer {
    /** 审核的时限：`blog.review.timeout`，默认 7 天。 */
    @Bean
    fun reviewPolicy(@Value("\${blog.review.timeout:7d}") timeout: Duration): ReviewPolicy =
        ReviewPolicy(timeout.toKotlinDuration())
}

fun main(args: Array<String>) {
    runApplication<BlogServer>(*args)
}
