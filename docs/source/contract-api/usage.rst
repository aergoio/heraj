Usage
=====

ContractApi provides java interface based smart contract call. To use it, you have to deploy smart contract and write corresponding interface for it. ContractApi automatically fill nonce for signer. It commit fails by nonce error, it automatically fetch right nonce and retry with it.

Make
----

Deploy a contract.

.. code-block:: java

  // make a contract definition
  String encodedContract = contractPayload;
  ContractDefinition contractDefinition = ContractDefinition.newBuilder()
      .encodedContract(encodedContract)
      .build();

  // deploy contract
  walletApi.unlock(authentication);
  TxHash txHash = walletApi.with(client).transaction()
      .deploy(contractDefinition, Fee.INFINITY);
  walletApi.lock();

  // sleep
  Thread.sleep(2000L);

  // get ContractTxReceipt
  ContractTxReceipt contractTxReceipt = walletApi.with(client).query()
      .getContractTxReceipt(txHash);

  // get contract address
  ContractAddress contractAddress = contractTxReceipt.getContractAddress();
  System.out.println("Deployed contract address: " + contractPayload);

Write an interface. Interface methods should matches with smart contract functions.

.. code-block:: java

  // interface for smart contract
  interface CustomInterface1 {

    /*
      Matches with

        function set(key, arg1, arg2)
          ...
        end

        ...

        abi.register(set)

      And it also uses provided fee when making transaction.
     */
    TxHash set(String key, int arg1, String args2, Fee fee);

    /*
      Matches with

        function set(key, arg1, arg2)
          ...
        end

        ...

        abi.register(set)

      And it also uses Fee.INFINITY when making transaction.
     */
    TxHash set(String key, int arg1, String args2);

    /*
      Matches with

        function get(key)
          ...
          -- returns lua table which can be binded with Data class
          return someVal
        end

        ...

        abi.register_view(get)
     */
    Data get(String key);

  }

  // java bean
  class Data {

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
      return "Data{" +
          "intVal=" + intVal +
          ", stringVal=" + stringVal +
          '}';
    }
  }

Make a contract api with implicit retry count and interval on nonce failure.

.. code-block:: java

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class);
  System.out.println("ContractApi: " + contractApi);

Make a contract api with explicit retry count and interval on nonce failure.

.. code-block:: java

  // create a contract api with retry count 5 and interval 1000ms
  ContractAddress contractAddress = deployedContractAddress;
  TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(5, Time.of(1000L));
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class, tryCountAndInterval);
  System.out.println("ContractApi: " + contractApi);

Execute
-------

With an aergo key.

.. code-block:: java

  // prepare an signer
  AergoKey signer = richKey;

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class);

  // execute contract with a contract api
  TxHash executeTxHash = contractApi.with(client).execution(signer)
      .set("key", 123, "test", Fee.INFINITY);
  System.out.println("Execute tx hash: " + executeTxHash);

With a wallet api.

.. code-block:: java

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class);

  // execute contract with a contract api
  walletApi.unlock(authentication);
  TxHash executeTxHash = contractApi.with(client).execution(walletApi)
      .set("key", 123, "test", Fee.INFINITY);
  walletApi.lock();
  System.out.println("Execute tx hash: " + executeTxHash);

Query
-----

With a model binded.

.. code-block:: java

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class);

  // query contract with a contract api
  Data data = contractApi.with(client).query().get("key");
  System.out.println("Queried data: " + data);

Without binded model.

.. code-block:: java

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface2> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface2.class);

  // query contract with a contract api
  ContractResult contractResult = contractApi.with(client).query().get("key");
  System.out.println("Queried data: " + contractResult);
