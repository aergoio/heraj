BlockchainOperation
===================

Provides blockchain, node related operations.

Get Chain Id Hash
-----------------

Get chain id hash of current node.

.. code-block:: java

  ChainIdHash chainIdHash = client.getBlockchainOperation().getChainIdHash();
  System.out.println("Chain id hash: " + chainIdHash);

Get Blockchain Status
---------------------

Get blockchain status of current node.

.. code-block:: java

  BlockchainStatus blockchainStatus = client.getBlockchainOperation().getBlockchainStatus();
  System.out.println("Blockchain status: " + blockchainStatus);

Get Chain Info
--------------

Get chain info of current node.

.. code-block:: java

  ChainInfo chainInfo = client.getBlockchainOperation().getChainInfo();
  System.out.println("Chain info: " + chainInfo);

Get Chain Stats
---------------

Get chain statistics of current node.

.. code-block:: java

  ChainStats chainStats = client.getBlockchainOperation().getChainStats();
  System.out.println("Chain stats: " + chainStats);

Get Node Status
---------------

Get node status of current node.

.. code-block:: java

  NodeStatus nodeStatus = client.getBlockchainOperation().getNodeStatus();
  System.out.println("Node status: " + nodeStatus);

Get Server Info
---------------

Get server info of current node. Category is not implemented yet.

.. code-block:: java

  List<String> categories = emptyList();
  ServerInfo serverInfo = client.getBlockchainOperation().getServerInfo(categories);
  System.out.println("Server info: " + serverInfo);

List Peers
----------

List peers of current node.

Filtering itself and hidden.

.. code-block:: java

  List<Peer> peers = client.getBlockchainOperation().listPeers(false, false);
  System.out.println("Peers: " + peers);

Not filtering itself and hidden.

.. code-block:: java

  List<Peer> peers = client.getBlockchainOperation().listPeers(true, true);
  System.out.println("Peers: " + peers);

List Peers Metrics
------------------

List peers metrics of current node.

.. code-block:: java

  List<PeerMetric> peerMetrics = client.getBlockchainOperation().listPeerMetrics();
  System.out.println("PeerMetrics: " + peerMetrics);
