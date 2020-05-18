Query Api
=========

QueryApi provides high-level api for querying state from aergo node. It doesn't need unlocked account.

Get Account State
-----------------

Get account state.

.. code-block:: java

  // get account state
  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  AccountState state = walletApi.with(client).query()
      .getAccountState(accountAddress);
  System.out.println("Account state: " + state);

Get Name Owner
--------------

Get name owner.

At current block.

.. code-block:: java

  // get name owner
  Name name = Name.of("namenamename");
  AccountAddress nameOwner = walletApi.with(client).query().getNameOwner(name);
  System.out.println("Name owner: " + nameOwner);

At specific block.

.. code-block:: java

  // get name owner at block 10
  Name name = Name.of("namenamename");
  AccountAddress nameOwner = walletApi.with(client).query().getNameOwner(name, 10);
  System.out.println("Name owner: " + nameOwner);

Get Stake Info
--------------

Get stake info of an account.

.. code-block:: java

  // get stake info
  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  StakeInfo stakeInfo = walletApi.with(client).query().getStakeInfo(accountAddress);
  System.out.println("Stake info: " + stakeInfo);

List Elected Bps
----------------

Get elected block producers.

.. code-block:: java

  // list elected bps
  List<ElectedCandidate> candidates = walletApi.with(client).query().listElectedBps(23);
  System.out.println("Elected bps: " + candidates);

List Elected
------------

Get elected candidates for vote id.

.. code-block:: java

  // list elected for "voteBP"
  List<ElectedCandidate> candidates = walletApi.with(client).query()
      .listElected("voteBP", 23);
  System.out.println("Elected candidates: " + candidates);

Get Vote Info
-------------

Get vote info of an account.

.. code-block:: java

  // get vote info
  AccountAddress accountAddress = AccountAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  AccountTotalVote accountTotalVote = walletApi.with(client).query().getVotesOf(accountAddress);
  System.out.println("Account total vote: " + accountTotalVote);

Get Best Block Hash
-------------------

Get best block hash.

.. code-block:: java

  // get best block hash
  BlockHash blockHash = walletApi.with(client).query().getBestBlockHash();
  System.out.println("Best block hash: " + blockHash);

Get Best Block Height
---------------------

Get best block height.

.. code-block:: java

  // get best block hash
  long blockHeight = walletApi.with(client).query().getBestBlockHeight();
  System.out.println("Best block height: " + blockHeight);

Get Chain Id Hash
-----------------

Get chain id hash of blockchain.

.. code-block:: java

  // get chain id hash
  ChainIdHash chainIdHash = walletApi.with(client).query().getChainIdHash();
  System.out.println("Chain id hash: " + chainIdHash);

Get Blockchain Status
---------------------

Get blockchain status.

.. code-block:: java

  // get blockchain status
  BlockchainStatus blockchainStatus = walletApi.with(client).query().getBlockchainStatus();
  System.out.println("Blockchain status: " + blockchainStatus);

Get Chain Info
--------------

Get chain info of current node.

.. code-block:: java

  // get chain info
  ChainInfo chainInfo = walletApi.with(client).query().getChainInfo();
  System.out.println("ChainInfo: " + chainInfo);

Get Chain Stats
---------------

Get chain statistics of current node.

.. code-block:: java

  // get chain stats
  ChainStats chainStats = walletApi.with(client).query().getChainStats();
  System.out.println("ChainStats: " + chainStats);

List Peers
----------

List peers of current node.

Filtering hidden peers and itself.

.. code-block:: java

  // list peers
  List<Peer> peers = walletApi.with(client).query().listPeers();
  System.out.println("Peers: " + peers);

Not filtering hidden peers and itself.

.. code-block:: java

  // list peers
  List<Peer> peers = walletApi.with(client).query().listPeers(true, true);
  System.out.println("Peers: " + peers);

List Peer Metrics
-----------------

List peers metrics of current node.

.. code-block:: java

  // list peer metrics
  List<PeerMetric> peerMetrics = walletApi.with(client).query().listPeerMetrics();
  System.out.println("Peer metrics: " + peerMetrics);

Get Server Info
---------------

Get server info of current node. Category is not implemented yet.

.. code-block:: java

  // get server info
  List<String> categories = emptyList();
  ServerInfo serverInfo = walletApi.with(client).query().getServerInfo(categories);
  System.out.println("Server info: " + serverInfo);

Get Node Status
---------------

Get node status of current node.

.. code-block:: java

  // get node status
  NodeStatus nodeStatus = walletApi.with(client).query().getNodeStatus();
  System.out.println("Node status: " + nodeStatus);

Get Block Metadata
------------------

Get block metadata. It returns null if no corresponding one.

By hash.

.. code-block:: java

  // get block metadata
  BlockHash blockHash = BlockHash.of("DN9TvryaThbJneSpzaXp5ZsS4gE3UMzKfaXC4x8L5qR1");
  BlockMetadata blockMetadata = walletApi.with(client).query().getBlockMetadata(blockHash);
  System.out.println("Block metadata by hash: " + blockMetadata);

