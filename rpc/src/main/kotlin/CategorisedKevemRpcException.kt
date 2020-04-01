package org.kevem.rpc

import org.kevem.common.CategorisedKevemException

open class CategorisedKevemRpcException(message: String, code: Long, cause: Throwable? = null, val id: Long? = null, val jsonrpc: String? = null) : CategorisedKevemException(message, code, cause)
