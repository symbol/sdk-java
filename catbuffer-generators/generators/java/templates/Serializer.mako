/** Objects of this interface knows how to serialize a catbuffer object. */
public interface Serializer {

	/**
	 * Serializes an object to bytes.
	 *
	 * @return Serialized bytes.
	 */
	byte[] serialize();

	/**
	 * Gets the size of the object.
	 *
	 * @return Size in bytes.
	 */
	int getSize();
}
