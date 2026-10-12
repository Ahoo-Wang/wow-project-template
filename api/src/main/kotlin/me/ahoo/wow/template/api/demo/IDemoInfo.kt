package me.ahoo.wow.template.api.demo

import jakarta.validation.constraints.NotBlank

/**
 * Demo 的可编辑信息，作为命令输入与状态的共享形状。
 *
 * 注意：领域事件不实现该接口。事件一经持久化便不可变，其结构必须独立演进。
 */
interface IDemoInfo {
    @get:NotBlank
    val data: String
}
