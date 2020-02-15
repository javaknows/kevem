package org.kevm.evm

enum class HardFork(val eip: EIP) {
    Homestead(EIP.EIP606),
    SpuriousDragon(EIP.EIP607),
    TangerineWhistle(EIP.EIP608),
    Byzantium(EIP.EIP609),
    Constantinople(EIP.EIP1013),
    Istanbul(EIP.EIP1679),
    Petersburg(EIP.EIP1716);

    fun eips(): List<EIP> = eipsAcc(setOf(eip)).sorted()
}

enum class EIP(val description: String, val immediateDependencies: List<EIP> = emptyList()) {
    EIP2("Homestead Hard-fork Changes"),
    EIP7("DELEGATECALL"),
    EIP8("devp2p Forward Compatibility Requirements for Homestead"),
    EIP100("Change difficulty adjustment to target mean block time including uncles"),
    EIP140("REVERT instruction"),
    EIP145("Bitwise shifting instructions in EVM"),
    EIP150("Gas cost changes for IO-heavy operations"),
    EIP152("Add BLAKE2 compression function `F` precompile"),
    EIP155("Simple replay attack protection"),
    EIP160("EXP cost increase"),
    EIP161("State trie clearing (invariant-preserving alternative)"),
    EIP170("Contract code size limit"),
    EIP196("Precompiled contracts for addition and scalar multiplication on the elliptic curve alt_bn128"),
    EIP197("Precompiled contracts for optimal ate pairing check on the elliptic curve alt_bn128"),
    EIP198("Big integer modular exponentiation"),
    EIP211("New opcodes: RETURNDATASIZE and RETURNDATACOPY"),
    EIP214("New opcode STATICCALL"),
    EIP649("Metropolis Difficulty Bomb Delay and Block Reward Reduction"),
    EIP658("Embedding transaction status code in receipts", listOf(EIP140)),
    EIP606("Hardfork Homestead", listOf(EIP2, EIP7, EIP8)),
    EIP779("Hardfork DAO Fork", listOf(EIP606)),
    EIP1014("Skinny CREATE2"),
    EIP1052("EXTCODEHASH opcode", listOf(EIP161)),
    EIP1108("Reduce alt_bn128 precompile gas costs", listOf(EIP196, EIP197)),
    EIP1234("Constantinople Difficulty Bomb Delay and Block Reward Adjustment"),
    EIP1283("Net gas metering for SSTORE without dirty maps"),
    EIP1344("ChainID opcode", listOf(EIP155)),
    EIP1884("Repricing for trie-size-dependent opcodes", listOf(EIP150, EIP1052)),
    EIP2028("Transaction data gas cost reduction"),
    EIP2200("Structured Definitions for Net Gas Metering"),
    EIP2384("Muir Glacier Difficulty Bomb Delay"),
    EIP608("Hardfork Tangerine Whistle", listOf(EIP150, EIP779)),
    EIP607("Hardfork Spurious Dragon", listOf(EIP155, EIP160, EIP161, EIP170, EIP608)),
    EIP609(
        "Hardfork Byzantium",
        listOf(EIP100, EIP140, EIP196, EIP197, EIP198, EIP211, EIP214, EIP607, EIP649, EIP658)
    ),
    EIP1013("Hardfork Constantinople", listOf(EIP145, EIP609, EIP1014, EIP1052, EIP1234, EIP1283)),
    EIP1716("Hardfork Petersburg", listOf(EIP1013, EIP1283)),
    EIP1679("Hardfork Istanbul", listOf(EIP1716, EIP152, EIP1108, EIP1344, EIP1884, EIP2028, EIP2200));

    fun dependencies(): List<EIP> = eipsAcc(immediateDependencies.toSet()).sorted()
}

private tailrec fun eipsAcc(eips: Set<EIP>, acc: List<EIP> = emptyList()): List<EIP> =
    if (eips.isEmpty()) acc
    else {
        val nextEip = eips.first()

        val add =
            if (acc.contains(nextEip)) emptyList()
            else nextEip.immediateDependencies

        val remaining = eips - nextEip

        eipsAcc(remaining + add, acc + nextEip)
    }