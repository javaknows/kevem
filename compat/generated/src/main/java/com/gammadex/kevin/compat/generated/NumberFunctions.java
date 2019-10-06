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
    private static final String BINARY = "608060405234801561001057600080fd5b506109c8806100206000396000f3fe608060405234801561001057600080fd5b50600436106101735760003560e01c806389769422116100de578063d8c8807b11610097578063e9635a1f11610071578063e9635a1f14610610578063f590bbf314610648578063fe8c327914610680578063ffe119e0146106b857610173565b8063d8c8807b14610568578063dc1ee57b146105a0578063df9da9fd146105d857610173565b8063897694221461040e5780639a07a89314610450578063a2d00d8d14610488578063b0c37966146104c0578063b894fa07146104f8578063bcdba4ba1461053057610173565b80632c00612d116101305780632c00612d146102c857806330e0cae1146102f657806338f071201461032e5780634361fb43146103665780636e443c761461039e578063895feacd146103d657610173565b8063042d5b2314610178578063053ee5a2146101b05780630c6560b3146101f257806315a831e71461022057806321020e221461025857806326b70f1214610290575b600080fd5b6101ae6004803603604081101561018e57600080fd5b8101908080359060200190929190803590602001909291905050506106f0565b005b6101f0600480360360608110156101c657600080fd5b8101908080359060200190929190803590602001909291908035906020019092919050505061070b565b005b61021e6004803603602081101561020857600080fd5b810190808035906020019092919050505061072c565b005b6102566004803603604081101561023657600080fd5b810190808035906020019092919080359060200190929190505050610741565b005b61028e6004803603604081101561026e57600080fd5b81019080803590602001909291908035906020019092919050505061075c565b005b6102c6600480360360408110156102a657600080fd5b810190808035906020019092919080359060200190929190505050610777565b005b6102f4600480360360208110156102de57600080fd5b8101908080359060200190929190505050610792565b005b61032c6004803603604081101561030c57600080fd5b8101908080359060200190929190803590602001909291905050506107a7565b005b6103646004803603604081101561034457600080fd5b8101908080359060200190929190803590602001909291905050506107c2565b005b61039c6004803603604081101561037c57600080fd5b8101908080359060200190929190803590602001909291905050506107dd565b005b6103d4600480360360408110156103b457600080fd5b8101908080359060200190929190803590602001909291905050506107f8565b005b61040c600480360360408110156103ec57600080fd5b810190808035906020019092919080359060200190929190505050610813565b005b61044e6004803603606081101561042457600080fd5b8101908080359060200190929190803590602001909291908035906020019092919050505061082e565b005b6104866004803603604081101561046657600080fd5b81019080803590602001909291908035906020019092919050505061084f565b005b6104be6004803603604081101561049e57600080fd5b81019080803590602001909291908035906020019092919050505061086a565b005b6104f6600480360360408110156104d657600080fd5b810190808035906020019092919080359060200190929190505050610885565b005b61052e6004803603604081101561050e57600080fd5b8101908080359060200190929190803590602001909291905050506108a0565b005b6105666004803603604081101561054657600080fd5b8101908080359060200190929190803590602001909291905050506108bb565b005b61059e6004803603604081101561057e57600080fd5b8101908080359060200190929190803590602001909291905050506108d6565b005b6105d6600480360360408110156105b657600080fd5b8101908080359060200190929190803590602001909291905050506108f1565b005b61060e600480360360408110156105ee57600080fd5b81019080803590602001909291908035906020019092919050505061090c565b005b6106466004803603604081101561062657600080fd5b810190808035906020019092919080359060200190929190505050610927565b005b61067e6004803603604081101561065e57600080fd5b810190808035906020019092919080359060200190929190505050610942565b005b6106b66004803603604081101561069657600080fd5b81019080803590602001909291908035906020019092919050505061095d565b005b6106ee600480360360408110156106ce57600080fd5b810190808035906020019092919080359060200190929190505050610978565b005b600435602435808207604051818152602081a0505050505050565b60043560243560443580828409604051818152602081a05050505050505050565b6004358019604051818152602081a050505050565b60043560243580821b604051818152602081a0505050505050565b600435602435808204604051818152602081a0505050505050565b600435602435808210604051818152602081a0505050505050565b6004358015604051818152602081a050505050565b600435602435808214604051818152602081a0505050505050565b600435602435808218604051818152602081a0505050505050565b600435602435808205604051818152602081a0505050505050565b600435602435808217604051818152602081a0505050505050565b600435602435808201604051818152602081a0505050505050565b60043560243560443580828408604051818152602081a05050505050505050565b60043560243580820a604051818152602081a0505050505050565b600435602435808213604051818152602081a0505050505050565b60043560243580821d604051818152602081a0505050505050565b600435602435808212604051818152602081a0505050505050565b600435602435808202604051818152602081a0505050505050565b60043560243580821a604051818152602081a0505050505050565b60043560243580821c604051818152602081a0505050505050565b600435602435808211604051818152602081a0505050505050565b600435602435808206604051818152602081a0505050505050565b600435602435808203604051818152602081a0505050505050565b600435602435808216604051818152602081a0505050505050565b60043560243580820b604051818152602081a050505050505056fea265627a7a72305820d47182079162a6c8bd5403246e2174ce14781c6f300be25a3f4caba5f5e928c864736f6c634300050a0032";

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
