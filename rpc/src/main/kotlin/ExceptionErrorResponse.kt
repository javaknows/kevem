package org.kevem.rpc

import org.kevem.common.CategorisedKevemException
import org.kevem.rpc.module.RpcResponse

class ExceptionErrorResponse {

    fun errorResponse(e: Throwable?, jsonrpc: String?, id: Long?): RpcResponse<*>? {
        val code = if (e is CategorisedKevemException) e.code else -1

        val exceptionMessage = e?.message

        val message =
            if (exceptionMessage != null)
                if (e is CategorisedKevemException) exceptionMessage
                else "unexpected error: $exceptionMessage"
            else "unknown error"

        return RpcResponse(jsonrpc, id, ErrorDTO(code, message))
    }

}