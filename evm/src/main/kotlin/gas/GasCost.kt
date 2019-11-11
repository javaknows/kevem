package com.gammadex.kevin.evm.gas

enum class GasCategory {
    Formula,
    MemoryUsage,
    Simple
}

enum class GasCost(val cost: Int) {
    Zero(0),
    Base(2),
    VeryLow(3),
    Low(5),
    Mid(8),
    High(10),
    ExtCode(700),
    Balance(400),
    SLoad(200),
    JumpDest(1),
    SSet(20000),
    SReset(5000),
    Create(32000),
    CodeDeposit(200),
    Call(700),
    CallValue(9000),
    CallStipend(2300),
    NewAccount(25000),
    Exp(10),
    ExpByte(50),
    Memory(3),
    BlockHash(20),
    Formula(0),
    Copy(3),
    Log(2300),
    LogData(8),
    LogTopic(375),

}