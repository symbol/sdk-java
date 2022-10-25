package io.nem.symbol.catapult.builders;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.ExceptionUtils;
import org.yaml.snakeyaml.Yaml;


public class VectorTest {

    public static final String TEST_RESOURCES_VECTOR = "src/test/resources/vector";

    public static class BuilderTestItem {

        public final String filename;

        public final String builder;

        public final String payload;

        public final String comment;

        public BuilderTestItem(String filename, String builder, String payload, String comment) {
            this.filename = filename;
            this.builder = builder;
            this.payload = payload;
            this.comment = comment;
        }

        @Override
        public String toString() {
            String commentSuffix = comment == null ? hash(payload) : comment;
            return filename + " - " + builder + " - "  + commentSuffix;
        }

        public static String hash(String stringToHash) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(stringToHash.getBytes());
                return GeneratorUtils.toHex(messageDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private static List<BuilderTestItem> vectors() throws Exception {
        List<Path> walk = Files.walk(Paths.get(TEST_RESOURCES_VECTOR)).collect(Collectors.toList());
        try (Stream<Path> paths = walk.stream()) {
            return paths
                .filter(Files::isRegularFile).map(Path::toFile)
                .flatMap(VectorTest::getVectorFromFile).collect(Collectors.toList());
        }
    }

    private static Stream<BuilderTestItem> getVectorFromFile(File file) {
        try {
            InputStream input = new FileInputStream(file);
            Yaml yaml = new Yaml();
            List<Map<String, String>> data = yaml.load(input);
            return data.stream().map(
                stringStringMap -> {
                    String payload = Objects.toString(stringStringMap.get("payload"));
                    return new BuilderTestItem(file.getName(),
                        stringStringMap.get("builder"),
                        payload,
                        stringStringMap.get("comment"));
                });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    @ParameterizedTest
    @MethodSource("vectors")
    public void serialization(BuilderTestItem item) {
        try {
            String className = this.getClass().getPackage().getName() + "." + item.builder;
            DataInputStream inputStream = new DataInputStream(
                new ByteArrayInputStream(GeneratorUtils.hexToBytes(item.payload)));
            Serializer serializer = (Serializer) Class.forName(className)
                .getMethod("loadFromBinary", DataInputStream.class).invoke(null,
                    inputStream);
            Assertions.assertEquals(item.payload.toUpperCase(), GeneratorUtils.toHex(serializer.serialize()).toUpperCase());
        } catch (RuntimeException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Assertions
                .fail("Cannot run test " + item + " Error: " + ExceptionUtils.readStackTrace(e));
        }

    }

}
