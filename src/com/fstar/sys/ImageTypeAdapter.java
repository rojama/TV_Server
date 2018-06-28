package com.fstar.sys;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.shiro.codec.Base64;

import java.io.IOException;
import java.lang.reflect.Type;

public class ImageTypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement != null){
            return Base64.decode(jsonElement.getAsString());
        }
        return new byte[0];
    }

    @Override
    public JsonElement serialize(byte[] bytes, Type type, JsonSerializationContext jsonSerializationContext) {
        if (bytes != null){
            return new JsonPrimitive(Base64.encodeToString(bytes));
        }
        return null;
    }
}
