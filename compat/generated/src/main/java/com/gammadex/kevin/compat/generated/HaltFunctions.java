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
public class HaltFunctions extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5060e68061001f6000396000f3fe6080604052348015600f57600080fd5b506004361060505760003560e01c80631253f7aa146055578063624f011d14605d578063afc874d2146065578063b49b8c4e14606d578063c19166cd146075575b600080fd5b605b607d565b005b6063608d565b005b606b6093565b005b607360a3565b005b607b60a9565b005b60405160018152600080a0602081f35b600080a0005b60405160018152600080a0602081fd5b600080a0fe5b600080a06000fffea265627a7a72305820552789a6db4a6b90587e8898158cc3854fa05639356312e7f0b3ce7c7db3193264736f6c634300050a0032";

    public static final String FUNC_DORETURN = "doReturn";

    public static final String FUNC_DOSTOP = "doStop";

    public static final String FUNC_DOREVERT = "doRevert";

    public static final String FUNC_DOINVALID = "doInvalid";

    public static final String FUNC_DOSELFDESTRUCT = "doSelfDestruct";

    @Deprecated
    protected HaltFunctions(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected HaltFunctions(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected HaltFunctions(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected HaltFunctions(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> doReturn() {
        final Function function = new Function(
                FUNC_DORETURN, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doStop() {
        final Function function = new Function(
                FUNC_DOSTOP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doRevert() {
        final Function function = new Function(
                FUNC_DOREVERT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doInvalid() {
        final Function function = new Function(
                FUNC_DOINVALID, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doSelfDestruct() {
        final Function function = new Function(
                FUNC_DOSELFDESTRUCT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static Function doReturnFunction() {
        final Function function = new Function(
                FUNC_DORETURN, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function doStopFunction() {
        final Function function = new Function(
                FUNC_DOSTOP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function doRevertFunction() {
        final Function function = new Function(
                FUNC_DOREVERT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function doInvalidFunction() {
        final Function function = new Function(
                FUNC_DOINVALID, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function doSelfDestructFunction() {
        final Function function = new Function(
                FUNC_DOSELFDESTRUCT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    @Deprecated
    public static HaltFunctions load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new HaltFunctions(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static HaltFunctions load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new HaltFunctions(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static HaltFunctions load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new HaltFunctions(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static HaltFunctions load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new HaltFunctions(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<HaltFunctions> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(HaltFunctions.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<HaltFunctions> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(HaltFunctions.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<HaltFunctions> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(HaltFunctions.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<HaltFunctions> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(HaltFunctions.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
