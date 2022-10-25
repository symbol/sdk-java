import "transaction.cats"

# binary layout for a transfer transaction
struct TransferTransactionBody
	# recipient address
	recipientAddress = UnresolvedAddress

	# size of attached message
	messageSize = uint16

	# number of attached mosaics
	mosaicsCount = uint8

	# reserved padding to align mosaics on 8-byte boundary
	transferTransactionBody_Reserved1 = uint32

	# reserved padding to align mosaics on 8-byte boundary
	transferTransactionBody_Reserved2 = uint8

	# attached mosaics
	mosaics = array(UnresolvedMosaic, mosaicsCount, sort_key=mosaicId)

	# attached message
	message = array(byte, messageSize)

# binary layout for a non-embedded transfer transaction
struct TransferTransaction
	const uint8 version = 1
	const EntityType entityType = 0x4154

	inline Transaction
	inline TransferTransactionBody

# binary layout for an embedded transfer transaction
struct EmbeddedTransferTransaction
	const uint8 version = 1
	const EntityType entityType = 0x4154

	inline EmbeddedTransaction
	inline TransferTransactionBody
