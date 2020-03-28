.. kevem documentation master file, created by
   sphinx-quickstart on Fri Mar 27 19:51:43 2020.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Kevem
#####

.. image:: kevem_k_color_border_path.svg
    :width: 200px
    :alt: Kevem logo
    :align: center
    :align: center

Kevem is an Ethereum test client for testing web3j-based Kotlin/Java/JVM code.

Using Kevem you can write tests which execute `web3j <https://www.web3labs.com/web3j>`_ calls in-process rather than calling an external Ethereum node.
This means you can have a more reliable and slicker development experience without leaving your IDE.

In-Process vs External Process Testing
======================================

For JVM developers the usual pattern for Ethereum development without using Kevem is to spin up an instance of
`Ganache <https://www.trufflesuite.com/ganache>`_ or `Parity <https://www.parity.io/>`_ running in ``testclient`` mode.
User code then connects to this using web3j to interact with the Ethereum node over the
`Ethereum JSON-RPC protocol <https://github.com/ethereum/wiki/wiki/JSON-RPC>`_.

.. hint::

   If you just want to try Kevem out quickly you can run it as a :ref:`standalone application <running-standalone>` and
connect to it remotely as if you were using Ganache or Parity.

External Process Drawbacks
==========================

Testing against an external node is an valid way to test (and Kevem supports this) but there can be drawbacks to working this way:

* The test instance may not be running requiring you you to start it up while being careful to use the same startup options every time
* When you rerun a test suite the state of the Ethereum node can be different between runs
* Your test user account can run out of Ethereum
* Contract and account states can get out of sync with what you expect
* Account balances can be unpredictable across reruns leading you to have to assert on deltas rather than absolute values

These aren't insurmountable problems and techniques like snapshotting help but they can still cause pain for developers.
For certain test scenarios using an in-process Kevem instance may be a good alternative.

In-Process Kevem
================

With Kevem in-process testing you create a web3j client which points at an instance of Kevem in memory.
Communication between your code and Kevem via web3j is over normal JVM method calls rather than over JSON-RPC.

Creating An In-Process Node
***************************

Kevem supports two ways to initialise an in-process EVM node and linked web3j client:

* Using the `Kotlin DSL <kotlin-dsl>`_ - good for Kotlin users
* Using the `Java Fluent API <java-fluent-api>`_ - good for Java and other JVM users

Database Analogy
****************

An analogy for SQL developers to help think about Kevem is to consider writing a test with `H2 <http://h2database.com/html/main.html>`_
or `HSQLDB <http://hsqldb.org/>`_ rather than using `Postgres <https://www.postgresql.org/>`_ or `Oracle <https://www.oracle.com/index.html>`_.
User code doesn't care what database is being used under-the-hood or if the database is in-memory or remote because JDBC provides an
abstraction layer. In the same way when using Kevem the client code doesn't care what Ethereum node is being used because web3j
provides an abstraction layer. The only difference to client code is how you initialise web3j.

Contents
========

.. toctree::
   :maxdepth: 2

   installation.rst
   kotlin-dsl.rst
   java-fluent-api.rst
   running-standalone.rst
   missing-features.rst
   license.rst

Index and Search
================

:ref:`genindex`, :ref:`search`
