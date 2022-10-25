const bufferToUint = buffer => {
	const dataView = new DataView(buffer.buffer);
	if (1 === buffer.byteLength)
		return dataView.getUint8(0, true);

	else if (2 === buffer.byteLength)
		return dataView.getUint16(0, true);

	else if (4 === buffer.byteLength)
		return dataView.getUint32(0, true);

	throw new Error('Unexpected buffer size');
};

const concatTypedArrays = (array1, array2) => {
	const newArray = new Uint8Array(array1.length + array2.length);
	newArray.set(array1);
	newArray.set(array2, array1.length);
	return newArray;
};

const fitByteArray = (array, size) => {
	if (array.length > size) {
		throw new RangeError('Data size larger than allowed');
	} else if (array.length < size) {
		const newArray = new Uint8Array(size);
		newArray.fill(0);
		newArray.set(array, size - array.length);
		return newArray;
	}
	return array;
};

class Uint8ArrayConsumableBuffer {
	constructor(binary) {
		this.offset = 0;
		this.binary = binary;
	}
	getBytes(count) {
		if (count + this.offset > this.binary.length)
			throw new RangeError();

		const bytes = this.binary.slice(this.offset, this.offset + count);
		this.offset += count;
		return bytes;
	}
}

const uintToBuffer = (uintValue, bufferSize) => {
	const buffer = new ArrayBuffer(bufferSize);
	const dataView = new DataView(buffer);
	if (1 === bufferSize)
		dataView.setUint8(0, uintValue, true);

	else if (2 === bufferSize)
		dataView.setUint16(0, uintValue, true);

	else if (4 === bufferSize)
		dataView.setUint32(0, uintValue, true);

	else
		throw new Error('Unexpected bufferSize');

	return new Uint8Array(buffer);
};

module.exports = {
	bufferToUint,
	concatTypedArrays,
	fitByteArray,
	Uint8ArrayConsumableBuffer,
	uintToBuffer
};
