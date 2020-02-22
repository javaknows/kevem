package org.kevm.ethereumtests

import com.fasterxml.jackson.core.type.TypeReference


class ExplodedGeneralTestCaseLoader {

    private val testsRoot = "ethereum-tests-pack/GeneralStateTests"

    private val loader = TestCaseLoader(
        TestCaseParser(object :
            TypeReference<Map<String, GeneralStateTestCase>>() {}, testsRoot), testsRoot
    )

    private val fillerLoader = GeneralStateTestsFillerLoader("ethereum-tests-pack/GeneralStateTestsFiller")

    fun loadTestCases(): List<GeneralStateTestExplodedCase> {
        val testCases: List<GeneralStateTestCase> = loader.loadTestCases()
        val testCasesWithFiller: List<Pair<GeneralStateTestCase, GeneralStateTestsFiller>> = loadFiller(testCases)

        return explodeTestCases(testCasesWithFiller)
    }

    private fun explodeTestCases(testCasesWithFiller: List<Pair<GeneralStateTestCase, GeneralStateTestsFiller>>): List<GeneralStateTestExplodedCase> =
        testCasesWithFiller.flatMap {
            val (tc, f) = it

            with(tc.transaction) {
                (data.indices zip data).flatMap { d ->
                    (gasLimit.indices zip gasLimit).flatMap { g ->
                        (value.indices zip value).flatMap { v ->
                            val explodedTransaction = GeneralStateTestExplodedCaseTransaction(
                                data = d.second,
                                gasLimit = g.second,
                                gasPrice = gasPrice,
                                nonce = nonce,
                                secretKey = secretKey,
                                to = to,
                                value = v.second
                            )

                            val expects: List<GeneralStateTestsFillerExpect> = f.expect
                                .filter { e -> e.indexes.data.contains(d.first) || e.indexes.data.contains(-1) }
                                .filter { e -> e.indexes.gas.contains(g.first) || e.indexes.gas.contains(-1) }
                                .filter { e -> e.indexes.value.contains(v.first) || e.indexes.value.contains(-1) }

                            tc.post.keys.map { hardFork ->
                                val expects: List<GeneralStateTestsFillerExpect> =
                                    filterExpectsForHardFork(expects, hardFork)

                                val results: Map<String, GeneralStateTestsFillerResult> =
                                    extractResultsForExpect(expects)

                                val name =
                                    "${tc.name} - $hardFork - data=${d.second}, gas=${g.second}, value=${v.second}"

                                val post = tc.post[hardFork] ?: emptyList()

                                GeneralStateTestExplodedCase(
                                    name = name,
                                    env = tc.env,
                                    post = post,
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
        }

    private fun filterExpectsForHardFork(
        expects: List<GeneralStateTestsFillerExpect>,
        hardFork: String
    ): List<GeneralStateTestsFillerExpect> = expects
        .flatMap { e: GeneralStateTestsFillerExpect ->
            e.network.map { network: String -> Pair(network, e) }
        }
        .filter { it.first.contains(hardFork) }
        .map { it.second }


    private fun extractResultsForExpect(eOnlyForFork: List<GeneralStateTestsFillerExpect>): Map<String, GeneralStateTestsFillerResult> =
        eOnlyForFork.flatMap { e ->
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

    private fun loadFiller(testCases: List<GeneralStateTestCase>): List<Pair<GeneralStateTestCase, GeneralStateTestsFiller>> =
        testCases.map { tc ->
            val fillerPath = tc.info.source.replace("src/GeneralStateTestsFiller/", "")
            val filler = fillerLoader.parse(fillerPath)

            Pair(tc, filler)
        }
}