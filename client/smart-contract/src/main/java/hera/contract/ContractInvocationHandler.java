package hera.contract;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.Fee;
import hera.exception.ContractException;
import hera.util.StringUtils;
import hera.wallet.Wallet;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

public class ContractInvocationHandler implements InvocationHandler {

  protected final Logger logger = getLogger(getClass());

  @Getter(value = AccessLevel.PROTECTED)
  protected ContractAddress contractAddress;

  @Getter(value = AccessLevel.PROTECTED)
  protected ContractInterface contractInterface;

  @Setter
  protected Wallet wallet;

  @Getter(value = AccessLevel.PROTECTED)
  @Setter
  protected Fee fee = Fee.getDefaultFee();

  ContractInvocationHandler(final ContractAddress contractAddress) {
    if (null == contractAddress) {
      throw new ContractException("Contract address must not null");
    }
    this.contractAddress = contractAddress;
  }

  protected Wallet getWallet() {
    if (null == this.wallet) {
      throw new ContractException("No binded wallet");
    }
    return this.wallet;
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

      if (isContractConfig(method)) {
        logger.debug("Invocation is contract config: {}", method);
        invokeConfigMethod(proxy, method, args);
        return null;
      } else {
        logger.debug("Invocation is contract call: {}", method);
        return invokeUserMethod(proxy, method, args);
      }
    } catch (Exception e) {
      throw new ContractException(e);
    }
  }

  protected boolean isContractConfig(final Method method) {
    return "bind".equals(method.getName());
  }

  protected void invokeConfigMethod(final Object proxy, final Method method, final Object[] args) {
    if (args.length > 1) {
      throw new ContractException("Illegal number of arguments for method \'bind\'");
    }

    final Object arg = args[0];
    if (arg instanceof Wallet) {
      setWallet((Wallet) arg);
    } else if (arg instanceof Fee) {
      setFee((Fee) arg);
    } else {
      throw new ContractException(
          "Unable to process bind argument for type " + arg.getClass().getName());
    }
  }

  protected Object invokeUserMethod(final Object proxy, final Method method, final Object[] args)
      throws Exception {
    final Wallet wallet = getWallet();
    if (null == getContractInterface()) {
      final ContractInterface contractInterface = wallet.getContractInterface(getContractAddress());
      validateInterface(proxy, contractInterface);
      this.contractInterface = contractInterface;
    }

    final ContractInterface contractInterface = getContractInterface();
    final ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
        .function(method.getName())
        .args(args)
        .build();

    if (Void.TYPE.equals(method.getReturnType())) {
      logger.debug("Contract execution: {}", contractInvocation);
      wallet.execute(contractInvocation, getFee());
      return null;
    } else {
      logger.debug("Contract query: {}", contractInvocation);
      ContractResult result = wallet.query(contractInvocation);
      return result.bind(method.getReturnType());
    }
  }

  protected void validateInterface(final Object proxy, final ContractInterface contractInterface) {
    logger.debug("Validate {} with {}", proxy, contractInterface);
    for (final Method method : proxy.getClass().getDeclaredMethods()) {
      final String methodName = method.getName();
      // java object methods
      if ("equals".equals(methodName) || "toString".equals(methodName)
          || "hashCode".equals(methodName)) {
        continue;
      }
      // contract specific methods
      if ("bind".equals(methodName) && 1 == method.getParameterCount()) {
        final Class<?> parameterType = method.getParameterTypes()[0];
        if (Wallet.class.equals(parameterType) || Fee.class.equals(parameterType)) {
          continue;
        }
      }
      if (null == contractInterface.findFunction(methodName)) {
        throw new ContractException("No method " + methodName + " in contract interface");
      }
    }
  }
}
