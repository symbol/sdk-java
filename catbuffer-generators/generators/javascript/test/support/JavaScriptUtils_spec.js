const assert = require('assert');
const JavaScriptUtils = require('../../support/JavaScriptUtils.js');
const { expect } = require('chai');


describe('bufferToUint function', () => {
	it('converts a 1 byte unsigned integer', () => {
		// Arrange:
		const int8 = 232;
		const array8 = new ArrayBuffer(1);
		const array8DataView = new DataView(array8);

		// Act:
		array8DataView.setUint8(0, int8, true);
		const value = JavaScriptUtils.bufferToUint(new Uint8Array(array8));
		array8DataView.setUint8(0, 0, true);
		const value2 = JavaScriptUtils.bufferToUint(new Uint8Array(array8));

		// Assert:
		expect(value).to.equal(int8);
		expect(value2).to.equal(0);
	});

	it('converts a 2 byte unsigned integer', () => {
		// Arrange:
		const int16 = 54345;
		const array16 = new ArrayBuffer(2);
		const array16DataView = new DataView(array16);

		// Act:
		array16DataView.setUint16(0, int16, true);
		const value = JavaScriptUtils.bufferToUint(new Uint8Array(array16));
		array16DataView.setUint16(0, 0, true);
		const value2 = JavaScriptUtils.bufferToUint(new Uint8Array(array16));

		// Assert:
		expect(value).to.equal(int16);
		expect(value2).to.equal(0);
	});

	it('converts a 4 byte unsigned integer', () => {
		// Arrange:
		const int32 = 765435;
		const array32 = new ArrayBuffer(4);
		const array32DataView = new DataView(array32);

		// Act:
		array32DataView.setUint32(0, int32, true);
		const value = JavaScriptUtils.bufferToUint(new Uint8Array(array32));
		array32DataView.setUint32(0, 0, true);
		const value2 = JavaScriptUtils.bufferToUint(new Uint8Array(array32));

		// Assert:
		expect(value).to.equal(int32);
		expect(value2).to.equal(0);
	});

	it('throws an exception when buffer size is unsupported', () => {
		// Assert:
		[0, 3, 8, 16].forEach(size => expect(() => JavaScriptUtils.bufferToUint(new Uint8Array(size))).to.throw('Unexpected buffer size'));
	});
});

describe('concatTypedArrays function', () => {
	it('returns an empty array for two empty array inputs', () => {
		// Arrange:
		const array1 = new Uint8Array([]);
		const array2 = new Uint8Array([]);

		// Act:
		const result = JavaScriptUtils.concatTypedArrays(array1, array2);

		// Assert:
		expect(Array.from(result)).to.deep.equal([]);
	});

	it('returns original array if one of the params is empty', () => {
		// Arrange:
		const array1 = new Uint8Array([]);
		const array2 = new Uint8Array([23, 54]);

		// Act:
		const result = JavaScriptUtils.concatTypedArrays(array1, array2);
		const result2 = JavaScriptUtils.concatTypedArrays(array2, array1);

		// Assert:
		expect(Array.from(result)).to.deep.equal([23, 54]);
		expect(Array.from(result2)).to.deep.equal([23, 54]);
	});

	it('returns the ordered concatenation of both params', () => {
		// Arrange:
		const array1 = new Uint8Array([23, 54]);
		const array2 = new Uint8Array([34, 2, 77, 91, 12]);

		// Act:
		const result = JavaScriptUtils.concatTypedArrays(array1, array2);
		const result2 = JavaScriptUtils.concatTypedArrays(array2, array1);

		// Assert:
		expect(Array.from(result)).to.deep.equal([23, 54, 34, 2, 77, 91, 12]);
		expect(Array.from(result2)).to.deep.equal([34, 2, 77, 91, 12, 23, 54]);
	});
});

