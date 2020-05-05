ContractOperation
=================

Provides contract related operations.

Deployment
----------

`Aergo Smart Contract <https://docs.aergo.io/en/latest/smart-contracts/index.html>`_

.. code-block:: java

  AergoKey key = new AergoKeyGenerator().create();
    // without args
  ContractDefinition withoutArgs = ContractDefinition.newBuilder()
      .encodedContract(
          "4coHBeUgiMBpiGmZ9McPhAXWt7Ymk8WoTipWvvHrMytjtLqVqwvKo2VqGjFYHLGT2vpvYCcB1AmMLGCbf9BPVE8KSfEqXTvc2TunE2Pp2ZjtErng1odLHttTrg2LBtT5CqWrLwwVEv8Wi8BQjvenrEvrVw3apJoJjGJFWP1eNX9hU7ahAtkMCYXDp3EaD3Vr6tpXwD61N3tvG8ejqQtUnkzNaHGauNUU8PrzXBXsa4a2WUrWAFzxV1e6mwk2tL8aZtzvBGm2LGrcXVaADyomk9a58FNG6YVKwLHwcttnxKi6CWRRe5ueCBZuAxxR1DQWRzCzUs4oEZY7MpEMmMdqgN5QLw4dWQDQfj36a94NpLN9DcbeXesQ8fmFVXZBJzRoZLuQg5DQGNsu5upKAq6fndvpJ3puurDkztYKawToTtDqBjbbAkwUd3FcSHjogeiY6odoxFQvVJcLjpbBwMVboSVsnn3p1XYdUFvZ3KpjJzLd8D3JbozGSoqED1X8Re7NsGJS5JZiYdrzSyPCHjkEd1SN1ssvniFVJBJVZkojTQ1PMgT6P5ZakQC2SEw42t52BdPujr4iVsgkqGKbry3ouXef8wA8c7tJGazqa7dZ2RBKY3Bxerz4LFJsVupF6SKpXnDjJwedHiuhgj1EvZ162SqkUrakk53JUwtdL5QPFG67nLar9ZHro41xc2k3MvH1rouDYpMnRbrHxz2ZkRvyQ75CxryKm1EyT2WEC51L41R8YA8DMhPMVpSUwcYr6zVLvEmvoXL2AGz1QZNyUdzRLeQ9sYqzkkSMkXaMLG3wuLP2nGZjVbKN8f4fssRLA8x2K5jkZW4TTHuZwM4j5bfuRnNp2XH9xUiK8wfiH7W8xr6j")
      .build();
  ContractTxHash withoutArgsDeployTxHash = client.getContractOperation()
      .deploy(key, withoutArgs, 1L, Fee.ZERO);

  // with args
  ContractDefinition withArgs = ContractDefinition.newBuilder()
      .encodedContract(
          "4coHBeUgiMBpiGmZ9McPhAXWt7Ymk8WoTipWvvHrMytjtLqVqwvKo2VqGjFYHLGT2vpvYCcB1AmMLGCbf9BPVE8KSfEqXTvc2TunE2Pp2ZjtErng1odLHttTrg2LBtT5CqWrLwwVEv8Wi8BQjvenrEvrVw3apJoJjGJFWP1eNX9hU7ahAtkMCYXDp3EaD3Vr6tpXwD61N3tvG8ejqQtUnkzNaHGauNUU8PrzXBXsa4a2WUrWAFzxV1e6mwk2tL8aZtzvBGm2LGrcXVaADyomk9a58FNG6YVKwLHwcttnxKi6CWRRe5ueCBZuAxxR1DQWRzCzUs4oEZY7MpEMmMdqgN5QLw4dWQDQfj36a94NpLN9DcbeXesQ8fmFVXZBJzRoZLuQg5DQGNsu5upKAq6fndvpJ3puurDkztYKawToTtDqBjbbAkwUd3FcSHjogeiY6odoxFQvVJcLjpbBwMVboSVsnn3p1XYdUFvZ3KpjJzLd8D3JbozGSoqED1X8Re7NsGJS5JZiYdrzSyPCHjkEd1SN1ssvniFVJBJVZkojTQ1PMgT6P5ZakQC2SEw42t52BdPujr4iVsgkqGKbry3ouXef8wA8c7tJGazqa7dZ2RBKY3Bxerz4LFJsVupF6SKpXnDjJwedHiuhgj1EvZ162SqkUrakk53JUwtdL5QPFG67nLar9ZHro41xc2k3MvH1rouDYpMnRbrHxz2ZkRvyQ75CxryKm1EyT2WEC51L41R8YA8DMhPMVpSUwcYr6zVLvEmvoXL2AGz1QZNyUdzRLeQ9sYqzkkSMkXaMLG3wuLP2nGZjVbKN8f4fssRLA8x2K5jkZW4TTHuZwM4j5bfuRnNp2XH9xUiK8wfiH7W8xr6j")
      .constructorArgs("key", 123, "test")
      .build();
  ContractTxHash withArgsDeployTxHash = client.getContractOperation()
      .deploy(key, withArgs, 2L, Fee.ZERO);

  // with args and amount
  ContractDefinition withArgsAndAmount = ContractDefinition.newBuilder()
      .encodedContract(
          "4coHBeUgiMBpiGmZ9McPhAXWt7Ymk8WoTipWvvHrMytjtLqVqwvKo2VqGjFYHLGT2vpvYCcB1AmMLGCbf9BPVE8KSfEqXTvc2TunE2Pp2ZjtErng1odLHttTrg2LBtT5CqWrLwwVEv8Wi8BQjvenrEvrVw3apJoJjGJFWP1eNX9hU7ahAtkMCYXDp3EaD3Vr6tpXwD61N3tvG8ejqQtUnkzNaHGauNUU8PrzXBXsa4a2WUrWAFzxV1e6mwk2tL8aZtzvBGm2LGrcXVaADyomk9a58FNG6YVKwLHwcttnxKi6CWRRe5ueCBZuAxxR1DQWRzCzUs4oEZY7MpEMmMdqgN5QLw4dWQDQfj36a94NpLN9DcbeXesQ8fmFVXZBJzRoZLuQg5DQGNsu5upKAq6fndvpJ3puurDkztYKawToTtDqBjbbAkwUd3FcSHjogeiY6odoxFQvVJcLjpbBwMVboSVsnn3p1XYdUFvZ3KpjJzLd8D3JbozGSoqED1X8Re7NsGJS5JZiYdrzSyPCHjkEd1SN1ssvniFVJBJVZkojTQ1PMgT6P5ZakQC2SEw42t52BdPujr4iVsgkqGKbry3ouXef8wA8c7tJGazqa7dZ2RBKY3Bxerz4LFJsVupF6SKpXnDjJwedHiuhgj1EvZ162SqkUrakk53JUwtdL5QPFG67nLar9ZHro41xc2k3MvH1rouDYpMnRbrHxz2ZkRvyQ75CxryKm1EyT2WEC51L41R8YA8DMhPMVpSUwcYr6zVLvEmvoXL2AGz1QZNyUdzRLeQ9sYqzkkSMkXaMLG3wuLP2nGZjVbKN8f4fssRLA8x2K5jkZW4TTHuZwM4j5bfuRnNp2XH9xUiK8wfiH7W8xr6j")
      .constructorArgs("key", 123, "test")
      .amount(Aer.AERGO_ONE)
      .build();
  ContractTxHash withArgsAndAmountDeployTxHash = client.getContractOperation()
      .deploy(key, withArgs, 3L, Fee.ZERO);

