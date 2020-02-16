Feature: Gas costs for precompiled contract execution

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000


  Scenario Outline: Gas cost for ECDSARECOVER precompiled contract - <opcode>
    Given the stack contains elements <stack>
    And 0x0049872459827432342344987245982743234234498724598274323423429943000000000000000000000000000000000000000000000000000000000000001be8359c341771db7f9ea3a662a1741d27775ce277961470028e054ed3285aab8e31f63eaac35c4e6178abbc2a1073040ac9bbb0b67f2bc89a2e9593ba9abe8c53 is stored in memory at location 0x0
    When opcode <opcode> is executed
    Then 3000 gas is now used by the previous call context

    Examples:
      | opcode       | stack                                 |
      | CALL         | [0x1, 0x1, 0x0, 0x0, 0x80, 0x0, 0x20] |
      | CALLCODE     | [0x1, 0x1, 0x0, 0x0, 0x80, 0x0, 0x20] |
      | DELEGATECALL | [0x1, 0x1,  0x0, 0x80, 0x0, 0x20]     |
      | STATICCALL   | [0x1, 0x1,  0x0, 0x80, 0x0, 0x20]     |


  Scenario Outline: Gas cost for SHA256 precompiled contract - <callType>
    Given the stack contains elements <stackElements>
    And 0x11223344 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then 72 gas is now used by the previous call context
    # gas = 60 + 12 * 1

    Examples:
      | callType     | stackElements                        |
      | CALL         | [0x1, 0x2, 0x0, 0x0, 0x4, 0x0, 0x20] |
      | CALLCODE     | [0x1, 0x2, 0x0, 0x0, 0x4, 0x0, 0x20] |
      | DELEGATECALL | [0x1, 0x2, 0x0, 0x4, 0x0, 0x20]      |
      | STATICCALL   | [0x1, 0x2, 0x0, 0x4, 0x0, 0x20]      |


  Scenario Outline: Gas cost for RIPEMD160 precompiled contract - <callType>
    Given the stack contains elements <stackElements>
    And 0x61 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then 720 gas is now used by the previous call context
    # gas = 600 + 120 * 1

    Examples:
      | callType     | stackElements                        |
      | CALL         | [0x1, 0x3, 0x0, 0x0, 0x1, 0x0, 0x20] |
      | CALLCODE     | [0x1, 0x3, 0x0, 0x0, 0x1, 0x0, 0x20] |
      | DELEGATECALL | [0x1, 0x3, 0x0, 0x1, 0x0, 0x20]      |
      | STATICCALL   | [0x1, 0x3, 0x0, 0x1, 0x0, 0x20]      |


  Scenario Outline: Gas cost for IDENTITY precompiled contract - <callType>
    Given the stack contains elements <stackElements>
    And 0x61ab is stored in memory at location 0x0
    When opcode <callType> is executed
    Then 18 gas is now used by the previous call context
    # gas = 15 + 3 * 1

    Examples:
      | callType     | stackElements                       |
      | CALL         | [0x1, 0x4, 0x0, 0x0, 0x2, 0x0, 0x2] |
      | CALLCODE     | [0x1, 0x4, 0x0, 0x0, 0x2, 0x0, 0x2] |
      | DELEGATECALL | [0x1, 0x4, 0x0, 0x2, 0x0, 0x2]      |
      | STATICCALL   | [0x1, 0x4, 0x0, 0x2, 0x0, 0x2]      |


  Scenario Outline: Gas cost for EXPMOD precompiled contract when factor less than 64 - <callType>
    Given the stack contains elements <stackElements>
    And 0x00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000002003ffff80 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then 102 gas is now used by the previous call context
    # gas = ((32 * 32) * 2) / 20

    Examples:
      | callType     | stackElements                         |
      | CALL         | [0x1, 0x5, 0x0, 0x0, 0x64, 0x0, 0x32] |
      | CALLCODE     | [0x1, 0x5, 0x0, 0x0, 0x64, 0x0, 0x32] |
      | DELEGATECALL | [0x1, 0x5, 0x0, 0x64, 0x0, 0x32]      |
      | STATICCALL   | [0x1, 0x5, 0x0, 0x64, 0x0, 0x32]      |


  Scenario Outline: Gas cost for EXPMOD precompiled contract when factor greater than 64 but less than or eq 1024 - <callType>
    Given the stack contains elements <stackElements>
    And 0x00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000040003ffff80 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then 35737 gas is now used by the previous call context
    # f = (1048576 / 4) + 96 * 1024 - 3072 = 357376
    # gas = (f * 2) / 20

    Examples:
      | callType     | stackElements                         |
      | CALL         | [0x1, 0x5, 0x0, 0x0, 0x64, 0x0, 0x32] |
      | CALLCODE     | [0x1, 0x5, 0x0, 0x0, 0x64, 0x0, 0x32] |
      | DELEGATECALL | [0x1, 0x5, 0x0, 0x64, 0x0, 0x32]      |
      | STATICCALL   | [0x1, 0x5, 0x0, 0x64, 0x0, 0x32]      |


  Scenario Outline: Gas cost for EXPMOD precompiled contract when factor greater than 1024 - <callType>
    Given the stack contains elements <stackElements>
    And 0x00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000040103ffff80 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then 35798 gas is now used by the previous call context
    # f = (1050625 / 16) + 480 * 1025 - 199680 = 357984
    # gas = (f * 2) / 20

    Examples:
      | callType     | stackElements                         |
      | CALL         | [0x1, 0x5, 0x0, 0x0, 0x64, 0x0, 0x32] |
      | CALLCODE     | [0x1, 0x5, 0x0, 0x0, 0x64, 0x0, 0x32] |
      | DELEGATECALL | [0x1, 0x5, 0x0, 0x64, 0x0, 0x32]      |
      | STATICCALL   | [0x1, 0x5, 0x0, 0x64, 0x0, 0x32]      |


  Scenario Outline: Gas cost for BNADD precompiled contract - <callType>
    Given the stack contains elements <stackElements>
    And 0x17c139df0efee0f766bc0204762b774362e4ded88953a39ce849a8a7fa163fa901e0559bacb160664764a357af8a9fe70baa9258e0b959273ffc5718c6d4cc7c039730ea8dff1254c0fee9c0ea777d29a9c710b7e616683f194f18c43b43b869073a5ffcc6fc7a28c30723d6e58ce577356982d65b833a5a5c15bf9024b43d98 is stored in memory at location 0x0
    And the highest byte touched in memory is 296
    When opcode <callType> is executed
    Then 500 gas is now used by the previous call context

    Examples:
      | callType     | stackElements                          |
      | CALL         | [0x1, 0x6, 0x0, 0x0, 0x128, 0x0, 0x64] |
      | CALLCODE     | [0x1, 0x6, 0x0, 0x0, 0x128, 0x0, 0x64] |
      | DELEGATECALL | [0x1, 0x6, 0x0, 0x128, 0x0, 0x64]      |
      | STATICCALL   | [0x1, 0x6, 0x0, 0x128, 0x0, 0x64]      |


  Scenario Outline: Gas cost for BNMUL precompiled contract - <callType>
    Given the stack contains elements <stackElements>
    And 0x1a87b0584ce92f4593d161480614f2989035225609f08058ccfa3d0f940febe31a2f3c951f6dadcc7ee9007dff81504b0fcd6d7cf59996efdc33d92bf7f9f8f60000000000000000000000000000000000000000000000000000000000000009 is stored in memory at location 0x0
    And the highest byte touched in memory is 0x96
    When opcode <callType> is executed
    Then 40000 gas is now used by the previous call context

    Examples:
      | callType     | stackElements                         |
      | CALL         | [0x1, 0x7, 0x0, 0x0, 0x96, 0x0, 0x64] |
      | CALLCODE     | [0x1, 0x7, 0x0, 0x0, 0x96, 0x0, 0x64] |
      | DELEGATECALL | [0x1, 0x7, 0x0, 0x96, 0x0, 0x64]      |
      | STATICCALL   | [0x1, 0x7, 0x0, 0x96, 0x0, 0x64]      |


  Scenario Outline: Gas cost for SNARKV precompiled contract - <callType>
    Given the stack contains elements <stackElements>
    And 0x00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c21800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa000000000000000000000000000000000000000000000000000000000000000130644e72e131a029b85045b68181585d97816a916871ca8d3c208c16d87cfd45198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c21800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa is stored in memory at location 0x0
    When opcode <callType> is executed
    Then there is no last error
    Then 260000 gas is now used by the previous call context
    # gas = 80000 * 2 + 100000

    Examples:
      | callType     | stackElements                          |
      | CALL         | [0x1, 0x8, 0x0, 0x0, 0x180, 0x0, 0x32] |
      | CALLCODE     | [0x1, 0x8, 0x0, 0x0, 0x180, 0x0, 0x32] |
      | DELEGATECALL | [0x1, 0x8, 0x0, 0x180, 0x0, 0x32]      |
      | STATICCALL   | [0x1, 0x8, 0x0, 0x180, 0x0, 0x32]      |


  Scenario Outline: Fail when not enough gas left to execute precompiled contract - <callType>
    Given there is 400 gas remaining
    And the stack contains elements <stackElements>
    And 0x17c139df0efee0f766bc0204762b774362e4ded88953a39ce849a8a7fa163fa901e0559bacb160664764a357af8a9fe70baa9258e0b959273ffc5718c6d4cc7c039730ea8dff1254c0fee9c0ea777d29a9c710b7e616683f194f18c43b43b869073a5ffcc6fc7a28c30723d6e58ce577356982d65b833a5a5c15bf9024b43d98 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then the last error is now OUT_OF_GAS with message "Out of gas"

    Examples:
      | callType     | stackElements                          |
      | CALL         | [0x1, 0x6, 0x0, 0x0, 0x128, 0x0, 0x64] |
      | CALLCODE     | [0x1, 0x6, 0x0, 0x0, 0x128, 0x0, 0x64] |
      | DELEGATECALL | [0x1, 0x6, 0x0, 0x128, 0x0, 0x64]      |
      | STATICCALL   | [0x1, 0x6, 0x0, 0x128, 0x0, 0x64]      |


  Scenario Outline: BLAKE2 precompiled smart contract gas cost is 0 when 0 rounds - <callType>
    Given the stack contains elements <stackElements>
    And 0x000000048c9bdf267e6096a3ba7ca8485ae67bb2bf894fe72f36e3cf1361d5f3af54fa5d182e6ad7f520e511f6c3e2b8c68059b6bbd41fbabd9831f79217e1319cde05b61626300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000300000000000000000000000000000001 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then 0 gas is now used by the previous call context

    Examples:
      | callType     | stackElements                         |
      | CALL         | [0x1, 0x9, 0x0, 0x0, 0xD5, 0x0, 0x64] |
      | CALLCODE     | [0x1, 0x9, 0x0, 0x0, 0xD5, 0x0, 0x64] |
      | DELEGATECALL | [0x1, 0x9, 0x0, 0xD5, 0x0, 0x64]      |
      | STATICCALL   | [0x1, 0x9, 0x0, 0xD5, 0x0, 0x64]      |


  Scenario Outline: BLAKE2 precompiled smart contract gas cost is 1 when 1 round - <callType>
    Given the stack contains elements <stackElements>
    And 0x000000148c9bdf267e6096a3ba7ca8485ae67bb2bf894fe72f36e3cf1361d5f3af54fa5d182e6ad7f520e511f6c3e2b8c68059b6bbd41fbabd9831f79217e1319cde05b61626300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000300000000000000000000000000000001 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then 1 gas is now used by the previous call context

    Examples:
      | callType     | stackElements                         |
      | CALL         | [0x1, 0x9, 0x0, 0x0, 0xD5, 0x0, 0x64] |
      | CALLCODE     | [0x1, 0x9, 0x0, 0x0, 0xD5, 0x0, 0x64] |
      | DELEGATECALL | [0x1, 0x9, 0x0, 0xD5, 0x0, 0x64]      |
      | STATICCALL   | [0x1, 0x9, 0x0, 0xD5, 0x0, 0x64]      |