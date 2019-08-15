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

package io.nem.sdk.infrastructure.okhttp;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.nem.sdk.model.transaction.JsonHelper;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fernando on 06/08/19.
 *
 * @author Fernando Boucquez
 */
public class JsonHelperGson implements JsonHelper {

    private final Gson objectMapper;

    public JsonHelperGson(Gson objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public <T> T parse(final String string, final Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(string)) {
                return null;
            }
            return objectMapper.fromJson(string, clazz);
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
            return objectMapper.toJson(object);
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
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        if (child == null || child.isJsonNull()) {
            return null;
        }
        return child.getAsInt();
    }

    @Override
    public String getString(Object object, String... path) {
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        if (child == null || child.isJsonNull()) {
            return null;
        }
        return child.getAsString();
    }

    @Override
    public Long getLong(Object object, String... path) {
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        if (child == null || child.isJsonNull()) {
            return null;
        }
        return child.getAsLong();
    }

    @Override
    public Boolean getBoolean(Object object, String... path) {
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        if (child == null || child.isJsonNull()) {
            return null;
        }
        return child.getAsBoolean();
    }

    @Override
    public List<Long> getLongList(Object object, String... path) {
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        if (child == null || child.isJsonNull()) {
            return null;
        }
        List<Long> array = new ArrayList<>();
        child.getAsJsonArray().iterator().forEachRemaining(n -> array.add(n.getAsLong()));
        return array;
    }


    private JsonElement getNode(final JsonObject parent, final String... path) {
        JsonElement child = parent;
        if (child == null) {
            return null;
        }
        for (String attribute : path) {
            child = ((JsonObject) child).get(attribute);
            if (child == null) {
                return null;
            }
        }
        return child;
    }
}
