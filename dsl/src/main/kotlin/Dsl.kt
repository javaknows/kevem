package org.kevm.dsl

import org.kevm.common.KevmException
import org.kevm.evm.model.*
import org.kevm.evm.toByteList
import org.kevm.rpc.AppConfig
import org.kevm.rpc.LocalAccount
import org.kevm.rpc.LocalAccounts
import org.kevm.rpc.toBigInteger
import java.math.BigInteger
import java.time.Clock
import java.time.Instant

fun eth(e: Long): BigInteger = e.toBigInteger().multiply(BigInteger("1000000000000000000"))
fun eth(e: String): BigInteger = toBigInteger(e).multiply(BigInteger("1000000000000000000"))
fun eth(e: BigInteger): BigInteger = e.multiply(BigInteger("1000000000000000000"))

fun gwei(e: Long): BigInteger = e.toBigInteger().multiply(BigInteger("1000000"))
fun gwei(e: String): BigInteger = toBigInteger(e).multiply(BigInteger("1000000"))
fun gwei(e: BigInteger): BigInteger = e.multiply(BigInteger("1000000"))

fun wei(e: Long): BigInteger = e.toBigInteger()
fun wei(e: String): BigInteger = toBigInteger(e)
fun wei(e: BigInteger): BigInteger = e

@DslMarker
annotation class Boundary

class EvmCreationContext(
    var accounts: Accounts = Accounts(),
    var localAccounts: List<LocalAccount> = emptyList()
) {
    fun addAccount(account: Account, localAccount: LocalAccount? = null) {
        accounts = accounts.updateAccount(account.address, account)

        if (localAccount != null) {
            localAccounts = localAccounts + localAccount
        }
    }
}

class EvmCreationResult(
    val accounts: Accounts,
    val localAccounts: List<LocalAccount>,
    val appConfig: AppConfig,
    val clock: Clock
) {
    fun toWeb3j() = Web3ServiceCreator.createWeb3(appConfig, LocalAccounts(localAccounts), accounts, clock)
}

@Boundary
class AccountCreator {
    var balance: BigInteger = BigInteger.ZERO
    var address: String? = null
    var privateKey: String? = null
}

@Boundary
class EvmCreator(private val creationContext: EvmCreationContext) {

    fun account(create: AccountCreator.() -> Unit) {
        val accountCreator = AccountCreator().apply(create)

        val balance = accountCreator.balance
        val providedAddress = accountCreator.address
        val privateKey = accountCreator.privateKey

        val (account, localAccount) = when {
            privateKey != null -> {
                val address = privateKeyToAddress(privateKey)
                if (providedAddress != null && providedAddress != address) {
                    throw KevmException("Address must match private key - provided ${providedAddress} but require ${address}. Either don't set address explicitly or provide matching value.")
                }
                Pair(Account(Address(address), balance), LocalAccount(Address(address), toByteList(privateKey), false))
            }
            providedAddress != null -> {
                Pair(Account(Address(providedAddress), balance), null)
            }
            else -> throw KevmException("Cannot create account without providing address or private key")
        }

        creationContext.addAccount(account, localAccount)
    }
}

data class Config(
    val chainId: Int? = null,
    val peerCount: Int? = null,
    val coinbase: String? = null,
    val hashRate: BigInteger? = null,
    val difficulty: BigInteger? = null,
    val extraData: String? = null,
    val gasPrice: BigInteger? = null,
    val blockGasLimit: BigInteger? = null,
    val genesisBlockTimestamp: String? = null
)

fun kevm(
    config: Config = Config(),
    clock: Clock = Clock.systemUTC(),
    create: EvmCreator.() -> Unit = {}
): EvmCreationResult =
    EvmCreationContext().let {
        EvmCreator(it).apply(create)
        return EvmCreationResult(it.accounts, it.localAccounts, toAppConfig(config), clock)
    }

internal fun toAppConfig(config: Config): AppConfig {
    val defaults = AppConfig()

    return defaults.copy(
        chainId = config.chainId ?: defaults.chainId,
        peerCount = config.peerCount ?: defaults.peerCount,
        coinbase = config.coinbase ?: defaults.coinbase,
        hashRate = config.hashRate ?: defaults.hashRate,
        difficulty = config.difficulty ?: defaults.difficulty,
        extraData = if(config.extraData != null) Word.coerceFrom(config.extraData) else defaults.extraData,
        gasPrice = config.gasPrice ?: defaults.gasPrice,
        blockGasLimit = config.blockGasLimit ?: defaults.blockGasLimit,
        genesisBlockTimestamp = if(config.genesisBlockTimestamp != null) Instant.parse(config.genesisBlockTimestamp) else defaults.genesisBlockTimestamp
    )
}