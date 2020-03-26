package org.kevem.rpc.module

import org.kevem.rpc.StandardRPC
import org.kevem.rpc.TestRPC
import kotlin.reflect.KClass

class EvmContext(
    val standardRpc: StandardRPC,
    val testRpc: TestRPC
)

open class RpcRequest<T>(
    val jsonrpc: String,
    val method: String,
    val id: Long,
    val params: T
)

open class RpcResponse<T>(
    val jsonrpc: String,
    val id: Long,
    val result: T
) {
    constructor(request: RpcRequest<*>, result: T) : this(request.jsonrpc, request.id, result)
}

interface RpcModule {
    fun supported(): Map<String, KClass<RpcRequest<*>>>

    fun process(request: RpcRequest<*>, context: EvmContext): RpcResponse<*>?
}

class Module(private val methods: List<Method< RpcRequest<*>,  RpcResponse<*>>>) : RpcModule {
    override fun supported(): Map<String, KClass<RpcRequest<*>>> =
        methods.map { Pair(it.method, it.requestClass) }.toMap()

    override fun process(request: RpcRequest<*>, context: EvmContext): RpcResponse<*>? {
        return methods.find { request::class == it.requestClass }?.process(request, context)
    }
}

abstract class Method<T : RpcRequest<*>, U : RpcResponse<*>>(
    val method: String,
    val requestClass: KClass<T>,
    val responseClass: KClass<U>
) {
    abstract fun process(request: T, context: EvmContext): U

    companion object {
        fun <T : RpcRequest<*>, U : RpcResponse<*>> create(
            method: String,
            requestClass: KClass<T>,
            responseClass: KClass<U>,
            op: (r: T, context: EvmContext) -> U
        ) = object : Method<T, U>(method, requestClass, responseClass) {
            override fun process(request: T, context: EvmContext): U = op(request, context)
        }
    }
}
