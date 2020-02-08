package org.kevm.web.module

import org.kevm.rpc.SetChainParamsRequestDTO

class TestSetChainParamsRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<SetChainParamsRequestDTO>>(jsonrpc, method, id, emptyList())

class TestSetChainParamsResponse(request: TestSetChainParamsRequest, result: Boolean) : RpcResponse<Boolean>(request, result)

private val TestSetChainParams =
    Method.create("test_setChainParams", TestSetChainParamsRequest::class, TestSetChainParamsResponse::class) { request, context ->
        val chainParams = request.params[0]
        val isSuccess = context.testRpc.setChainParams(chainParams)
        TestSetChainParamsResponse(request, isSuccess)
    }

class TestMineBlocksRequest(jsonrpc: String, method: String, id: Long) :
    RpcRequest<List<Int>>(jsonrpc, method, id, emptyList())

class TestMineBlocksResponse(request: TestMineBlocksRequest, result: Boolean) : RpcResponse<Boolean>(request, result)

private val TestMineBlocks =
    Method.create("test_mineBlocks", TestMineBlocksRequest::class, TestMineBlocksResponse::class) { request, context ->
        val isSuccess = context.testRpc.mineBlocks()
        TestMineBlocksResponse(request, isSuccess)
    }

@Suppress("UNCHECKED_CAST")
private val methods: List<Method<RpcRequest<*>, RpcResponse<*>>> = listOf(
    TestSetChainParams,
    TestMineBlocks
) as List<Method<RpcRequest<*>, RpcResponse<*>>>

val TestModule = Module(methods)