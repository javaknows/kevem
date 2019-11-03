package com.gammadex.kevin.evm

import com.gammadex.kevin.evm.model.Byte

enum class GasPriceType(val cost: Int) {
    Zero(0),
    Base(2),
    VeryLow(3),
    Low(5),
    Mid(8),
    High(10),
    ExtCodeSize(20),
    Balance(400),
    Create(32000),
    JumpDest(1),
    SLoad(200),
    BlockHash(20),
    Formula(0)
}

enum class Opcode(val code: Byte, val numArgs: Int, val numReturn: Int, val numBytes: Int, val priceType: GasPriceType) {
    STOP(0x00, 0, 0, 1, GasPriceType.Zero),
    ADD(0x01, 2, 1, 1, GasPriceType.VeryLow),
    MUL(0x02, 2, 1, 1, GasPriceType.Low),
    SUB(0x03, 2, 1, 1, GasPriceType.VeryLow),
    DIV(0x04, 2, 1, 1, GasPriceType.Low),
    SDIV(0x05, 2, 1, 1, GasPriceType.Low),
    MOD(0x06, 2, 1, 1, GasPriceType.Low),
    SMOD(0x07, 2, 1, 1, GasPriceType.Low),
    ADDMOD(0x08, 3, 1, 1, GasPriceType.Mid),
    MULMOD(0x09, 3, 1, 1, GasPriceType.Mid),
    EXP(0x0A, 2, 1, 1, GasPriceType.Formula),
    SIGNEXTEND(0x0B, 2, 1, 1, GasPriceType.Low),
    LT(0x10, 2, 1, 1, GasPriceType.VeryLow),
    GT(0x11, 2, 1, 1, GasPriceType.VeryLow),
    SLT(0x12, 2, 1, 1, GasPriceType.VeryLow),
    SGT(0x13, 2, 1, 1, GasPriceType.VeryLow),
    EQ(0x14, 2, 1, 1, GasPriceType.VeryLow),
    ISZERO(0x15, 1, 1, 1, GasPriceType.VeryLow),
    AND(0x16, 2, 1, 1, GasPriceType.VeryLow),
    OR(0x17, 2, 1, 1, GasPriceType.VeryLow),
    XOR(0x18, 2, 1, 1, GasPriceType.VeryLow),
    NOT(0x19, 1, 1, 1, GasPriceType.VeryLow),
    BYTE(0x1A, 2, 1, 1, GasPriceType.VeryLow),
    SHL(0x1B, 2, 1, 1, GasPriceType.VeryLow),
    SHR(0x1C, 2, 1, 1, GasPriceType.VeryLow),
    SAR(0x1D, 2, 1, 1, GasPriceType.VeryLow),
    SHA3(0x20, 2, 1, 1, GasPriceType.Formula),
    ADDRESS(0x30, 0, 1, 1, GasPriceType.Base),
    BALANCE(0x31, 1, 1, 1, GasPriceType.Balance),
    ORIGIN(0x32, 0, 1, 1, GasPriceType.Base),
    CALLER(0x33, 0, 1, 1, GasPriceType.Base),
    CALLVALUE(0x34, 0, 1, 1, GasPriceType.Base),
    CALLDATALOAD(0x35, 1, 1, 1, GasPriceType.VeryLow),
    CALLDATASIZE(0x36, 0, 1, 1, GasPriceType.Base),
    CALLDATACOPY(0x37, 3, 0, 1, GasPriceType.Formula),
    CODESIZE(0x38, 0, 1, 1, GasPriceType.Base),
    CODECOPY(0x39, 3, 0, 1, GasPriceType.Formula),
    GASPRICE(0x3A, 0, 1, 1, GasPriceType.Base),
    EXTCODESIZE(0x3B, 1, 1, 1, GasPriceType.ExtCodeSize),
    EXTCODECOPY(0x3C, 4, 0, 1, GasPriceType.Formula),
    RETURNDATASIZE(0x3D, 0, 1, 1, GasPriceType.Base),
    RETURNDATACOPY(0x3E, 3, 0, 1, GasPriceType.Formula),
    BLOCKHASH(0x40, 1, 1, 1, GasPriceType.BlockHash),
    COINBASE(0x41, 0, 1, 1, GasPriceType.Base),
    TIMESTAMP(0x42, 0, 1, 1, GasPriceType.Base),
    NUMBER(0x43, 0, 1, 1, GasPriceType.Base),
    DIFFICULTY(0x44, 0, 1, 1, GasPriceType.Base),
    GASLIMIT(0x45, 0, 1, 1, GasPriceType.Base),
    POP(0x50, 1, 0, 1, GasPriceType.Base),
    MLOAD(0x51, 1, 1, 1, GasPriceType.VeryLow),
    MSTORE(0x52, 2, 0, 1, GasPriceType.VeryLow),
    MSTORE8(0x53, 2, 0, 1, GasPriceType.VeryLow),
    SLOAD(0x54, 1, 1, 1, GasPriceType.SLoad),
    SSTORE(0x55, 2, 0, 1, GasPriceType.Formula),
    JUMP(0x56, 1, 0, 1, GasPriceType.Mid),
    JUMPI(0x57, 2, 0, 1, GasPriceType.High),
    PC(0x58, 0, 1, 1, GasPriceType.Base),
    MSIZE(0x59, 0, 1, 1, GasPriceType.Base),
    GAS(0x5A, 0, 1, 1, GasPriceType.Base),
    JUMPDEST(0x5B, 0, 0, 1, GasPriceType.JumpDest),
    PUSH1(0x60, 0, 1, 2, GasPriceType.VeryLow),
    PUSH2(0x61, 0, 1, 3, GasPriceType.VeryLow),
    PUSH3(0x62, 0, 1, 4, GasPriceType.VeryLow),
    PUSH4(0x63, 0, 1, 5, GasPriceType.VeryLow),
    PUSH5(0x64, 0, 1, 6, GasPriceType.VeryLow),
    PUSH6(0x65, 0, 1, 7, GasPriceType.VeryLow),
    PUSH7(0x66, 0, 1, 8, GasPriceType.VeryLow),
    PUSH8(0x67, 0, 1, 9, GasPriceType.VeryLow),
    PUSH9(0x68, 0, 1, 10, GasPriceType.VeryLow),
    PUSH10(0x69, 0, 1, 11, GasPriceType.VeryLow),
    PUSH11(0x6A, 0, 1, 12, GasPriceType.VeryLow),
    PUSH12(0x6B, 0, 1, 13, GasPriceType.VeryLow),
    PUSH13(0x6C, 0, 1, 14, GasPriceType.VeryLow),
    PUSH14(0x6D, 0, 1, 15, GasPriceType.VeryLow),
    PUSH15(0x6E, 0, 1, 16, GasPriceType.VeryLow),
    PUSH16(0x6F, 0, 1, 17, GasPriceType.VeryLow),
    PUSH17(0x70, 0, 1, 18, GasPriceType.VeryLow),
    PUSH18(0x71, 0, 1, 19, GasPriceType.VeryLow),
    PUSH19(0x72, 0, 1, 20, GasPriceType.VeryLow),
    PUSH20(0x73, 0, 1, 21, GasPriceType.VeryLow),
    PUSH21(0x74, 0, 1, 22, GasPriceType.VeryLow),
    PUSH22(0x75, 0, 1, 23, GasPriceType.VeryLow),
    PUSH23(0x76, 0, 1, 24, GasPriceType.VeryLow),
    PUSH24(0x77, 0, 1, 25, GasPriceType.VeryLow),
    PUSH25(0x78, 0, 1, 26, GasPriceType.VeryLow),
    PUSH26(0x79, 0, 1, 27, GasPriceType.VeryLow),
    PUSH27(0x7A, 0, 1, 28, GasPriceType.VeryLow),
    PUSH28(0x7B, 0, 1, 29, GasPriceType.VeryLow),
    PUSH29(0x7C, 0, 1, 30, GasPriceType.VeryLow),
    PUSH30(0x7D, 0, 1, 31, GasPriceType.VeryLow),
    PUSH31(0x7E, 0, 1, 32, GasPriceType.VeryLow),
    PUSH32(0x7F, 0, 1, 33, GasPriceType.VeryLow),
    DUP1(0x80, 1, 2, 1, GasPriceType.VeryLow),
    DUP2(0x81, 2, 3, 1, GasPriceType.VeryLow),
    DUP3(0x82, 3, 4, 1, GasPriceType.VeryLow),
    DUP4(0x83, 4, 5, 1, GasPriceType.VeryLow),
    DUP5(0x84, 5, 6, 1, GasPriceType.VeryLow),
    DUP6(0x85, 6, 7, 1, GasPriceType.VeryLow),
    DUP7(0x86, 7, 8, 1, GasPriceType.VeryLow),
    DUP8(0x87, 8, 9, 1, GasPriceType.VeryLow),
    DUP9(0x88, 9, 10, 1, GasPriceType.VeryLow),
    DUP10(0x89, 10, 11, 1, GasPriceType.VeryLow),
    DUP11(0x8A, 11, 12, 1, GasPriceType.VeryLow),
    DUP12(0x8B, 12, 13, 1, GasPriceType.VeryLow),
    DUP13(0x8C, 13, 14, 1, GasPriceType.VeryLow),
    DUP14(0x8D, 14, 15, 1, GasPriceType.VeryLow),
    DUP15(0x8E, 15, 16, 1, GasPriceType.VeryLow),
    DUP16(0x8F, 16, 17, 1, GasPriceType.VeryLow),
    SWAP1(0x90, 2, 2, 1, GasPriceType.VeryLow),
    SWAP2(0x91, 3, 3, 1, GasPriceType.VeryLow),
    SWAP3(0x92, 4, 4, 1, GasPriceType.VeryLow),
    SWAP4(0x93, 5, 5, 1, GasPriceType.VeryLow),
    SWAP5(0x94, 6, 6, 1, GasPriceType.VeryLow),
    SWAP6(0x95, 7, 7, 1, GasPriceType.VeryLow),
    SWAP7(0x96, 8, 8, 1, GasPriceType.VeryLow),
    SWAP8(0x97, 9, 9, 1, GasPriceType.VeryLow),
    SWAP9(0x98, 10, 10, 1, GasPriceType.VeryLow),
    SWAP10(0x99, 11, 11, 1, GasPriceType.VeryLow),
    SWAP11(0x9A, 12, 12, 1, GasPriceType.VeryLow),
    SWAP12(0x9B, 13, 13, 1, GasPriceType.VeryLow),
    SWAP13(0x9C, 14, 14, 1, GasPriceType.VeryLow),
    SWAP14(0x9D, 15, 15, 1, GasPriceType.VeryLow),
    SWAP15(0x9E, 16, 16, 1, GasPriceType.VeryLow),
    SWAP16(0x9F, 17, 17, 1, GasPriceType.VeryLow),
    LOG0(0xA0, 2, 0, 1, GasPriceType.Formula),
    LOG1(0xA1, 3, 0, 1, GasPriceType.Formula),
    LOG2(0xA2, 4, 0, 1, GasPriceType.Formula),
    LOG3(0xA3, 5, 0, 1, GasPriceType.Formula),
    LOG4(0xA4, 6, 0, 1, GasPriceType.Formula),
    CREATE(0xF0, 3, 1, 1, GasPriceType.Create),
    CALL(0xF1, 7, 1, 1, GasPriceType.Formula),
    CALLCODE(0xF2, 7, 1, 1, GasPriceType.Formula),
    RETURN(0xF3, 2, 0, 11, GasPriceType.Zero),
    DELEGATECALL(0xF4, 6, 1, 1, GasPriceType.Formula),
    STATICCALL(0xFA, 6, 1, 1, GasPriceType.Formula),
    CREATE2(0xFB, 3, 1, 1, GasPriceType.Create),
    REVERT(0xFD, 2, 0, 1, GasPriceType.Zero),
    INVALID(0xFE, 0, 0, 1, GasPriceType.Zero),
    SUICIDE(0xFF, 1, 0, 1, GasPriceType.Formula);

    constructor(code: Int, numArgs: Int, numReturn: Int, numBytes: Int, priceType: GasPriceType) : this(Byte(code), numArgs, numReturn, numBytes, priceType)

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
