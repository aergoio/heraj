package hera.contract;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.Fee;
import hera.exception.ContractException;
import hera.util.StringUtils;
import hera.wallet.WalletApi;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;

class ContractInvocationHandler implements InvocationHandler {

  protected final transient Logger logger = getLogger(getClass());

  protected final ContractInvocator contractInvocator = new ContractInvocator();

  ContractInvocationHandler(final ContractAddress contractAddress) {
    assertNotNull(contractAddress);
    this.contractInvocator.setContractAddress(contractAddress);
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args)
      throws Throwable {
    try {
      logger.debug("Method: {}", method);
      if (!method.getDeclaringClass().isInterface()) {
        return method.invoke(this, args);
      }

      logger.debug("Proxy: {}, Method: {}, Args: {}, Ret: {}", proxy, method,
          (args == null) ? null : StringUtils.join(args, ","));

      if (isPrepareMethod(method)) {
        logger.debug("Contract Invocation prepare: {}", method);
        method.invoke(this.contractInvocator, args);
        return null;
      } else {
        logger.debug("Contract Invocation with  invocator: {}", this.contractInvocator);
        return this.contractInvocator.invoke(method, args);
      }
    } catch (ContractException e) {
      throw e;
    } catch (Exception e) {
      throw new ContractException(e);
    }
  }

  protected boolean isPrepareMethod(final Method method) {
    final Method[] methods = ContractInvocationPreparable.class.getDeclaredMethods();
    for (final Method prepareMethod : methods) {
      if (prepareMethod.getName().equals(method.getName())) {
        return true;
      }
    }
    return false;
  }

  private class ContractInvocator implements ContractInvocationPreparable {

    protected final transient Logger logger = getLogger(getClass());

    protected ContractAddress contractAddress;

    @NonNull
    @Setter
    protected WalletApi walletApi;

    @NonNull
    @Setter
    protected Fee fee;

    protected volatile ContractInterface cachedContractInterface;

    public Object invoke(final Method method, final Object[] args) throws Throwable {
      final WalletApi walletApi = getWalletApi();

      final ContractInterface contractInterface = getContractInterface();
      final ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
          .function(method.getName())
          .args(args)
          .build();

      Object ret = null;
      if (Void.TYPE.equals(method.getReturnType())) {
        if (contractInvocation.getFunction().isView()) {
          throw new ContractException(
              "Unable to execute with function registered with abi.register_view()");
        }

        logger.debug("Contract execution: {}", contractInvocation);
        final Fee fee = getFee();
        walletApi.transactionApi().execute(contractInvocation, fee);
      } else {
        if (!contractInvocation.getFunction().isView()) {
          throw new ContractException(
              "Unable to query with function registered with abi.register()");
        }

        logger.debug("Contract query: {}", contractInvocation);
        final ContractResult result = walletApi.queryApi().query(contractInvocation);
        ret = result.bind(method.getReturnType());
      }

      return ret;
    }

    protected void setContractAddress(final ContractAddress contractAddress) {
      assertNotNull(contractAddress);
      // flush cached contract address when new contract address is set
      this.cachedContractInterface = null;
      this.contractAddress = contractAddress;
    }

    protected ContractAddress getContractAddress() {
      assertNotNull(this.contractAddress);
      return this.contractAddress;
    }

    protected WalletApi getWalletApi() {
      assertNotNull(this.walletApi);
      return this.walletApi;
    }

    protected Fee getFee() {
      assertNotNull(this.fee);
      return this.fee;
    }

    protected ContractInterface getContractInterface() {
      if (null == this.cachedContractInterface) {
        final ContractAddress contractAddress = getContractAddress();
        final WalletApi walletApi = getWalletApi();
        this.cachedContractInterface = walletApi.queryApi().getContractInterface(contractAddress);
      }
      return this.cachedContractInterface;
    }

  }

}
