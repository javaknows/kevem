package org.kevm.ethereumtests

import com.fasterxml.jackson.core.type.TypeReference
import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.kevm.evm.Executor
import org.kevm.evm.TransactionProcessor
import org.kevm.evm.gas.*
import org.kevm.evm.model.*
import org.kevm.evm.toByteList
import org.kevm.evm.toStringHexPrefix
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.math.BigInteger
import java.time.Instant

class GeneralStateTestCaseRunnerTest {

    private val executor = createExecutor()

    @DisplayName("ethereum-test GeneralStateTest pack")
    @ParameterizedTest(name = "{0}")
    @MethodSource(value = ["testCases"])
    internal fun `ethereum-test GeneralStateTest pack`(testCase: GeneralStateTestExplodedCase): Unit = with(testCase) {
        println(testCase.name)

        val nextBlock = Block(
            number = toBigInteger(env.currentNumber),
            difficulty = toBigInteger(env.currentDifficulty),
            gasLimit = toBigInteger(env.currentGasLimit),
            timestamp = Instant.ofEpochSecond(toBigInteger(env.currentTimestamp).toLong())
        )

        val features = Features(emptyList())

        val evmConfig = EvmConfig(coinbase = Address(env.currentCoinbase))

        val tp = TransactionProcessor(
            executor = createExecutor(),
            features = features,
            config = evmConfig
        )

        val worldState = WorldState(blocks = emptyList(), accounts = parseAccounts(pre))

        val keyPair = ECKeyPair.create(toByteList(transaction.secretKey).map { it.javaByte() }.toByteArray())

        val from = Address("0x" + Keys.getAddress(keyPair.publicKey.toStringHexPrefix()))

        val to =
            if (transaction.to.isBlank()) null
            else Address(transaction.to)

        val txMessage = TransactionMessage(
            from = from,
            to = to,
            value = toBigInteger0xTo0(transaction.value),
            gasPrice = toBigInteger(transaction.gasPrice),
            gasLimit = toBigInteger(transaction.gasLimit),
            data = toByteList(transaction.data),
            nonce = toBigInteger(transaction.nonce),
            hash = emptyList() // TODO - this doesn't really fit here - maybe move it
        )

        val result = tp.process(worldState, txMessage, nextBlock)

        //println("Woooooooooot")

        //println(result)

        //tp.

        /*
        val executionContext = createExecutionContext(testCase)

        val executed = try {
            executor.executeAll(executionContext)
        } catch (e: Exception) {
            fail("$testCase failed with ${e.message}", e)
        }

        if (post != null) {
            assertPostAccountsMatch(parseAccounts(post), executed)
        }
         */

    }

    private fun toBigInteger0xTo0(num: String): BigInteger =  toBigInteger(num.replace("^0x$".toRegex(), "0"))


