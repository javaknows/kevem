package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.gas.GasCost
import com.gammadex.kevin.evm.model.Byte

// TODO - move gas cost out of here
enum class Opcode(val code: Byte, val numArgs: Int, val numReturn: Int, val numBytes: Int, val cost: GasCost) {
    STOP(0x00, 0, 0, 1, GasCost.Zero),
    ADD(0x01, 2, 1, 1, GasCost.VeryLow),
    MUL(0x02, 2, 1, 1, GasCost.Low),
    SUB(0x03, 2, 1, 1, GasCost.VeryLow),
    DIV(0x04, 2, 1, 1, GasCost.Low),
    SDIV(0x05, 2, 1, 1, GasCost.Low),
    MOD(0x06, 2, 1, 1, GasCost.Low),
    SMOD(0x07, 2, 1, 1, GasCost.Low),
    ADDMOD(0x08, 3, 1, 1, GasCost.Mid),
    MULMOD(0x09, 3, 1, 1, GasCost.Mid),
    EXP(0x0A, 2, 1, 1, GasCost.Formula),
    SIGNEXTEND(0x0B, 2, 1, 1, GasCost.Low),
    LT(0x10, 2, 1, 1, GasCost.VeryLow),
    GT(0x11, 2, 1, 1, GasCost.VeryLow),
    SLT(0x12, 2, 1, 1, GasCost.VeryLow),
    SGT(0x13, 2, 1, 1, GasCost.VeryLow),
    EQ(0x14, 2, 1, 1, GasCost.VeryLow),
    ISZERO(0x15, 1, 1, 1, GasCost.VeryLow),
    AND(0x16, 2, 1, 1, GasCost.VeryLow),
    OR(0x17, 2, 1, 1, GasCost.VeryLow),
    XOR(0x18, 2, 1, 1, GasCost.VeryLow),
    NOT(0x19, 1, 1, 1, GasCost.VeryLow),
    BYTE(0x1A, 2, 1, 1, GasCost.VeryLow),
    SHL(0x1B, 2, 1, 1, GasCost.VeryLow),
    SHR(0x1C, 2, 1, 1, GasCost.VeryLow),
    SAR(0x1D, 2, 1, 1, GasCost.VeryLow),
    SHA3(0x20, 2, 1, 1, GasCost.Formula),
    ADDRESS(0x30, 0, 1, 1, GasCost.Base),
    BALANCE(0x31, 1, 1, 1, GasCost.Balance),
    ORIGIN(0x32, 0, 1, 1, GasCost.Base),
    CALLER(0x33, 0, 1, 1, GasCost.Base),
    CALLVALUE(0x34, 0, 1, 1, GasCost.Base),
    CALLDATALOAD(0x35, 1, 1, 1, GasCost.VeryLow),
    CALLDATASIZE(0x36, 0, 1, 1, GasCost.Base),
    CALLDATACOPY(0x37, 3, 0, 1, GasCost.Formula),
    CODESIZE(0x38, 0, 1, 1, GasCost.Base),
    CODECOPY(0x39, 3, 0, 1, GasCost.Formula),
    GASPRICE(0x3A, 0, 1, 1, GasCost.Base),
    EXTCODESIZE(0x3B, 1, 1, 1, GasCost.ExtCodeSize),
    EXTCODECOPY(0x3C, 4, 0, 1, GasCost.Formula),
    RETURNDATASIZE(0x3D, 0, 1, 1, GasCost.Base),
    RETURNDATACOPY(0x3E, 3, 0, 1, GasCost.Formula),
    BLOCKHASH(0x40, 1, 1, 1, GasCost.BlockHash),
    COINBASE(0x41, 0, 1, 1, GasCost.Base),
    TIMESTAMP(0x42, 0, 1, 1, GasCost.Base),
    NUMBER(0x43, 0, 1, 1, GasCost.Base),
    DIFFICULTY(0x44, 0, 1, 1, GasCost.Base),
    GASLIMIT(0x45, 0, 1, 1, GasCost.Base),
    POP(0x50, 1, 0, 1, GasCost.Base),
    MLOAD(0x51, 1, 1, 1, GasCost.VeryLow),
    MSTORE(0x52, 2, 0, 1, GasCost.VeryLow),
    MSTORE8(0x53, 2, 0, 1, GasCost.VeryLow),
    SLOAD(0x54, 1, 1, 1, GasCost.SLoad),
    SSTORE(0x55, 2, 0, 1, GasCost.Formula),
    JUMP(0x56, 1, 0, 1, GasCost.Mid),
    JUMPI(0x57, 2, 0, 1, GasCost.High),
    PC(0x58, 0, 1, 1, GasCost.Base),
    MSIZE(0x59, 0, 1, 1, GasCost.Base),
    GAS(0x5A, 0, 1, 1, GasCost.Base),
    JUMPDEST(0x5B, 0, 0, 1, GasCost.JumpDest),
    PUSH1(0x60, 0, 1, 2, GasCost.VeryLow),
    PUSH2(0x61, 0, 1, 3, GasCost.VeryLow),
    PUSH3(0x62, 0, 1, 4, GasCost.VeryLow),
    PUSH4(0x63, 0, 1, 5, GasCost.VeryLow),
    PUSH5(0x64, 0, 1, 6, GasCost.VeryLow),
    PUSH6(0x65, 0, 1, 7, GasCost.VeryLow),
    PUSH7(0x66, 0, 1, 8, GasCost.VeryLow),
    PUSH8(0x67, 0, 1, 9, GasCost.VeryLow),
    PUSH9(0x68, 0, 1, 10, GasCost.VeryLow),
    PUSH10(0x69, 0, 1, 11, GasCost.VeryLow),
    PUSH11(0x6A, 0, 1, 12, GasCost.VeryLow),
    PUSH12(0x6B, 0, 1, 13, GasCost.VeryLow),
    PUSH13(0x6C, 0, 1, 14, GasCost.VeryLow),
    PUSH14(0x6D, 0, 1, 15, GasCost.VeryLow),
    PUSH15(0x6E, 0, 1, 16, GasCost.VeryLow),
    PUSH16(0x6F, 0, 1, 17, GasCost.VeryLow),
    PUSH17(0x70, 0, 1, 18, GasCost.VeryLow),
    PUSH18(0x71, 0, 1, 19, GasCost.VeryLow),
    PUSH19(0x72, 0, 1, 20, GasCost.VeryLow),
    PUSH20(0x73, 0, 1, 21, GasCost.VeryLow),
    PUSH21(0x74, 0, 1, 22, GasCost.VeryLow),
    PUSH22(0x75, 0, 1, 23, GasCost.VeryLow),
    PUSH23(0x76, 0, 1, 24, GasCost.VeryLow),
    PUSH24(0x77, 0, 1, 25, GasCost.VeryLow),
    PUSH25(0x78, 0, 1, 26, GasCost.VeryLow),
    PUSH26(0x79, 0, 1, 27, GasCost.VeryLow),
    PUSH27(0x7A, 0, 1, 28, GasCost.VeryLow),
    PUSH28(0x7B, 0, 1, 29, GasCost.VeryLow),
    PUSH29(0x7C, 0, 1, 30, GasCost.VeryLow),
    PUSH30(0x7D, 0, 1, 31, GasCost.VeryLow),
    PUSH31(0x7E, 0, 1, 32, GasCost.VeryLow),
    PUSH32(0x7F, 0, 1, 33, GasCost.VeryLow),
    DUP1(0x80, 1, 2, 1, GasCost.VeryLow),
    DUP2(0x81, 2, 3, 1, GasCost.VeryLow),
    DUP3(0x82, 3, 4, 1, GasCost.VeryLow),
    DUP4(0x83, 4, 5, 1, GasCost.VeryLow),
    DUP5(0x84, 5, 6, 1, GasCost.VeryLow),
    DUP6(0x85, 6, 7, 1, GasCost.VeryLow),
    DUP7(0x86, 7, 8, 1, GasCost.VeryLow),
    DUP8(0x87, 8, 9, 1, GasCost.VeryLow),
    DUP9(0x88, 9, 10, 1, GasCost.VeryLow),
    DUP10(0x89, 10, 11, 1, GasCost.VeryLow),
    DUP11(0x8A, 11, 12, 1, GasCost.VeryLow),
    DUP12(0x8B, 12, 13, 1, GasCost.VeryLow),
    DUP13(0x8C, 13, 14, 1, GasCost.VeryLow),
    DUP14(0x8D, 14, 15, 1, GasCost.VeryLow),
    DUP15(0x8E, 15, 16, 1, GasCost.VeryLow),
    DUP16(0x8F, 16, 17, 1, GasCost.VeryLow),
    SWAP1(0x90, 2, 2, 1, GasCost.VeryLow),
    SWAP2(0x91, 3, 3, 1, GasCost.VeryLow),
    SWAP3(0x92, 4, 4, 1, GasCost.VeryLow),
    SWAP4(0x93, 5, 5, 1, GasCost.VeryLow),
    SWAP5(0x94, 6, 6, 1, GasCost.VeryLow),
    SWAP6(0x95, 7, 7, 1, GasCost.VeryLow),
    SWAP7(0x96, 8, 8, 1, GasCost.VeryLow),
    SWAP8(0x97, 9, 9, 1, GasCost.VeryLow),
    SWAP9(0x98, 10, 10, 1, GasCost.VeryLow),
    SWAP10(0x99, 11, 11, 1, GasCost.VeryLow),
    SWAP11(0x9A, 12, 12, 1, GasCost.VeryLow),
    SWAP12(0x9B, 13, 13, 1, GasCost.VeryLow),
    SWAP13(0x9C, 14, 14, 1, GasCost.VeryLow),
    SWAP14(0x9D, 15, 15, 1, GasCost.VeryLow),
    SWAP15(0x9E, 16, 16, 1, GasCost.VeryLow),
    SWAP16(0x9F, 17, 17, 1, GasCost.VeryLow),
    LOG0(0xA0, 2, 0, 1, GasCost.Formula),
    LOG1(0xA1, 3, 0, 1, GasCost.Formula),
    LOG2(0xA2, 4, 0, 1, GasCost.Formula),
    LOG3(0xA3, 5, 0, 1, GasCost.Formula),
    LOG4(0xA4, 6, 0, 1, GasCost.Formula),
    CREATE(0xF0, 3, 1, 1, GasCost.Create),
    CALL(0xF1, 7, 1, 1, GasCost.Formula),
    CALLCODE(0xF2, 7, 1, 1, GasCost.Formula),
    RETURN(0xF3, 2, 0, 11, GasCost.Zero),
    DELEGATECALL(0xF4, 6, 1, 1, GasCost.Formula),
    STATICCALL(0xFA, 6, 1, 1, GasCost.Formula),
    CREATE2(0xFB, 3, 1, 1, GasCost.Create),
    REVERT(0xFD, 2, 0, 1, GasCost.Zero),
    INVALID(0xFE, 0, 0, 1, GasCost.Zero),
    SUICIDE(0xFF, 1, 0, 1, GasCost.Formula);

