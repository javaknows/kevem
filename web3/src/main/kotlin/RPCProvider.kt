package com.gammadex.kevin.web3

import org.web3j.protocol.core.Request
import org.web3j.protocol.core.Response

interface RPCProvider {

    fun <T : Response<*>> execute(request: Request<*, out Response<*>>, responseType: Class<T>): T?

}