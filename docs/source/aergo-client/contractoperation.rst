ContractOperation
=================

Provides contract related operations. For more about writing smart contract, see `Aergo Smart Contract <https://docs.aergo.io/en/latest/smart-contracts/index.html>`_.

Deploy
------

Deploy smart contract. Normally, deployment process is

deploy -> wait for confirm -> get contract tx receipt -> find a contract address -> get contract interface

For more about making contract definition, see :doc:`ContractDefinition <../model/contract-definition>`.

.. code-block:: java

  AergoKey signer = richKey;

  // made by aergoluac --payload {some_contract}.lua
  String encodedContract = contractPayload;

  // make a contract definition
  ContractDefinition contractDefinition = ContractDefinition.newBuilder()
      .encodedContract(encodedContract)
      .build();

  // deploy
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getContractOperation().deployTx(signer, contractDefinition,
      nonce, Fee.ZERO);
  System.out.println("Contract deployment tx hash: " + txHash);

  // wait deploy contract to be confirmed
  Thread.sleep(2200L);

  // get contract tx receipt
  ContractTxReceipt contractTxReceipt = client.getContractOperation()
      .getContractTxReceipt(txHash);
  System.out.println("Contract tx receipt: " + contractTxReceipt);

  // find a contract address
  ContractAddress contractAddress = contractTxReceipt.getContractAddress();

  // get contract interface
  ContractInterface contractInterface = client.getContractOperation()
      .getContractInterface(contractAddress);
  System.out.println("Contract interface: " + contractInterface);

Re-Deploy
---------

Re-deploy to an already deployed one. It replaces contract logic while keeping contract state. This operations is available private mode only. For more about making contract definition, see :doc:`ContractDefinition <../model/contract-definition>`.

.. code-block:: java

  // prepare signer
  AergoKey signer = richKey;

  // made by aergoluac --payload {some_contract}.lua
  String encodedContract = contractPayload;

  // make a contract definition
  ContractDefinition newDefinition = ContractDefinition.newBuilder()
      .encodedContract(encodedContract)
      .build();

  // redeploy
  ContractAddress contractAddress = contractAddressKeep;
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getContractOperation()
      .redeployTx(signer, contractAddress, newDefinition, nonce, Fee.ZERO);
  System.out.println("Redeploy tx hash: " + txHash);


Get Contract Tx Receipt
-----------------------

Get contract tx receipt. It returns null if no corresponding one.

.. code-block:: java

  TxHash txHash = TxHash.of("EGXNDgjY2vQ6uuP3UF3dNXud54dF4FNVY181kaeQ26H9");
  ContractTxReceipt contractTxReceipt = client.getContractOperation()
      .getContractTxReceipt(txHash);
  System.out.println("ContractTxReceipt: " + contractTxReceipt);

Get Contract Interface
----------------------

Get contract interface. It returns null if no corresponding one.

.. code-block:: java

  ContractAddress contractAddress = ContractAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  ContractInterface contractInterface = client.getContractOperation()
      .getContractInterface(contractAddress);
  System.out.println("ContractInterface: " + contractInterface);

Execute
-------

Execute contract function of already deployed one. For more about making contract invocation, see :doc:`ContractInvocation <../model/contract-invocation>`.

.. code-block:: java

  // prepare signer
  AergoKey signer = richKey;

  // make a contract invocation
  ContractInterface contractInterface = contractInterfaceKeep;
  ContractInvocation invocation = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", 333, "test2")
      .build();

  // execute
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  TxHash txHash = client.getContractOperation()
      .executeTx(signer, invocation, nonce, Fee.ZERO);
  System.out.println("Execute tx hash: " + txHash);

Query
-----

Get state of contract. It can be binded to an java bean. For more about making contract invocation, see :doc:`ContractInvocation <../model/contract-invocation>`.

.. code-block:: java

  // java bean
  public class Data {

    protected int intVal;

    protected String stringVal;

    public int getIntVal() {
      return intVal;
    }

    public void setIntVal(int intVal) {
      this.intVal = intVal;
    }

    public String getStringVal() {
      return stringVal;
    }

    public void setStringVal(String stringVal) {
      this.stringVal = stringVal;
    }

    @Override
    public String toString() {
      return "Data [intVal=" + intVal + ", stringVal=" + stringVal + "]";
    }

  }

.. code-block:: java

  // make a contract invocation
  ContractInterface contractInterface = contractInterfaceKeep;
  ContractInvocation query = contractInterface.newInvocationBuilder()
      .function("get")
      .args("key")
      .build();

  // query contract
  ContractResult queryResult = client.getContractOperation().query(query);
  Data data = queryResult.bind(Data.class);
  System.out.println("Raw contract result: " + queryResult); // { "intVal": 123, "stringVal": "test" }
  System.out.println("Binded data: " + data);

List Event
----------

Get event infos at some block. For more about making event filter, see :doc:`EventFilter <../model/event-filter>`.

.. code-block:: java

  ContractAddress contractAddress = contractAddressKeep;
  EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
      .eventName("set")
      .args("key")
      .recentBlockCount(1000)
      .build();
  List<Event> events = client.getContractOperation().listEvents(eventFilter);
  System.out.println("Events: " + events);

Event Subscription
------------------

Subscribe new generated event of specific contract. For more about making event filter, see :doc:`EventFilter <../model/event-filter>`.

.. code-block:: java

  // prepare signer
  AergoKey signer = richKey;

  // subscribe event
  ContractAddress contractAddress = contractAddressKeep;
  EventFilter eventFilter = EventFilter.newBuilder(contractAddress).build();
  Subscription<Event> subscription = client.getContractOperation()
      .subscribeEvent(eventFilter, new StreamObserver<Event>() {
        @Override
        public void onNext(Event value) {
          System.out.println("Next event: " + value);
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {
        }
      });

  // execute
  ContractInterface contractInterface = contractInterfaceKeep;
  ContractInvocation run = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", 333, "test2")
      .build();
  long nonce = nonceProvider.incrementAndGetNonce(signer.getAddress());
  client.getContractOperation().executeTx(signer, run, nonce, Fee.ZERO);
  Thread.sleep(2200L);

  // unsubscribe event
  subscription.unsubscribe();
