pragma solidity >=0.4.24;

contract LogContext {

    uint callType;

    function setCallType(uint _callType) public {
        callType = _callType;
    }

    function createContextLogs() public {
        uint c = callType;

        assembly {
            log3(0, 0, c, 1, mload(0x200))
            log3(0, 0, c, 2, sload(1))
            log3(0, 0, c, 3, address())
            log3(0, 0, c, 4, caller())
            log3(0, 0, c, 5, callvalue())
            log3(0, 0, c, 6, calldataload(0))
            log3(0, 0, c, 7, codesize())
            log3(0, 0, c, 8, origin())
        }
    }
}