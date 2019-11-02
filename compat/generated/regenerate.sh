#!/bin/bash

set -e

script_path=`dirname $0`
base_path=${script_path}

generate() {
    solc_src=$1
    solc_output=$2
    jweb3_output=$3
    package=$4

    solc --overwrite --hashes --abi --bin --bin-runtime --asm  -o ${solc_output} ${solc_src}/*.sol

    for name in `find ${solc_src} -type f -maxdepth 1 | grep -v '/lib/'  | sed -r 's|.*/([^/]+)\.sol|\1|'`; do # | grep -v '/external/'
        echo "${name}.sol"
        web3j solidity-exposed generate -o ${jweb3_output} -a ${solc_output}/${name}.abi -b ${solc_output}/${name}.bin -p ${package}
    done
}

generate ${base_path}/src/main/solidity ${base_path}/src/main/resources/com/gammadex/kevin/compat ${base_path}/src/main/java com.gammadex.kevin.compat.generated
generate ${base_path}/src/main/solidity/sample ${base_path}/src/main/resources/com/gammadex/kevin/compat/sample ${base_path}/src/main/java com.gammadex.kevin.compat.generated.sample