Get Tx Receipt
--------------

.. code-block:: java

  ContractTxHash txHash = ContractTxHash.of("AVT74R9MVNMDRm9Mg9ZnA8gzxAzeeyAd3bFcGnDK978L");
  ContractTxReceipt receipt = client.getContractOperation()
      .getReceipt(txHash);

Get Contract Interface
----------------------

.. code-block:: java

  ContractAddress contractAddress = ContractAddress.of("AmgVbUZiReUVFXdYb4UVMru4ZqyicSsFPqumRx8LfwMKLFk66SNw");
  ContractInterface contractInterface = client.getContractOperation()
      .getContractInterface(contractAddress);

Execute
-------

.. code-block:: java

  ContractInterface contractInterface = ...;
  AergoKey key = new AergoKeyGenerator().create();
  ContractInvocation execution = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", 333, "test2")
      .build();
  ContractTxHash executeTxHash = client.getContractOperation()
      .execute(key, execution, 4L, Fee.ZERO);

Query
-----

.. code-block:: java

  ContractInterface contractInterface = ...;
  ContractInvocation query = contractInterface.newInvocationBuilder()
      .function("get")
      .args("key")
      .build();
  ContractResult queryResult = client.getContractOperation().query(query);
  // bind raw result to data class
  Data data = queryResult.bind(Data.class);