By height.

.. code-block:: java

  // get block metadata
  long height = 27_066_653L;
  BlockMetadata blockMetadata = walletApi.with(client).query().getBlockMetadata(height);
  System.out.println("Block metadata by height: " + blockMetadata);

List Block Metadata
-------------------

Get block metadatas. Size maximum is 1000.

By hash.

.. code-block:: java

  // block metadatas by from hash to previous 100 block
  BlockHash blockHash = BlockHash.of("DN9TvryaThbJneSpzaXp5ZsS4gE3UMzKfaXC4x8L5qR1");
  List<BlockMetadata> blockMetadatas = walletApi.with(client).query()
      .listBlockMetadatas(blockHash, 100);
  System.out.println("Block metadatas by hash: " + blockMetadatas);

By height.

.. code-block:: java

  // block metadatas by from height to previous 100 block
  long height = 27_066_653L;
  List<BlockMetadata> blockMetadatas = walletApi.with(client).query()
      .listBlockMetadatas(height, 100);
  System.out.println("Block metadatas by height: " + blockMetadatas);

Get Block
---------

Get block. It returns null if no corresponding one.

By hash.

.. code-block:: java

  // get block by hash
  BlockHash blockHash = BlockHash.of("DN9TvryaThbJneSpzaXp5ZsS4gE3UMzKfaXC4x8L5qR1");
  Block block = walletApi.with(client).query().getBlock(blockHash);
  System.out.println("Block by hash: " + block);

By height.

.. code-block:: java

  // get block by height
  long height = 27_066_653L;
  Block block = walletApi.with(client).query().getBlock(height);
  System.out.println("Block by hash: " + block);

Block Metadata Subscription
---------------------------

Subscribe new generated block metadata.

.. code-block:: java

  // make a subscription
  Subscription<BlockMetadata> metadataSubscription = walletApi.with(client).query()
      .subscribeBlockMetadata(new StreamObserver<BlockMetadata>() {
        @Override
        public void onNext(BlockMetadata value) {
          System.out.println("Next block metadata: " + value);
        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onCompleted() {
        }
      });

  // wait for a while
  Thread.sleep(2000L);

  // unsubscribe it
  metadataSubscription.unsubscribe();

Block Subscription
------------------

Subscribe new generated block.

.. code-block:: java

  // make a subscription
  Subscription<Block> subscription = walletApi.with(client).query()
      .subscribeBlock(new StreamObserver<Block>() {
        @Override
        public void onNext(Block value) {
          System.out.println("Next block: " + value);
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {
        }
      });

  // wait for a while
  Thread.sleep(2000L);

  // unsubscribe it
  subscription.unsubscribe();

Get Transaction
---------------

Get transaction info. It returns null if no corresponding one.

.. code-block:: java

  // get transaction
  TxHash txHash = TxHash.of("39vLyMqsg1mTT9mF5NbADgNB2YUiRVsT6SUkDujBZme8");
  Transaction transaction = walletApi.with(client).query().getTransaction(txHash);
  System.out.println("Transaction: " + transaction);

Get Transaction Receipt
-----------------------

Get receipt of transaction. It returns null if no corresponding one.

.. code-block:: java

  // get tx receipt
  TxHash txHash = TxHash.of("39vLyMqsg1mTT9mF5NbADgNB2YUiRVsT6SUkDujBZme8");
  TxReceipt txReceipt = walletApi.with(client).query().getTxReceipt(txHash);
  System.out.println("Transaction receipt: " + txReceipt);

Get Contract Tx Receipt
-----------------------

Get contract tx receipt. It returns null if no corresponding one.

.. code-block:: java

  // get contract tx receipt
  TxHash txHash = TxHash.of("EGXNDgjY2vQ6uuP3UF3dNXud54dF4FNVY181kaeQ26H9");
  ContractTxReceipt contractTxReceipt = walletApi.with(client).query()
      .getContractTxReceipt(txHash);
  System.out.println("Contract tx receipt: " + contractTxReceipt);

Get Contract Interface
----------------------

Get contract interface. It returns null if no corresponding one.

.. code-block:: java

  // get contract interface
  ContractAddress contractAddress = ContractAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  ContractInterface contractInterface = walletApi.with(client).query()
      .getContractInterface(contractAddress);
  System.out.println("ContractInterface: " + contractInterface);

Query Contract
--------------

Get state of contract. It can be binded to an java bean. For more about making contract invocation, see :doc:`ContractInvocation <../model/contractinvocation>`.

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
  System.out.println("Raw contract result: " + queryResult);
  System.out.println("Binded data: " + data);

List Event
----------

Get event infos at some block. For more about making event filter, see :doc:`EventFilter <../model/eventfilter>`.

.. code-block:: java

  // list events with a filter
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

Subscribe new generated event of specific contract. For more about making event filter, see :doc:`EventFilter <../model/eventfilter>`.

.. code-block:: java

  // subscribe event
  ContractAddress contractAddress = ContractAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
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

  Thread.sleep(2200L);

  // unsubscribe event
  subscription.unsubscribe();