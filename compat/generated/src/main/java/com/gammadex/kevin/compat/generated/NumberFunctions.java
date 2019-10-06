package com.gammadex.kevin.compat.generated;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.3.2-SNAPSHOT.
 */
public class NumberFunctions extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610a36806100206000396000f3fe608060405234801561001057600080fd5b506004361061018e5760003560e01c806389769422116100de578063d8c8807b11610097578063e9635a1f11610071578063e9635a1f14610663578063f590bbf31461069b578063fe8c3279146106d3578063ffe119e01461070b5761018e565b8063d8c8807b146105bb578063dc1ee57b146105f3578063df9da9fd1461062b5761018e565b806389769422146104615780639a07a893146104a3578063a2d00d8d146104db578063b0c3796614610513578063b894fa071461054b578063bcdba4ba146105835761018e565b80632c00612d1161014b5780634361fb43116101255780634361fb431461038157806350ace6a9146103b95780636e443c76146103f1578063895feacd146104295761018e565b80632c00612d146102e357806330e0cae11461031157806338f07120146103495761018e565b8063042d5b2314610193578063053ee5a2146101cb5780630c6560b31461020d57806315a831e71461023b57806321020e221461027357806326b70f12146102ab575b600080fd5b6101c9600480360360408110156101a957600080fd5b810190808035906020019092919080359060200190929190505050610743565b005b61020b600480360360608110156101e157600080fd5b8101908080359060200190929190803590602001909291908035906020019092919050505061075e565b005b6102396004803603602081101561022357600080fd5b810190808035906020019092919050505061077f565b005b6102716004803603604081101561025157600080fd5b810190808035906020019092919080359060200190929190505050610794565b005b6102a96004803603604081101561028957600080fd5b8101908080359060200190929190803590602001909291905050506107af565b005b6102e1600480360360408110156102c157600080fd5b8101908080359060200190929190803590602001909291905050506107ca565b005b61030f600480360360208110156102f957600080fd5b81019080803590602001909291905050506107e5565b005b6103476004803603604081101561032757600080fd5b8101908080359060200190929190803590602001909291905050506107fa565b005b61037f6004803603604081101561035f57600080fd5b810190808035906020019092919080359060200190929190505050610815565b005b6103b76004803603604081101561039757600080fd5b810190808035906020019092919080359060200190929190505050610830565b005b6103ef600480360360408110156103cf57600080fd5b81019080803590602001909291908035906020019092919050505061084b565b005b6104276004803603604081101561040757600080fd5b810190808035906020019092919080359060200190929190505050610866565b005b61045f6004803603604081101561043f57600080fd5b810190808035906020019092919080359060200190929190505050610881565b005b6104a16004803603606081101561047757600080fd5b8101908080359060200190929190803590602001909291908035906020019092919050505061089c565b005b6104d9600480360360408110156104b957600080fd5b8101908080359060200190929190803590602001909291905050506108bd565b005b610511600480360360408110156104f157600080fd5b8101908080359060200190929190803590602001909291905050506108d8565b005b6105496004803603604081101561052957600080fd5b8101908080359060200190929190803590602001909291905050506108f3565b005b6105816004803603604081101561056157600080fd5b81019080803590602001909291908035906020019092919050505061090e565b005b6105b96004803603604081101561059957600080fd5b810190808035906020019092919080359060200190929190505050610929565b005b6105f1600480360360408110156105d157600080fd5b810190808035906020019092919080359060200190929190505050610944565b005b6106296004803603604081101561060957600080fd5b81019080803590602001909291908035906020019092919050505061095f565b005b6106616004803603604081101561064157600080fd5b81019080803590602001909291908035906020019092919050505061097a565b005b6106996004803603604081101561067957600080fd5b810190808035906020019092919080359060200190929190505050610995565b005b6106d1600480360360408110156106b157600080fd5b8101908080359060200190929190803590602001909291905050506109b0565b005b610709600480360360408110156106e957600080fd5b8101908080359060200190929190803590602001909291905050506109cb565b005b6107416004803603604081101561072157600080fd5b8101908080359060200190929190803590602001909291905050506109e6565b005b600435602435808207604051818152602081a0505050505050565b60043560243560443580828409604051818152602081a05050505050505050565b6004358019604051818152602081a050505050565b60043560243580821b604051818152602081a0505050505050565b600435602435808204604051818152602081a0505050505050565b600435602435808210604051818152602081a0505050505050565b6004358015604051818152602081a050505050565b600435602435808214604051818152602081a0505050505050565b600435602435808218604051818152602081a0505050505050565b600435602435808205604051818152602081a0505050505050565b600435602435808220604051818152602081a0505050505050565b600435602435808217604051818152602081a0505050505050565b600435602435808201604051818152602081a0505050505050565b60043560243560443580828408604051818152602081a05050505050505050565b60043560243580820a604051818152602081a0505050505050565b600435602435808213604051818152602081a0505050505050565b60043560243580821d604051818152602081a0505050505050565b600435602435808212604051818152602081a0505050505050565b600435602435808202604051818152602081a0505050505050565b60043560243580821a604051818152602081a0505050505050565b60043560243580821c604051818152602081a0505050505050565b600435602435808211604051818152602081a0505050505050565b600435602435808206604051818152602081a0505050505050565b600435602435808203604051818152602081a0505050505050565b600435602435808216604051818152602081a0505050505050565b60043560243580820b604051818152602081a050505050505056fea265627a7a723058207511c5215a6b8146c4ea1842fe54d809ffe10a2b7d1cdb494639387eee6b486864736f6c634300050a0032";

    public static final String FUNC_CALLSMOD = "callSmod";

    public static final String FUNC_CALLMULMOD = "callMulmod";

    public static final String FUNC_CALLNOT = "callNot";

    public static final String FUNC_CALLSHL = "callShl";

    public static final String FUNC_CALLDIV = "callDiv";

    public static final String FUNC_CALLLT = "callLt";

    public static final String FUNC_CALLISZERO = "callIszero";

    public static final String FUNC_CALLEQ = "callEq";

    public static final String FUNC_CALLXOR = "callXor";

    public static final String FUNC_CALLSDIV = "callSdiv";

    public static final String FUNC_CALLSHA3 = "callSha3";

    public static final String FUNC_CALLOR = "callOr";

    public static final String FUNC_CALLADD = "callAdd";

    public static final String FUNC_CALLADDMOD = "callAddmod";

    public static final String FUNC_CALLEXP = "callExp";

    public static final String FUNC_CALLSGT = "callSgt";

    public static final String FUNC_CALLSAR = "callSar";

    public static final String FUNC_CALLSLT = "callSlt";

    public static final String FUNC_CALLMUL = "callMul";

    public static final String FUNC_CALLBYTE = "callByte";

    public static final String FUNC_CALLSHR = "callShr";

    public static final String FUNC_CALLGT = "callGt";

    public static final String FUNC_CALLMOD = "callMod";

    public static final String FUNC_CALLSUB = "callSub";

    public static final String FUNC_CALLAND = "callAnd";

    public static final String FUNC_CALLSIGNEXTEND = "callSignextend";

    @Deprecated
    protected NumberFunctions(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NumberFunctions(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected NumberFunctions(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected NumberFunctions(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> callSmod(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callMulmod(byte[] a, byte[] b, byte[] c) {
        final Function function = new Function(
                FUNC_CALLMULMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b), 
                new org.web3j.abi.datatypes.generated.Bytes32(c)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callNot(byte[] a) {
        final Function function = new Function(
                FUNC_CALLNOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callShl(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSHL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callDiv(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLDIV, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callLt(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLLT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callIszero(byte[] a) {
        final Function function = new Function(
                FUNC_CALLISZERO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callEq(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLEQ, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callXor(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLXOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callSdiv(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSDIV, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callSha3(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSHA3, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callOr(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callAdd(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLADD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callAddmod(byte[] a, byte[] b, byte[] c) {
        final Function function = new Function(
                FUNC_CALLADDMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b), 
                new org.web3j.abi.datatypes.generated.Bytes32(c)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callExp(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLEXP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callSgt(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSGT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callSar(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSAR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callSlt(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSLT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callMul(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLMUL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callByte(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLBYTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callShr(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSHR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callGt(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLGT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callMod(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callSub(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSUB, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callAnd(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLAND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callSignextend(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSIGNEXTEND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static Function callSmodFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callMulmodFunction(byte[] a, byte[] b, byte[] c) {
        final Function function = new Function(
                FUNC_CALLMULMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b), 
                new org.web3j.abi.datatypes.generated.Bytes32(c)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callNotFunction(byte[] a) {
        final Function function = new Function(
                FUNC_CALLNOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callShlFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSHL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callDivFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLDIV, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callLtFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLLT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callIszeroFunction(byte[] a) {
        final Function function = new Function(
                FUNC_CALLISZERO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callEqFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLEQ, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callXorFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLXOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callSdivFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSDIV, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callSha3Function(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSHA3, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callOrFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callAddFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLADD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callAddmodFunction(byte[] a, byte[] b, byte[] c) {
        final Function function = new Function(
                FUNC_CALLADDMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b), 
                new org.web3j.abi.datatypes.generated.Bytes32(c)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callExpFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLEXP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callSgtFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSGT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callSarFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSAR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callSltFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSLT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callMulFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLMUL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callByteFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLBYTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callShrFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSHR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callGtFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLGT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callModFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callSubFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSUB, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callAndFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLAND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callSignextendFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLSIGNEXTEND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    @Deprecated
    public static NumberFunctions load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NumberFunctions(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static NumberFunctions load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NumberFunctions(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static NumberFunctions load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new NumberFunctions(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static NumberFunctions load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new NumberFunctions(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<NumberFunctions> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NumberFunctions.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NumberFunctions> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NumberFunctions.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<NumberFunctions> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NumberFunctions.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NumberFunctions> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NumberFunctions.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
