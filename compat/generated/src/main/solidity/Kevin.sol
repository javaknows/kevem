pragma solidity >=0.4.24;

contract Kevin  {

    function add(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := add(arg1, arg2)

            mstore(0x40, ret)
            log0(0x40, 0x20)
        }
    }
}