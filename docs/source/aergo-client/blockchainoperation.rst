BlockchainOperation
===================

Provides blockchain, node related operations.

Get Chain Id Hash
-----------------

.. code-block:: java

  ChainIdHash chainIdHash = client.getBlockchainOperation().getChainIdHash();

Get Blockchain Status
---------------------

.. code-block:: java

  BlockchainStatus blockchainStatus = client.getBlockchainOperation().getBlockchainStatus();

Get Chain Info
--------------

.. code-block:: java

  ChainInfo chainInfo = client.getBlockchainOperation().getChainInfo();

Get Chain Stats
---------------

.. code-block:: java

  ChainStats chainStats = client.getBlockchainOperation().getChainStats();

Get Node Status
---------------

.. code-block:: java

  NodeStatus nodeStatus = client.getBlockchainOperation().getNodeStatus();

Get Server Info
---------------

Categories is reduntant now. Pass empty list to it.

.. code-block:: java

  List<String> categories = Collections.emptyList();
  ServerInfo serverInfo = client.getBlockchainOperation().getServerInfo(categories);

List Peers
----------

Filtering itself and hidden

.. code-block:: java

  List<Peer> hideHiddenAndSelfPeers = client.getBlockchainOperation().listPeers(false, false);

Not filtering itself and hidden

.. code-block:: java

  List<Peer> showAllPeers = client.getBlockchainOperation().listPeers(true, true);

List Peers Metrics
------------------

.. code-block:: java

  List<PeerMetric> peerMetrics = client.getBlockchainOperation().listPeerMetrics();