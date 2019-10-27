pragma solidity >=0.4.24;

contract HaltFunctions  {

    function doStop() public {
        assembly {
            log0(0, 0)
            stop()
        }
    }

    function doReturn() public {
        assembly {
            let memPointer := mload(0x40)
            mstore(memPointer, 0x1)

            log0(0, 0)
            return(memPointer, 0x20)
        }
    }

    function doRevert() public {
        assembly {
            let memPointer := mload(0x40)
            mstore(memPointer, 0x1)

            log0(0, 0)
            revert(memPointer, 0x20)
        }
    }

    function doInvalid() public {
        assembly {
            log0(0, 0)
            invalid()
        }
    }

    function doSelfDestruct() public {
        assembly {
            log0(0, 0)
            selfdestruct(0x0)
            //log0(0, 0)
        }
    }
}