.. code-block:: java

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

List Event
----------

.. code-block:: java

  // by block number
  EventFilter blockNumberFilter = EventFilter.newBuilder(contractAddress)
      .fromBlockNumber(1L)
      .toBlockNumber(10L)
      .build();
  List<Event> eventsByBlockNumber = client.getContractOperation()
      .listEvents(blockNumberFilter);

  // by name in recent 1000 block
  EventFilter nameFilter = EventFilter.newBuilder(contractAddress)
      .eventName("set")
      .recentBlockCount(1000)
      .build();
  List<Event> eventsByName = client.getContractOperation().listEvents(nameFilter);

  // by name and args in recent 1000 block
  EventFilter nameAndArgsFilter = EventFilter.newBuilder(contractAddress)
      .eventName("set")
      .args("key")
      .recentBlockCount(1000)
      .build();
  List<Event> eventsByNameAndArgs = client.getContractOperation().listEvents(nameFilter);

Subscribe Event
---------------

.. code-block:: java

  // subscribe event
  EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
      .recentBlockCount(1000)
      .build();
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
  ContractInvocation run = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", 333, "test2")
      .build();
  client.getContractOperation().execute(key, run, 5L, Fee.ZERO);
  Thread.sleep(2200L);

  // unsubscribe event
  subscription.unsubscribe();

Re-Deployment (private mode only)
---------------------------------

.. code-block:: java

  AergoKey key = new AergoKeyGenerator().create();
  ContractAddress alreadyDeployed = ContractAddress.of("AmgVbUZiReUVFXdYb4UVMru4ZqyicSsFPqumRx8LfwMKLFk66SNw");
  ContractDefinition newDefinition = ContractDefinition.newBuilder()
      .encodedContract(
          "4coHBeUgiMBpiGmZ9McPhAXWt7Ymk8WoTipWvvHrMytjtLqVqwvKo2VqGjFYHLGT2vpvYCcB1AmMLGCbf9BPVE8KSfEqXTvc2TunE2Pp2ZjtErng1odLHttTrg2LBtT5CqWrLwwVEv8Wi8BQjvenrEvrVw3apJoJjGJFWP1eNX9hU7ahAtkMCYXDp3EaD3Vr6tpXwD61N3tvG8ejqQtUnkzNaHGauNUU8PrzXBXsa4a2WUrWAFzxV1e6mwk2tL8aZtzvBGm2LGrcXVaADyomk9a58FNG6YVKwLHwcttnxKi6CWRRe5ueCBZuAxxR1DQWRzCzUs4oEZY7MpEMmMdqgN5QLw4dWQDQfj36a94NpLN9DcbeXesQ8fmFVXZBJzRoZLuQg5DQGNsu5upKAq6fndvpJ3puurDkztYKawToTtDqBjbbAkwUd3FcSHjogeiY6odoxFQvVJcLjpbBwMVboSVsnn3p1XYdUFvZ3KpjJzLd8D3JbozGSoqED1X8Re7NsGJS5JZiYdrzSyPCHjkEd1SN1ssvniFVJBJVZkojTQ1PMgT6P5ZakQC2SEw42t52BdPujr4iVsgkqGKbry3ouXef8wA8c7tJGazqa7dZ2RBKY3Bxerz4LFJsVupF6SKpXnDjJwedHiuhgj1EvZ162SqkUrakk53JUwtdL5QPFG67nLar9ZHro41xc2k3MvH1rouDYpMnRbrHxz2ZkRvyQ75CxryKm1EyT2WEC51L41R8YA8DMhPMVpSUwcYr6zVLvEmvoXL2AGz1QZNyUdzRLeQ9sYqzkkSMkXaMLG3wuLP2nGZjVbKN8f4fssRLA8x2K5jkZW4TTHuZwM4j5bfuRnNp2XH9xUiK8wfiH7W8xr6j")
      .build();
  ContractTxHash redeployTxHash = client.getContractOperation()
      .redeploy(key, alreadyDeployed, newDefinition, 6L, Fee.ZERO);

