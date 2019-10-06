pragma solidity >=0.4.24;

contract AdvancedFunctions  {

    function callKeccak256(bytes32 a) public {
        assembly {
            let memPointer := mload(0x40)

            calldatacopy(memPointer, 0x4, 0x20)

            let ret := keccak256(memPointer, 0x20)

            memPointer := add(0x20, memPointer)

            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }
}