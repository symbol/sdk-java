import { Serializer } from './Serializer';

/**
 * Generator utility class.
 */
export class GeneratorUtils {

    /**
     * Convert a UInt8Array input into an array of 2 numbers.
     * Numbers in the returned array are cast to UInt32.
     * @param {Uint8Array} input A uint8 array.
     * @returns {number[]} The uint64 representation of the input.
     */
    public static bufferToUint64(input: Uint8Array): number[] {
        const view = new DataView(input.slice(0, 8).reverse().buffer);
        return [view.getUint32(4), view.getUint32(0)];
    }

    /**
     * Read 4 bytes as a uint32 value from buffer bytes starting at given index.
     * @param {Uint8Array} bytes A uint8 array.
     * @param {number} index Index.
     * @returns {number} 32bits integer.
     */
    public static readUint32At(bytes: Uint8Array, index: number): number {
        return (bytes[index] + (bytes[index + 1] << 8) + (bytes[index + 2] << 16) + (bytes[index + 3] << 24)) >>> 0;
    }

    /**
     * Convert uint value into buffer
     * @param {number} uintValue A uint8 array.
     * @param {number} bufferSize Buffer size.
     * @returns {Uint8Array}
     */
    public static uintToBuffer(uintValue: number, bufferSize: number): Uint8Array {
        const buffer = new ArrayBuffer(bufferSize);
        const dataView = new DataView(buffer);
        try {
            if (1 === bufferSize) {
                dataView.setUint8(0, uintValue);
            } else if (2 === bufferSize) {
                dataView.setUint16(0, uintValue, true);
            } else if (4 === bufferSize) {
                dataView.setUint32(0, uintValue, true);
            } else {
                throw new Error('Unexpected bufferSize ' + bufferSize);
            }
            return new Uint8Array(buffer);
        } catch (e) {
            throw new Error(`Converting uint value ` + uintValue + ` into buffer with error: ` + e);
        }
    }
    /**
     * Convert uint value into buffer
     * @param {number} uintValue A uint8 array.
     * @returns {Uint8Array}
     */
    public static uint8ToBuffer(uintValue: number): Uint8Array {
        return GeneratorUtils.uintToBuffer(uintValue, 1);
    }

    /**
     * Convert uint value into buffer
     * @param {number} uintValue A uint8 array.
     * @returns {Uint8Array}
     */
    public static uint16ToBuffer(uintValue: number): Uint8Array {
        return GeneratorUtils.uintToBuffer(uintValue, 2);
    }

    /**
     * Convert uint value into buffer
     * @param {number} uintValue A uint8 array.
     * @returns {Uint8Array}
     */
    public static uint32ToBuffer(uintValue: number): Uint8Array {
        return GeneratorUtils.uintToBuffer(uintValue, 4);
    }

    /**
     * It validates that a value is not undefined or null
     * @param value the value
     * @param message the message in the exception if the value is null or undefined.
     */
    public static notNull(value: any, message: string): void {
        if (value === undefined || value === null) {
            throw new Error(message);
        }
    }

    /**
     * Convert uint8 array buffer into number
     * @param {Uint8Array} buffer A uint8 array.
     * @returns {number}
     */
    public static bufferToUint(buffer: Uint8Array, size: number): number {
        const dataView = new DataView(buffer.buffer);
        try {
            if (1 === size) {
                return dataView.getUint8(0);
            } else if (2 === size) {
                return dataView.getUint16(0, true);
            } else if (4 === size) {
                return dataView.getUint32(0, true);
            }
            throw new Error('Unexpected size ' + size);
        } catch (e) {
            throw new Error(`Converting buffer into number with error:` + e);
        }
    }

    /**
     * Convert uint8 array buffer into number
     * @param {Uint8Array} buffer A uint8 array.
     * @returns {number}
     */
    public static bufferToUint8(buffer: Uint8Array): number {
        return GeneratorUtils.bufferToUint(buffer, 1);
    }

    /**
     * Convert uint8 array buffer into number
     * @param {Uint8Array} buffer A uint8 array.
     * @returns {number}
     */
    public static bufferToUint16(buffer: Uint8Array): number {
        return GeneratorUtils.bufferToUint(buffer, 2);
    }

