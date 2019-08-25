/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.nem.sdk.model.transaction.JsonHelper;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fernando on 03/08/19.
 *
 * @author Fernando Boucquez
 */
public class JsonHelperJackson2 implements JsonHelper {

    private final ObjectMapper objectMapper;

    public JsonHelperJackson2(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public static ObjectMapper configureMapper(ObjectMapper objectMapper) {
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        objectMapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Override
    public <T> T parse(final String string, final Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(string)) {
                return null;
            }
            return objectMapper.readValue(string, clazz);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @Override
    public String print(final Object object) {
        try {
            if (object == null) {
                return null;
            }
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }


    private static IllegalArgumentException handleException(Exception e) {
        return new IllegalArgumentException(e.getMessage(), e);
    }

    @Override
    public <T> T convert(Object object, Class<T> instanceClass) {
        if (object == null) {
            return null;
        }
        if (instanceClass.isInstance(object)) {
            return (T) object;
        }
        return parse(print(object), instanceClass);
    }

    @Override
    public Integer getInteger(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        if (child == null || child.isNull()) {
            return null;
        }
        return child.asInt();
    }

    @Override
    public String getString(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        if (child == null || child.isNull()) {
            return null;
        }
        return child.textValue();
    }

    @Override
    public Long getLong(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        if (child == null || child.isNull()) {
            return null;
        }
        return child.asLong();
    }

    @Override
    public Boolean getBoolean(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        if (child == null || child.isNull()) {
            return null;
        }
        return child.asBoolean();
    }

    @Override
    public List<Long> getLongList(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        if (child == null || child.isNull()) {
            return null;
        }
        List<Long> array = new ArrayList<>();
        child.iterator().forEachRemaining(n -> array.add(n.longValue()));
        return array;
    }

    @Override
    public boolean contains(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        return child != null && !child.isNull();
    }

    private JsonNode getNode(final JsonNode parent, final String... path) {
        JsonNode child = parent;
        if (child == null) {
            return null;
        }
        for (String attribute : path) {
            child = child.get(attribute);
            if (child == null) {
                return null;
            }
        }
        return child;
    }

}
