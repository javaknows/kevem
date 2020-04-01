package org.kevem.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl
import org.kevem.rpc.CategorisedKevemRpcException
import org.kevem.rpc.ExceptionErrorResponse
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class ErrorProvider(
    private val exceptionErrorResponse: ExceptionErrorResponse = ExceptionErrorResponse(),
    private val simpleMapper: ObjectMapper = jacksonObjectMapper()
) : ExceptionMapper<Throwable> {

    override fun toResponse(exception: Throwable?): Response {
        val (jsonrpc, id) =
            if (exception is CategorisedKevemRpcException) Pair(exception.jsonrpc, exception.id)
            else Pair(null, null)

        val errorResponse = exceptionErrorResponse.errorResponse(exception, jsonrpc, id)
        val body = simpleMapper.writeValueAsString(errorResponse)

        return ResponseBuilderImpl().entity(body).build()
    }
}