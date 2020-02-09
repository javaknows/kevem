package org.kevm.rpc

class SetChainParamsGenesisDTO(
    val author: String?,
    val difficulty: String?,
    val gasLimit: String?,
    val nonce: String?,
    val extraData: String?,
    val timestamp: String?,
    val mixHash: String?
)

class SetChainParamsRequestDTO(
    val sealEngine: String,
    val params: Map<String, String>,
    val genesis: SetChainParamsGenesisDTO,
    val accounts: Map<String, SetChainParamsAccountDTO>
)

class SetChainParamsAccountDTO(
    val balance: String?,
    val code: String?,
    val nonce: String?,
    val storage: Map<String, String>?
)