    /**
     * Convert uint8 array buffer into number
     * @param {Uint8Array} buffer A uint8 array.
     * @returns {number}
     */
    public static bufferToUint32(buffer: Uint8Array): number {
        return GeneratorUtils.bufferToUint(buffer, 4);
    }

    /**
     * Convert unit64 into buffer
     * @param {number} uintValue Uint64 (number[]).
     * @returns {Uint8Array}
     */
    public static uint64ToBuffer(uintValue: number[] | number): Uint8Array {
        const uint32Array = new Uint32Array(GeneratorUtils.fromUint(uintValue));
        return new Uint8Array(uint32Array.buffer);
    }

    /**
     * Concatenate two arrays
     * @param {Uint8Array} array1 A Uint8Array.
     * @param {Uint8Array} array2 A Uint8Array.
     * @returns {Uint8Array}
     */
    public static concatTypedArrays(array1: Uint8Array, array2: Uint8Array): Uint8Array {
        const newArray = new Uint8Array(array1.length + array2.length);
        newArray.set(array1);
        newArray.set(array2, array1.length);
        return newArray;
    }

    /** Converts an unsigned byte to a signed byte with the same binary representation.
     * @param {number} input An unsigned byte.
     * @returns {number} A signed byte with the same binary representation as the input.
     *
     */
    public static uint8ToInt8 = (input: number): number => {
        if (0xff < input) {
            throw Error(`input '` + input + `' is out of range`);
        }
        return (input << 24) >> 24;
    };

    /** Get bytes by given sub array size.
     * @param {Uint8Array} binary Binary bytes array.
     * @param {number} size Subarray size.
     * @returns {Uint8Array}
     *
     */
    public static getBytes(binary: Uint8Array, size: number): Uint8Array {
        if (size > binary.length) {
            throw new RangeError();
        }
        const bytes = binary.slice(0, size);
        return bytes;
    }

    /**
     * Gets the padding size that rounds up \a size to the next multiple of \a alignment.
     * @param size Inner element size
     * @param alignment Next multiple alignment
     */
    public static getPaddingSize(size: number, alignment: number): number {
        if (alignment === 0) {
            return 0;
        }
        return 0 === size % alignment ? 0 : alignment - (size % alignment);
    }

    /**
     * Adds the padding to the reported size according to the alignment
     * @param size the size
     * @param alignment the alignment
     */
    public static getSizeWithPadding(size: number, alignment: number): number {
        return size + GeneratorUtils.getPaddingSize(size, alignment);
    }

    /**
     * Tries to compact a uint64 into a simple numeric.
     * @param {module:coders/uint64~uint64} uint64 A uint64 value.
     * @returns {number|module:coders/uint64~uint64}
     * A numeric if the uint64 is no greater than Number.MAX_SAFE_INTEGER or the original uint64 value otherwise.
     */
    public static compact(uint64: number[] | number): number {
        if (Array.isArray(uint64)) {
            const low = uint64[0];
            const high = uint64[1];
            // don't compact if the value is >= 2^53
            if (0x00200000 <= high) {
                throw new Error('Cannot compact number: ' + uint64);
            }
            // multiply because javascript bit operations operate on 32bit values
            return high * 0x100000000 + low;
        } else {
            return uint64;
        }
    }

    /**
     * Converts a numeric unsigned integer into a uint64.
     * @param {number} number The unsigned integer.
     * @returns {module:coders/uint64~uint64} The uint64 representation of the input.
     */
    public static fromUint(number: number | number[]): number[] {
        if (Array.isArray(number)) {
            return number;
        }
        return [(number & 0xffffffff) >>> 0, (number / 0x100000000) >>> 0];
    }

