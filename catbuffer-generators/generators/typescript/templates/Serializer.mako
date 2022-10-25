/** Objects of this interface knows how to serialize a catbuffer object. */
export interface Serializer {

	/**
	 * Serializes an object to bytes.
	 *
	 * @return Serialized bytes.
	 */
	serialize(): Uint8Array;

	/**
	 * Gets the size of the object.
	 *
	 * @return Size in bytes.
	 */
	getSize(): number;
}
