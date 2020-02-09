package org.kevm.web.module

import org.kevm.rpc.SetChainParamsRequestDTO
import org.kevm.rpc.toBigInteger

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

class TestRewindToBlockRequest(jsonrpc: String, method: String, id: Long, params: List<String>) :
    RpcRequest<List<String>>(jsonrpc, method, id, params)

class TestRewindToBlockResponse(request: TestRewindToBlockRequest, result: Boolean) : RpcResponse<Boolean>(request, result)

private val TestRewindToBlock =
    Method.create("test_rewindToBlock", TestRewindToBlockRequest::class, TestRewindToBlockResponse::class) { request, context ->
        val blockNumber = request.params[0]
        val isSuccess = context.testRpc.revertToBlock(toBigInteger(blockNumber))
        TestRewindToBlockResponse(request, isSuccess)
    }

class TestModifyTimestampRequest(jsonrpc: String, method: String, id: Long, params: List<Long>) :
    RpcRequest<List<Long>>(jsonrpc, method, id, params)

class TestModifyTimestampResponse(request: TestModifyTimestampRequest, result: Boolean) : RpcResponse<Boolean>(request, result)

private val TestModifyTimestamp =
    Method.create("test_modifyTimestamp", TestModifyTimestampRequest::class, TestModifyTimestampResponse::class) { request, context ->
        val timestamp = request.params[0]
        val isSuccess = context.testRpc.modifyTimestamp(timestamp)
        TestModifyTimestampResponse(request, isSuccess)
    }

@Suppress("UNCHECKED_CAST")
private val methods: List<Method<RpcRequest<*>, RpcResponse<*>>> = listOf(
    TestSetChainParams,
    TestMineBlocks,
    TestRewindToBlock,
    TestModifyTimestamp
) as List<Method<RpcRequest<*>, RpcResponse<*>>>

val TestModule = Module(methods)