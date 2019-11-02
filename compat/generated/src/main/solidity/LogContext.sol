pragma solidity >=0.4.24;

contract LogContext {

    uint callType;

    function setCallType(uint _callType) public {
        callType = _callType;
    }

    function createContextLogs() public {
        uint c = callType;

        assembly {
            let memPointer := mload(0x40)
            let memStart := memPointer

            mstore(memPointer, memPointer)
            memPointer := add(memPointer, 0x20)

            mstore(memPointer, sload(1))
            memPointer := add(memPointer, 0x20)

            mstore(memPointer, address())
            memPointer := add(memPointer, 0x20)

            mstore(memPointer, caller())
            memPointer := add(memPointer, 0x20)

            mstore(memPointer, origin())

            return(memStart, add(memStart, 0x200))
        }
    }

    // codesize always same
    // calldata always same
}