package org.kevm.web3

import org.kevm.rpc.CategorisedRpcException
import org.kevm.rpc.RpcException
import io.reactivex.Flowable
import org.web3j.protocol.Web3jService
import org.web3j.protocol.core.Request
import org.web3j.protocol.core.Response
import org.web3j.protocol.websocket.events.Notification
import java.util.concurrent.CompletableFuture

class KevmWeb3Service(private val providers: List<RPCProvider>) : Web3jService {

    override fun <T : Response<*>> send(request: Request<*, out Response<*>>, responseType: Class<T>): T {
        val result = try {
            execute(request, responseType)
        } catch (e: Exception) {
            errorResult(responseType, e)
        }

        return result.apply {
            id = request.id
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Response<*>> errorResult(responseType: Class<T>, exception: Exception): T {
        val constructor = responseType.constructors
            .find { it.parameters.isEmpty() }
            ?: throw RpcException("invalid response type $responseType - no no-arg constructor")

        val instance = constructor.newInstance() as T

        return instance.apply {
            val errorCode =
                if (exception is CategorisedRpcException) exception.code
                else 0

            error = Response.Error(errorCode, exception.message)
        }
    }

    private fun <T : Response<*>> execute(request: Request<*, out Response<*>>, responseType: Class<T>): T {
        val result = providers.mapNotNull { it.execute(request, responseType) }.firstOrNull()

        if (result == null)
            throw CategorisedRpcException(1000, "unknown web3j request method ${request.method}")
        else
            return result
    }

    override fun <T : Response<*>> sendAsync(
        request: Request<*, out Response<*>>,
        responseType: Class<T>
    ): CompletableFuture<T> = CompletableFuture.supplyAsync { send(request, responseType) }

    override fun <T : Notification<*>> subscribe(
        request: Request<*, out Response<*>>,
        unsubscribeMethod: String,
        responseType: Class<T>
    ): Flowable<T> {
        TODO("not implemented")
    }

    override fun close() {}
}