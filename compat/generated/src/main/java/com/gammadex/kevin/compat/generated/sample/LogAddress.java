package com.gammadex.kevin.compat.generated.sample;

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
public class LogAddress extends Contract {
    private static final String BINARY = "6080604052348015600f57600080fd5b50607180601d6000396000f3fe6080604052348015600f57600080fd5b506004361060285760003560e01c8063c040622614602d575b600080fd5b60336035565b005b30600080a156fea265627a7a723058203c79d129fbb20fba2e6d9df99e583e6f5ae5eff7fbc9b1ab4bb602eebe4df83164736f6c634300050a0032";

    public static final String FUNC_RUN = "run";

    @Deprecated
    protected LogAddress(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected LogAddress(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected LogAddress(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected LogAddress(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> run() {
        final Function function = new Function(
                FUNC_RUN, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static Function runFunction() {
        final Function function = new Function(
                FUNC_RUN, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    @Deprecated
    public static LogAddress load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new LogAddress(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static LogAddress load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new LogAddress(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static LogAddress load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new LogAddress(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static LogAddress load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new LogAddress(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<LogAddress> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(LogAddress.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<LogAddress> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LogAddress.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<LogAddress> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(LogAddress.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<LogAddress> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LogAddress.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
