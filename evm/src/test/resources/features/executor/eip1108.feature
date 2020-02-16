Feature: EIP-1108: Reduce alt_bn128 precompile gas costs

  # https://eips.ethereum.org/EIPS/eip-1108

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000


  Scenario Outline: BNADD precompiled smart contract gas is correct when <eip> is enabled
    Given the stack contains elements [0x1, 0x6, 0x0, 0x0, 0x128, 0x0, 0x64]
    And 0x17c139df0efee0f766bc0204762b774362e4ded88953a39ce849a8a7fa163fa901e0559bacb160664764a357af8a9fe70baa9258e0b959273ffc5718c6d4cc7c039730ea8dff1254c0fee9c0ea777d29a9c710b7e616683f194f18c43b43b869073a5ffcc6fc7a28c30723d6e58ce577356982d65b833a5a5c15bf9024b43d98 is stored in memory at location 0x0
    And the highest byte touched in memory is 0x128
    And EIP <eip> is enabled
    When opcode CALL is executed
    Then <gas> gas is now used

    Examples:
      | eip     | gas |
      | EIP196  | 500 |
      | EIP1108 | 150 |


  Scenario Outline: BNMUL precompiled smart contract gas is correct when <eip> is enabled
    Given the stack contains elements [0x1, 0x7, 0x0, 0x0, 0x96, 0x0, 0x64]
    And 0x1a87b0584ce92f4593d161480614f2989035225609f08058ccfa3d0f940febe31a2f3c951f6dadcc7ee9007dff81504b0fcd6d7cf59996efdc33d92bf7f9f8f60000000000000000000000000000000000000000000000000000000000000009 is stored in memory at location 0x0
    And the highest byte touched in memory is 0x96
    And EIP <eip> is enabled
    When opcode CALL is executed
    Then <gas> gas is now used

    Examples:
      | eip     | gas   |
      | EIP196  | 40000 |
      | EIP1108 | 6000  |


  Scenario Outline: SNARKV precompiled smart contract gas is correct when <eip> is enabled
    Given the stack contains elements [0x1, 0x8, 0x0, 0x0, 0x180, 0x0, 0x32]
    And 0x00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000002198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c21800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa000000000000000000000000000000000000000000000000000000000000000130644e72e131a029b85045b68181585d97816a916871ca8d3c208c16d87cfd45198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c21800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa is stored in memory at location 0x0
    And EIP <eip> is enabled
    When opcode CALL is executed
    Then <gas> gas is now used

    Examples:
      | eip     | gas    |
      | EIP197  | 260000 |
      | EIP1108 | 113000 |