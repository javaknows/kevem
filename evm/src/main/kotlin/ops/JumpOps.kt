package org.kevm.evm.ops

import org.kevm.evm.Opcode
import org.kevm.evm.model.*

object JumpOps {

    fun jump(executionCtx: ExecutionContext): ExecutionContext = with(executionCtx) {
        val (destination, newStack) = stack.popWord()

        jumpOrFailIfInvalid(executionCtx, destination.toInt(), newStack)
    }

    fun jumpi(executionCtx: ExecutionContext): ExecutionContext = with(executionCtx) {
        val (elements, newStack) = stack.popWords(2)
        val (destination, condition) = elements

        if (condition.toBoolean()) {
            jumpOrFailIfInvalid(executionCtx, destination.toInt(), newStack)
        } else executionCtx.updateCurrentCallCtxIfPresent { ctx ->
            ctx.copy(stack = newStack, currentLocation = ctx.currentLocation + 1)
        }
    }

    fun jumpDest(executionCtx: ExecutionContext) = executionCtx

    private fun jumpOrFailIfInvalid(executionCtx: ExecutionContext, dest: Int, newStack: Stack): ExecutionContext =
        with(executionCtx) {
            val call = callStack.last()

            if (dest !in call.code.indices) HaltOps.fail(executionCtx, error(dest))
            else {
                val nextOpCode = Opcode.byCode[call.code[dest]]

                if (nextOpCode == Opcode.JUMPDEST)
                    executionCtx.updateCurrentCallCtx(stack = newStack, currentLocation = dest)
                else HaltOps.fail(executionCtx, error(dest))
            }
        }

    private fun error(dest: Int) = EvmError(ErrorCode.INVALID_JUMP_DESTINATION, "Invalid jump destination 0x${dest.toString(16)}")
}