    companion object {
        private const val testsRoot = "ethereum-tests-pack/GeneralStateTests"

        private val loader = TestCaseLoader(
            TestCaseParser(object :
                TypeReference<Map<String, GeneralStateTestCase>>() {}, testsRoot), testsRoot
        )

        private val fillerLoader = GeneralStateTestsFillerLoader("ethereum-tests-pack/GeneralStateTestsFiller")

        @JvmStatic
        fun testCases(): List<GeneralStateTestExplodedCase> {
            val testCases: List<GeneralStateTestCase> = loader.loadTestCases()
            val tcWithFiller: List<Pair<GeneralStateTestCase, GeneralStateTestsFiller>> = loadFiller(testCases)
            val withFork = explodeWithFork(tcWithFiller)

            val flatMap: List<GeneralStateTestExplodedCase> = withFork.flatMap {
                val (tc, f) = it

                with(tc.transaction) {
                    (data.indices zip data).flatMap { d ->
                        (gasLimit.indices zip gasLimit).flatMap { g ->
                            (value.indices zip value).map { v ->
                                val explodedTransaction = GeneralStateTestExplodedCaseTransaction(
                                    data = d.second,
                                    gasLimit = g.second,
                                    gasPrice = gasPrice,
                                    nonce = nonce,
                                    secretKey = secretKey,
                                    to = to,
                                    value = v.second
                                )

                                val eee: List<GeneralStateTestsFillerExpect> = f.expect
                                    .filter { e -> e.indexes.data.contains(d.first) || e.indexes.data.contains(-1) }
                                    .filter { e ->  e.indexes.gas.contains(g.first) || e.indexes.gas.contains(-1) }
                                    .filter { e -> e.indexes.value.contains(v.first) || e.indexes.value.contains(-1) }

                                val eOnlyForFork: List<GeneralStateTestsFillerExpect> = eee
                                    .flatMap { e: GeneralStateTestsFillerExpect ->
                                        e.network.map { network: String -> Pair(network, e) }
                                    }
                                    .filter { it.first.contains(tc.post.keys.first()) }
                                    .map { it.second }

                                val results: Map<String, GeneralStateTestsFillerResult> = eOnlyForFork.flatMap { e ->
                                    e.result.entries.map { entry ->
                                        val (address, r) = entry
                                        Pair(
                                            address, GeneralStateTestsFillerResult(
                                                balance = r.balance,
                                                nonce = r.nonce,
                                                code = r.code,
                                                storage = r.storage,
                                                shouldnotexist = r.shouldnotexist
                                            )
                                        )
                                    }
                                }.toMap()

                                val hardFork = tc.post.keys.first()

                                val name =
                                    "${tc.name} - $hardFork - data=${d.second}, gas=${g.second}, value=${v.second}"

                                GeneralStateTestExplodedCase(
                                    name = name,
                                    env = tc.env,
                                    post = tc.post,
                                    pre = tc.pre,
                                    transaction = explodedTransaction,
                                    results = results,
                                    hardFork = hardFork
                                )
                            }
                        }
                    }
                }

            }

            println("Loaded ${flatMap.size} test cases" )

            return flatMap
        }

        private fun explodeWithFork(tcWithFiller: List<Pair<GeneralStateTestCase, GeneralStateTestsFiller>>): List<Pair<GeneralStateTestCase, GeneralStateTestsFiller>> {
            return tcWithFiller.flatMap { pair ->
                val (tc, f) = pair

                tc.post.map { entry ->
                    val tcWithFork = tc.copy(post = mapOf(entry.toPair()))
                    Pair(tcWithFork, f)
                }
            }
        }

        private fun loadFiller(testCases: List<GeneralStateTestCase>): List<Pair<GeneralStateTestCase, GeneralStateTestsFiller>> {
            val tcWithFiller = testCases.map { tc ->
                val fillerPath = tc.info.source.replace("src/GeneralStateTestsFiller/", "")
                val filler = fillerLoader.parse(fillerPath)

                Pair(tc, filler)
            }
            return tcWithFiller
        }
    }

    private fun assertOutDataMatches(out: String?, executed: ExecutionContext) =
        assertThat(executed.lastReturnData).isEqualTo(toByteList(out))

    private fun assertPostAccountsMatch(accounts: Accounts, executed: ExecutionContext) =
        accounts.list().forEach { a ->
            val account: Account = executed.accounts.list().find { it.address == a.address }
                ?: fail("no account with address ${a.address}")

            assertThat(a).isEqualTo(account)
        }

    private fun createExecutor(): Executor =
        Executor(
            GasCostCalculator(
                BaseGasCostCalculator(CallGasCostCalc(), PredefinedContractGasCostCalc()),
                MemoryUsageGasCostCalculator(
                    MemoryUsageGasCalc()
                )
            )
        )

    private fun parseAccounts(post: Map<String, GeneralStateTestCasePre>?): Accounts {
        val accountList = post?.map { entry ->
            val (a, d) = entry

            val contract =
                if (d.code != "0x") Contract(
                    code = toByteList(d.code),
                    storage = Storage(
                        d.storage.map { e ->
                            val (k, v) = e
                            Pair(toBigInteger(k), Word.coerceFrom(v))
                        }.toMap()
                    )
                ) else null

            Account(
                Address(a),
                toBigInteger(d.balance),
                contract,
                nonce = toBigInteger(d.nonce)
            )
        } ?: emptyList()

        return Accounts(accountList)
    }

    // use from general location
    private fun toBigInteger(number: String) =
        if (number.startsWith("0x")) BigInteger(cleanHexNumber(number), 16)
        else BigInteger(number)

    private fun cleanHexNumber(number: String) = number.replaceFirst("0x0+", "0x0").replaceFirst("0x", "")

    fun toBigIntegerOrNull(number: String?) =
        if (number == null) null
        else if (number.startsWith("0x")) BigInteger(cleanHexNumber(number), 16)
        else BigInteger(number)

}