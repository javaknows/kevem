package com.gammadex.kevin.evm.gas

enum class GasCost(val cost: Int) {
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
    Formula(0),
    NewAccount(25000),
    CallValue(9000),
    Call(700),
    CallStipend(375),
    Log(2300),
    LogData(8),
    LogTopic(375)
}