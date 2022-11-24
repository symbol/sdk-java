import "mosaic/mosaic_types.cats"
import "transaction.cats"

# binary layout for a mosaic supply revocation transaction
struct MosaicSupplyRevocationTransactionBody
	# address from which tokens should be revoked
	sourceAddress = UnresolvedAddress

	# revoked mosaic and amount
	mosaic = UnresolvedMosaic

# binary layout for a non-embedded mosaic supply revocation transaction
struct MosaicSupplyRevocationTransaction
	const uint8 version = 1
	const EntityType entityType = 0x434D

	inline Transaction
	inline MosaicSupplyRevocationTransactionBody

# binary layout for an embedded mosaic supply revocation transaction
struct EmbeddedMosaicSupplyRevocationTransaction
	const uint8 version = 1
	const EntityType entityType = 0x434D

	inline EmbeddedTransaction
	inline MosaicSupplyRevocationTransactionBody
