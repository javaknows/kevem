package org.kevm.web3

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Flowable
import org.kevm.web.KevmWebRpcService
import org.kevm.web.jackson.RequestObjectMapper
import org.kevm.web.module.*
import org.web3j.protocol.ObjectMapperFactory
import org.web3j.protocol.Web3jService
import org.web3j.protocol.core.Request
import org.web3j.protocol.core.Response
import org.web3j.protocol.websocket.events.Notification
import java.util.concurrent.CompletableFuture

class AdapterKevmWeb3jService(
    val service: KevmWebRpcService,
    val web3jObjectMapper: ObjectMapper = ObjectMapperFactory.getObjectMapper(false),
    val kevmObjectMapper: ObjectMapper = RequestObjectMapper().create(
        WebModule.supported() + NetModule.supported() + EthModule.supported()
    )
) : Web3jService {

    override fun <T : Response<*>?> send(request: Request<*, out Response<*>>, responseType: Class<T>): T {
        val kevmRequest = web3RequestToKevm(request)
        val kevmResponse = service.processRequest(kevmRequest)
        return kevmResponseToWeb3j(kevmResponse, responseType)
    }

    private fun <T : Response<*>?> kevmResponseToWeb3j(response: RpcResponse<*>?, responseType: Class<T>): T {
        val responseJson = kevmObjectMapper.writeValueAsString(response)
        return web3jObjectMapper.readValue(responseJson, responseType)
    }

    private fun web3RequestToKevm(request: Request<*, out Response<*>>): RpcRequest<*> {
        val requestJson = web3jObjectMapper.writeValueAsString(request)
        return kevmObjectMapper.readValue(requestJson, RpcRequest::class.java)
    }

    override fun <T : Response<*>> sendAsync(
        request: Request<*, out Response<*>>,
        responseType: Class<T>
    ): CompletableFuture<T> = CompletableFuture.supplyAsync { send(request, responseType) }

    override fun <T : Notification<*>?> subscribe(
        request: Request<*, out Response<*>>?,
        unsubscribeMethod: String?,
        responseType: Class<T>?
    ): Flowable<T> {
        TODO("not implemented")
    }

    override fun close() {
    }
}