    constructor(code: Int, numArgs: Int, numReturn: Int, numBytes: Int, cost: GasCost) : this(Byte(code), numArgs, numReturn, numBytes, cost)

    companion object {
        val byCode = values().map { it.code to it }.toMap()

        // TODO - validate this set agsinst GETH
        private val nonStaticOpcodes = setOf(
            SSTORE,
            SUICIDE,
            REVERT,
            CREATE,
            CREATE2,
            CALL,
            CALLCODE,
            DELEGATECALL,
            LOG0,
            LOG1,
            LOG2,
            LOG3,
            LOG4
        )

        private val haltingOpcodes = setOf(STOP, RETURN, REVERT, SUICIDE, INVALID)

        private val callOpcodes = setOf(CALL, CALLCODE, STATICCALL, DELEGATECALL)

        private val jumpOpcodes = setOf(JUMP, JUMPI)

        fun isAllowedInStatic(opcode: Opcode?) = !nonStaticOpcodes.contains(opcode)

        fun isHaltingOpcode(opcode: Opcode?) = haltingOpcodes.contains(opcode)

        fun isCallOpcode(opcode: Opcode?) = callOpcodes.contains(opcode)

        fun isJumpOpcode(opcode: Opcode?) = jumpOpcodes.contains(opcode)

        fun numBytes(opcode: Opcode?) = opcode?.numBytes ?: 1

        fun numArgs(opcode: Opcode?) = opcode?.numArgs ?: 0

        fun fromName(name: String): Opcode? = values().find { it.name == name.toUpperCase() }

        fun fromCode(code: Byte): Opcode? = values().find { it.code == code }
    }
}