describe('fitByteArray function', () => {
	it('throws if data provided is larger than the requested output size', () => {
		// Assert:
		assert.throws(() => JavaScriptUtils.fitByteArray(new Uint8Array([34, 2, 77, 91, 12]), 2), RangeError);
	});

	it('returns the same array if the parameter size is the same as the parameter array length', () => {
		// Arrange:
		const array1 = new Uint8Array([]);
		const array2 = new Uint8Array([34, 2, 77, 91, 12]);

		// Assert:
		expect(JavaScriptUtils.fitByteArray(array1, array1.length)).to.deep.equal(array1);
		expect(JavaScriptUtils.fitByteArray(array2, array2.length)).to.deep.equal(array2);
	});

	it('returns an array of the provided size initialized with trailing 0s if the input array was smaller', () => {
		// Arrange:
		const array1 = new Uint8Array([4]);
		const array2 = new Uint8Array([34, 2, 77, 91, 12]);

		// Assert:
		expect(Array.from(JavaScriptUtils.fitByteArray(array1, 2))).to.deep.equal([0].concat(Array.from(array1)));
		expect(Array.from(JavaScriptUtils.fitByteArray(array2, 10))).to.deep.equal([0, 0, 0, 0, 0].concat(Array.from(array2)));
	});
});

describe('Uint8ArrayConsumableBuffer class', () => {
	it('constructs with buffer offset to 0 and stored array', () => {
		// Arrange:
		const array = new Uint8Array([34, 2, 77, 91, 12]);

		// Act:
		const consumableBuffer = new JavaScriptUtils.Uint8ArrayConsumableBuffer(array);

		// Assert:
		expect(consumableBuffer.offset).to.equal(0);
		expect(consumableBuffer.binary).to.equal(array);
	});

	it('getBytes method throws if requesting more bytes than available with offset 0', () => {
		// Arrange:
		const array1 = new Uint8Array([]);
		const array2 = new Uint8Array([34, 2, 77, 91, 12]);
		const consumableBuffer1 = new JavaScriptUtils.Uint8ArrayConsumableBuffer(array1);
		const consumableBuffer2 = new JavaScriptUtils.Uint8ArrayConsumableBuffer(array2);

		// Assert:
		expect(() => consumableBuffer1.getBytes(array1.length + 1)).to.throw();
		expect(() => consumableBuffer2.getBytes(array2.length + 1)).to.throw();
	});

	it('getBytes method throws if requesting more bytes than available when offset is not 0', () => {
		// Arrange:
		const array = new Uint8Array([34, 2, 77, 91, 12]);
		const consumableBuffer = new JavaScriptUtils.Uint8ArrayConsumableBuffer(array);
		consumableBuffer.offset = 5;

		// Assert:
		expect(() => consumableBuffer.getBytes(1)).to.throw();
	});

	it('getBytes method returns requested bytes, and increases offset', () => {
		// Arrange:
		const array = [34, 2, 77, 91, 12];
		const consumableBuffer = new JavaScriptUtils.Uint8ArrayConsumableBuffer(new Uint8Array(array));

		// Assert:
		expect(Array.from(consumableBuffer.getBytes(2))).to.deep.equal([34, 2]);
		expect(consumableBuffer.offset).to.equal(2);

		expect(Array.from(consumableBuffer.getBytes(3))).to.deep.equal([77, 91, 12]);
		expect(consumableBuffer.offset).to.equal(5);
	});
});

describe('uintToBuffer function', () => {
	it('converts an integer to a 1 byte buffer', () => {
		// Arrange:
		const int8 = 233;

		// Act:
		const buffer = JavaScriptUtils.uintToBuffer(int8, 1);

		// Assert:
		expect(buffer.byteLength).to.equal(1);
		expect(Array.from(buffer)).to.deep.equal([233]);
	});

	it('converts an integer to a 2 byte buffer', () => {
		// Arrange:
		const int16 = 54346;

		// Act:
		const buffer = JavaScriptUtils.uintToBuffer(int16, 2);

		// Assert:
		expect(buffer.byteLength).to.equal(2);
		expect(Array.from(buffer)).to.deep.equal([74, 212]);
	});

	it('converts an integer to a 4 byte buffer', () => {
		// Arrange:
		const int32 = 765436;

		// Act:
		const buffer = JavaScriptUtils.uintToBuffer(int32, 4);

		// Assert:
		expect(buffer.byteLength).to.equal(4);
		expect(Array.from(buffer)).to.deep.equal([252, 173, 11, 0]);
	});

	it('throws for unexpected sizes', () => {
		// Assert:
		[0, 3, 8, 16].forEach(size => expect(() => JavaScriptUtils.uintToBuffer(765436, size)).to.throw('Unexpected bufferSize'));
	});
});
