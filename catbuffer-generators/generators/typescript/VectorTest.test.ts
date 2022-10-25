import * as YAML from 'yaml';
import * as assert from 'assert';
import * as fs from 'fs';
import * as builders from '../src';

interface BuilderTestItem {
    filename: string;
    builder: string;
    payload: string;
    comment: string;
}

const fromHexString = (hexString: string) =>
    new Uint8Array((hexString.match(/.{1,2}/g) || []).map(byte => parseInt(byte, 16)));

const toHexString = (bytes: Uint8Array) =>
    bytes.reduce((str, byte) => str + byte.toString(16).padStart(2, '0'), '').toUpperCase();

const vectorDirectory = 'test/vector';
const files = fs.readdirSync(vectorDirectory);

const items: BuilderTestItem[] = files.map(filename => {
    const yamlText = fs.readFileSync(vectorDirectory + '/' + filename, 'utf8');
    const yamlList = YAML.parse(yamlText)
    return yamlList.map((a: BuilderTestItem) => {
        const builder = a.builder;
        return ({
            ...a, builder: builder, filename
        } as BuilderTestItem);
    });
}).reduce((acc, val) => acc.concat(val), []);


describe('serialize', function () {
    items.forEach(item => {
        const stringPayload = item.payload + '';
        it(item.filename + " - " + item.builder + " - " + (item.comment || stringPayload), function () {
            const builderClass = (<any>builders)[item.builder]
            const serializer = builderClass.loadFromBinary(fromHexString(stringPayload));
            assert.equal(toHexString(serializer.serialize()), stringPayload.toUpperCase())
            assert.equal(serializer.getSize(), stringPayload.length / 2)
        });
    })
});
