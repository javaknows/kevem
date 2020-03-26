package org.kevem.evm

import org.kevem.common.KevemException
import org.kevem.evm.crypto.*
import org.kevem.evm.model.Address
import org.kevem.evm.model.Byte
import org.kevem.evm.model.ExecutionContext
import org.kevem.evm.model.Word
import org.kevem.evm.ops.CallArguments
import org.kevem.evm.precompiled.expmod
import java.math.BigInteger

object PrecompiledContractExecutor {

    fun isPrecompiledContractCall(address: Address) =
        address.value <= BigInteger("9") && address.value > BigInteger.ZERO

    fun doPrecompiled(context: ExecutionContext, args: CallArguments): ExecutionContext =
        when (args.address.value.toInt()) {
            1 -> executePrecompiled(context, args) { input -> Pair(true, Byte.trimAndPadLeft(ecdsarecover(input), 32)) }
            2 -> executePrecompiled(context, args) { input -> Pair(true, Word(sha256(input)).data) }
            3 -> executePrecompiled(context, args) { input -> Pair(true, Word.coerceFrom(ripemd160(input)).data) }
            4 -> executePrecompiled(context, args) { input -> Pair(true, input) }
            5 -> executePrecompiled(context, args) { input -> Pair(true, expmod(input)) }
            6 -> executePrecompiled(context, args) { input -> Pair(true, bnAdd(input)) }
            7 -> executePrecompiled(context, args) { input -> Pair(true, bnMul(input)) }
            8 -> executePrecompiled(context, args) { input -> Pair(true, snarkV(input)) }
            9 -> executePrecompiled(context, args) { input -> Pair(true, blake2(input)) }
            else -> throw KevemException("Unknown precompiled smart contract address ${args.address}")
        }

    private fun executePrecompiled(
        context: ExecutionContext,
        args: CallArguments,
        func: (List<Byte>) -> Pair<Boolean, List<Byte>>
    ): ExecutionContext =
        with(context) {
            val (input, newMemory) = memory.read(args.inLocation, args.inSize)

            val result = try {
                func(input)
            } catch (e: Exception) {
                Pair(false, emptyList<Byte>())
            }

            val newMemory2 = result.let {
                if (result.first && it.second.isNotEmpty())
                    newMemory.write(args.outLocation, Byte.trimAndPadRight(it.second, args.outSize))
                else
                    newMemory
            }

            val newStack = stack.pushWord(Word.coerceFrom(result.first))

            updateCurrentCallCtx(memory = newMemory2, stack = newStack)
        }

}