package com.gammadex.kevin.evm.ops

import com.gammadex.kevin.evm.Opcode
import com.gammadex.kevin.evm.model.*

object JumpOps {

    private val invalidJumpError = EvmError(ErrorCode.INVALID_JUMP_DESTINATION, "Invalid jump destination")

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

            if (dest !in call.code.indices) HaltOps.fail(executionCtx, invalidJumpError)
            else {
                val nextOpCode = Opcode.byCode[call.code[dest]]

                if (nextOpCode == Opcode.JUMPDEST)
                    executionCtx.updateCurrentCallCtx(stack = newStack, currentLocation = dest)
                else HaltOps.fail(executionCtx, invalidJumpError)
            }
        }
}