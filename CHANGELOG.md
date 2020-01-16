# CHANGELOG
All notable changes to this project will be documented in this file.

The changelog format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [0.16.1] - 16-Jan-2020

**Milestone**: Fuschicho 3

- Fixed transaction status's code issue in both Http and Websocket due to schema update.
- Added transaction criteria to account repository with the ability of filter by transaction type.
- Added partial transaction queries to the account repository. 
- Fixed empty message mapping in transfer transactions.
- Implemented MosaicRestrictionTransactionService.
- Added missing 'partial' value in TransactionState.
- Other small fixes.

## [0.16.0] - 11-Dec-2019

**Milestone**: Fuschicho 3

- Added TransactionService. A service with many utility methods that simplifies how transactions are announced, validated and resolved.
- Added AggregateTransactionService. A service that allows clients to validate if an Aggregate Transactions is completed with all the necessary signatures.
- Fixed BalanceTransferReceipt serialization
- Added epochAdjustment introduced in catapult-server v0.9.1.1 (Fushicho.3)

## [0.15.0] - 25-Nov-2019

**Milestone**: Fuschicho 2

- Applied latest Fushicho2 schema changes to both transaction serialization and http client codes.
- Added MetadataTransactionService that simplifies how metadata can be set and updated.
- Added 'Mosaic Owned' endpoints.
- Other small fixes.

## [0.14.2] - 04-Nov-2019

**Milestone**: Fuschicho

- Added Address isValidAddress methods.
- Added Transaction size methods to calculate transaction fees.
- Listener status filtered by topic/address.
- NetworkRepository uses Network Routes.
- NetworkType is now present in Address Alias / Unresolved Address Catbuffer serialization.
- Other small fixes.

## [0.14.1] - 24-Oct-2019

**Milestone**: Fuschicho

- Improved Transaction serialization. Signature and signer are serialized when present.
- Upgraded Gradle to latest 5.6.3. Gradle build has been simplified.
- Removed JUnit 4 support. All the tests are now JUnit 5.
- Fixed hardcoded network type in unresolved address serialization.
- Improved release process.
- Other small fixes.

## [0.14.0] - 16-Oct-2019

**Milestone**: Fuschicho

- Applied latest OpenAPI doc (`v0.7.19`).
- Catbuffer binary Serialization and Deserialization for offline work.
- JSON Serialization and Deserialization for offline work.
- Created repository factories.
- Created new rest client repositories.
- Mosaic and address restrictions support
- Address, mosaic and namespace metadata support.
- Mosaic and address aliases support.
- Audit merkle proofs support.
- Improved integration tests.
- Replaced `UInt64DTO` for `BigInteger`.
- Open API generated libraries.
- Vertx and OkHttp rest client support.
- Improved Listeners.
- Other small fixes.


