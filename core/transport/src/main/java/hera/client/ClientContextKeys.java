/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.FailoverHandler;
import hera.Key;
import hera.api.model.HostnameAndPort;
import hera.strategy.ConnectStrategy;
import hera.strategy.InvocationStrategy;
import hera.strategy.SecurityConfigurationStrategy;

abstract class ClientContextKeys {

  /* connection */

  public static final Key<HostnameAndPort> GRPC_CONNECTION_ENDPOINT = Key
      .of("GRPC_CONNECTION_ENDPOINT", HostnameAndPort.class);

  @SuppressWarnings("rawtypes")
  public static final Key<ConnectStrategy> GRPC_CONNECTION_STRATEGY = Key
      .of("GRPC_CONNECTION_STRATEGY", ConnectStrategy.class);

  public static final Key<SecurityConfigurationStrategy> GRPC_CONNECTION_NEGOTIATION = Key
      .of("GRPC_CONNECTION_NEGOTIATION", SecurityConfigurationStrategy.class);

  @SuppressWarnings("rawtypes")
  public static final Key<GrpcClient> GRPC_CLIENT = Key.of("GRPC_CLIENT", GrpcClient.class);



  /* request */

  public static final Key<InvocationStrategy> GRPC_REQUEST_TIMEOUT = Key
      .of("GRPC_REQUEST_TIMEOUT", InvocationStrategy.class);



  /* value holders */

  public static final Key<ChainIdHashHolder> GRPC_VALUE_CHAIN_ID_HASH_HOLDER = Key
      .of("GRPC_VALUE_CHAIN_ID_HASH_HOLDER", ChainIdHashHolder.class);



  /* failover */

  public static final Key<FailoverHandler> GRPC_FAILOVER_HANDLER_CHAIN = Key
      .of("GRPC_FAILOVER_HANDLER_CHAIN", FailoverHandler.class);



  /* decorator */

  public static final Key<InvocationStrategy> GRPC_BEFORE_REQUEST = Key
      .of("GRPC_BEFORE_REQUEST", InvocationStrategy.class);

  public static final Key<InvocationStrategy> GRPC_AFTER_SUCCESS = Key
      .of("GRPC_AFTER_SUCCESS", InvocationStrategy.class);

  public static final Key<InvocationStrategy> GRPC_AFTER_FAILURE = Key
      .of("GRPC_AFTER_FAILURE", InvocationStrategy.class);

}
