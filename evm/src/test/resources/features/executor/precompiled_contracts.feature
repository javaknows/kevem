Feature: Functionality of precompiled contracts

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000


  Scenario Outline: ECRECOVER precompiled smart contract can be executed correctly - <callType>
    Given the stack contains elements <stackElements>
    And 0x0049872459827432342344987245982743234234498724598274323423429943000000000000000000000000000000000000000000000000000000000000001be8359c341771db7f9ea3a662a1741d27775ce277961470028e054ed3285aab8e31f63eaac35c4e6178abbc2a1073040ac9bbb0b67f2bc89a2e9593ba9abe8c53 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then there is no last error
    And 32 bytes of memory from position 0 is 0x0000000000000000000000000c65a9d9ffc02c7c99e36e32ce0f950c7804ceda

    Examples:
      | callType     | stackElements                         |
      | CALL         | [0x1, 0x1, 0x0, 0x0, 0x80, 0x0, 0x20] |
      | CALLCODE     | [0x1, 0x1, 0x0, 0x0, 0x80, 0x0, 0x20] |
      | DELEGATECALL | [0x1, 0x1, 0x0, 0x80, 0x0, 0x20]      |
      | STATICCALL   | [0x1, 0x1, 0x0, 0x80, 0x0, 0x20]      |


  Scenario Outline: SHA256 precompiled smart contract can be executed correctly - <callType>
    Given the stack contains elements <stackElements>
    And 0x11223344 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then there is no last error
    And 32 bytes of memory from position 0 is 0x1a835ed8734f86355ca5b835d824d486993aabf1913cd3a011b7446c0514b7c9

    Examples:
      | callType     | stackElements                        |
      | CALL         | [0x1, 0x2, 0x0, 0x0, 0x4, 0x0, 0x20] |
      | CALLCODE     | [0x1, 0x2, 0x0, 0x0, 0x4, 0x0, 0x20] |
      | DELEGATECALL | [0x1, 0x2, 0x0, 0x4, 0x0, 0x20]      |
      | STATICCALL   | [0x1, 0x2, 0x0, 0x4, 0x0, 0x20]      |


  Scenario Outline: RIPEMD160 precompiled smart contract can be executed correctly - <callType>
    Given the stack contains elements <stackElements>
    And 0x61 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then there is no last error
    And 32 bytes of memory from position 0 is 0x0000000000000000000000000bdc9d2d256b3ee9daae347be6f4dc835a467ffe

    Examples:
      | callType     | stackElements                        |
      | CALL         | [0x1, 0x3, 0x0, 0x0, 0x1, 0x0, 0x20] |
      | CALLCODE     | [0x1, 0x3, 0x0, 0x0, 0x1, 0x0, 0x20] |
      | DELEGATECALL | [0x1, 0x3, 0x0, 0x1, 0x0, 0x20]      |
      | STATICCALL   | [0x1, 0x3, 0x0, 0x1, 0x0, 0x20]      |


  Scenario Outline: IDENTITY precompiled smart contract can be executed correctly - <callType>
    Given the stack contains elements <stackElements>
    And 0x61ab is stored in memory at location 0x0
    When opcode <callType> is executed
    Then there is no last error
    And 2 bytes of memory from position 0 is 0x61ab

    Examples:
      | callType     | stackElements                       |
      | CALL         | [0x1, 0x4, 0x0, 0x0, 0x2, 0x0, 0x2] |
      | CALLCODE     | [0x1, 0x4, 0x0, 0x0, 0x2, 0x0, 0x2] |
      | DELEGATECALL | [0x1, 0x4, 0x0, 0x2, 0x0, 0x2]      |
      | STATICCALL   | [0x1, 0x4, 0x0, 0x2, 0x0, 0x2]      |


  Scenario Outline: EXPMOD precompiled smart contract can be executed correctly - <callType>
    Given the stack contains elements <stackElements>
    And 0x00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000002003ffff80 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then there is no last error
    And 32 bytes of memory from position 0 is 0x3b01b01ac41f2d6e917c6d6a221ce793802469026d9ab7578fa2e79e4da6aaab

    Examples:
      | callType     | stackElements                         |
      | CALL         | [0x1, 0x5, 0x0, 0x0, 0x64, 0x0, 0x32] |
      | CALLCODE     | [0x1, 0x5, 0x0, 0x0, 0x64, 0x0, 0x32] |
      | DELEGATECALL | [0x1, 0x5, 0x0, 0x64, 0x0, 0x32]      |
      | STATICCALL   | [0x1, 0x5, 0x0, 0x64, 0x0, 0x32]      |


  Scenario Outline: BNADD precompiled smart contract can be executed correctly - <callType>
    Given the stack contains elements <stackElements>
    And 0x17c139df0efee0f766bc0204762b774362e4ded88953a39ce849a8a7fa163fa901e0559bacb160664764a357af8a9fe70baa9258e0b959273ffc5718c6d4cc7c039730ea8dff1254c0fee9c0ea777d29a9c710b7e616683f194f18c43b43b869073a5ffcc6fc7a28c30723d6e58ce577356982d65b833a5a5c15bf9024b43d98 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then there is no last error
    And 64 bytes of memory from position 0 is 0x15bf2bb17880144b5d1cd2b1f46eff9d617bffd1ca57c37fb5a49bd84e53cf66049c797f9ce0d17083deb32b5e36f2ea2a212ee036598dd7624c168993d1355f

    Examples:
      | callType     | stackElements                          |
      | CALL         | [0x1, 0x6, 0x0, 0x0, 0x128, 0x0, 0x64] |
      | CALLCODE     | [0x1, 0x6, 0x0, 0x0, 0x128, 0x0, 0x64] |
      | DELEGATECALL | [0x1, 0x6, 0x0, 0x128, 0x0, 0x64]      |
      | STATICCALL   | [0x1, 0x6, 0x0, 0x128, 0x0, 0x64]      |


  Scenario Outline: BNMUL precompiled smart contract can be executed correctly - <callType>
    Given the stack contains elements <stackElements>
    And 0x1a87b0584ce92f4593d161480614f2989035225609f08058ccfa3d0f940febe31a2f3c951f6dadcc7ee9007dff81504b0fcd6d7cf59996efdc33d92bf7f9f8f60000000000000000000000000000000000000000000000000000000000000009 is stored in memory at location 0x0
    When opcode <callType> is executed
    Then there is no last error
    And 64 bytes of memory from position 0 is 0x1dbad7d39dbc56379f78fac1bca147dc8e66de1b9d183c7b167351bfe0aeab742cd757d51289cd8dbd0acf9e673ad67d0f0a89f912af47ed1be53664f5692575

    Examples:
      | callType     | stackElements                         |
      | CALL         | [0x1, 0x7, 0x0, 0x0, 0x96, 0x0, 0x64] |
      | CALLCODE     | [0x1, 0x7, 0x0, 0x0, 0x96, 0x0, 0x64] |
      | DELEGATECALL | [0x1, 0x7, 0x0, 0x96, 0x0, 0x64]      |
      | STATICCALL   | [0x1, 0x7, 0x0, 0x96, 0x0, 0x64]      |


  Scenario Outline: SNARKV precompiled smart contract can be executed correctly - <callType>
    Given the stack contains elements <stackElements>
    And 0x00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c21800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa000000000000000000000000000000000000000000000000000000000000000130644e72e131a029b85045b68181585d97816a916871ca8d3c208c16d87cfd45198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c21800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa is stored in memory at location 0x0
    When opcode <callType> is executed
    Then there is no last error
    And 32 bytes of memory from position 0 is 0x0000000000000000000000000000000000000000000000000000000000000001

    Examples:
      | callType     | stackElements                          |
      | CALL         | [0x1, 0x8, 0x0, 0x0, 0x180, 0x0, 0x32] |
      | CALLCODE     | [0x1, 0x8, 0x0, 0x0, 0x180, 0x0, 0x32] |
      | DELEGATECALL | [0x1, 0x8, 0x0, 0x180, 0x0, 0x32]      |
      | STATICCALL   | [0x1, 0x8, 0x0, 0x180, 0x0, 0x32]      |