    /**
     * It loads a static list of entities from the payload
     * @param loadFromBinary the factory function
     * @param payload the payload
     * @param count the amount of entities
     */
    public static loadFromBinary<T extends Serializer>(
        loadFromBinary: (payload: Uint8Array) => T,
        payload: Uint8Array,
        count: number | number[],
    ): T[] {
        const byteArray = Array.from(payload);
        const values: T[] = [];
        for (let i = 0; i < GeneratorUtils.compact(count); i++) {
            const item = loadFromBinary(Uint8Array.from(byteArray));
            const itemSize = item.getSize();
            values.push(item);
            byteArray.splice(0, itemSize);
        }
        return values;
    }

    /**
     * Loads a list of numbers from the array based on the count and number size.
     * @param payload the payload
     * @param count the count
     * @param itemSize the number size.
     */
    public static loadFromBinaryEnums(payload: Uint8Array, count: number | number[], itemSize: number): number[] {
        const byteArray = Array.from(payload);
        const values: number[] = [];
        for (let i = 0; i < GeneratorUtils.compact(count); i++) {
            values.push(GeneratorUtils.bufferToUint(payload, 2));
            byteArray.splice(0, itemSize);
        }
        return values;
    }

    /**
     * It loads a static list of entities from the payload
     * @param loadFromBinary the factory function
     * @param payload the payload
     * @param payloadSize the amount of bytes to process.
     * @param alignment for the padding
     */
    public static loadFromBinaryRemaining<T extends Serializer>(
        loadFromBinary: (payload: Uint8Array) => T,
        payload: Uint8Array,
        payloadSize: number,
        alignment: number,
    ): T[] {
        const byteArray = Array.from(payload);
        let remainingByteSizes: number = payloadSize;
        const transactions: T[] = [];
        while (remainingByteSizes > 0) {
            const item = loadFromBinary(Uint8Array.from(byteArray));
            transactions.push(item);
            let size = item.getSize();
            const itemSize = size + GeneratorUtils.getPaddingSize(item.getSize(), alignment);
            remainingByteSizes -= itemSize;
            byteArray.splice(0, itemSize);
        }
        return transactions;
    }

    /**
     * It converts a list of buffers into an Uint8Array
     * @param elements the buffers to serialize
     * @param alignment add padding to each element according to the alignment.
     * @return the serialized buffer
     */
    public static writeList(elements: Serializer[], alignment: number): Uint8Array {
        return elements.reduce((newArray, item) => {
            const byte = item.serialize();
            const padding = new Uint8Array(GeneratorUtils.getPaddingSize(byte.length, alignment));
            return GeneratorUtils.concatTypedArrays(newArray, GeneratorUtils.concatTypedArrays(byte, padding));
        }, Uint8Array.from([]));
    }

    /**
     * It serializes a list of number to a Uint8Array
     * @param elements
     * @param alignment
     */
    public static writeListEnum(elements: number[], alignment: number): Uint8Array {
        return elements.reduce((newArray, item) => {
            const byte = GeneratorUtils.uint16ToBuffer(item);
            const padding = new Uint8Array(GeneratorUtils.getPaddingSize(byte.length, alignment));
            return GeneratorUtils.concatTypedArrays(newArray, GeneratorUtils.concatTypedArrays(byte, padding));
        }, Uint8Array.from([]));
    }

    /**
     * It generates a list of flags from an aggregated value
     *
     * @param enumClass the enum class holding all the possible values
     * @param bitMaskValue the aggregate value
     * @param the flags
     */
    public static toFlags(enumClass: any, bitMaskValue: number): number[] {
        const values: number[] = Object.keys(enumClass)
            .map((key) => enumClass[key])
            .filter((k) => parseInt(k) >= 0)
            .map((k) => parseInt(k));
        return values.filter((value) => (value & bitMaskValue) !== 0);
    }

    /**
     * It converts a list of flag into an aggregated number
     * @param enumClass the enum class to know the valid numbers
     * @param flags the flags
     */
    public static fromFlags(enumClass: any, flags: number[]): number {
        const values: number[] = Object.keys(enumClass)
            .map((key) => enumClass[key])
            .filter((k) => parseInt(k) >= 0)
            .map((k) => parseInt(k));
        return flags.filter((f) => values.indexOf(f) > -1).reduce((a, b) => a + b, 0);
    }
}
