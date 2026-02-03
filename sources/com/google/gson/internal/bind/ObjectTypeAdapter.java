package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class ObjectTypeAdapter extends TypeAdapter<Object> {
    private static final TypeAdapterFactory DOUBLE_FACTORY = newFactory(ToNumberPolicy.DOUBLE);
    private final Gson gson;
    private final ToNumberStrategy toNumberStrategy;

    private ObjectTypeAdapter(Gson gson, ToNumberStrategy toNumberStrategy) {
        this.gson = gson;
        this.toNumberStrategy = toNumberStrategy;
    }

    private static TypeAdapterFactory newFactory(final ToNumberStrategy toNumberStrategy) {
        return new TypeAdapterFactory() { // from class: com.google.gson.internal.bind.ObjectTypeAdapter.1
            @Override // com.google.gson.TypeAdapterFactory
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                if (type.getRawType() == Object.class) {
                    return new ObjectTypeAdapter(gson, toNumberStrategy);
                }
                return null;
            }
        };
    }

    public static TypeAdapterFactory getFactory(ToNumberStrategy toNumberStrategy) {
        if (toNumberStrategy == ToNumberPolicy.DOUBLE) {
            return DOUBLE_FACTORY;
        }
        return newFactory(toNumberStrategy);
    }

    private Object tryBeginNesting(JsonReader in, JsonToken peeked) throws IOException {
        switch (peeked) {
            case BEGIN_ARRAY:
                in.beginArray();
                return new ArrayList();
            case BEGIN_OBJECT:
                in.beginObject();
                return new LinkedTreeMap();
            default:
                return null;
        }
    }

    private Object readTerminal(JsonReader in, JsonToken peeked) throws IOException {
        switch (peeked) {
            case STRING:
                return in.nextString();
            case NUMBER:
                return this.toNumberStrategy.readNumber(in);
            case BOOLEAN:
                return Boolean.valueOf(in.nextBoolean());
            case NULL:
                in.nextNull();
                return null;
            default:
                throw new IllegalStateException("Unexpected token: " + peeked);
        }
    }

    @Override // com.google.gson.TypeAdapter
    public Object read(JsonReader in) throws IOException {
        JsonToken peeked = in.peek();
        Object current = tryBeginNesting(in, peeked);
        if (current == null) {
            return readTerminal(in, peeked);
        }
        Deque<Object> stack = new ArrayDeque<>();
        while (true) {
            if (in.hasNext()) {
                String name = null;
                if (current instanceof Map) {
                    name = in.nextName();
                }
                JsonToken peeked2 = in.peek();
                Object value = tryBeginNesting(in, peeked2);
                boolean isNesting = value != null;
                if (value == null) {
                    value = readTerminal(in, peeked2);
                }
                if (current instanceof List) {
                    List<Object> list = (List) current;
                    list.add(value);
                } else {
                    Map<String, Object> map = (Map) current;
                    map.put(name, value);
                }
                if (isNesting) {
                    stack.addLast(current);
                    current = value;
                }
            } else {
                if (current instanceof List) {
                    in.endArray();
                } else {
                    in.endObject();
                }
                if (stack.isEmpty()) {
                    return current;
                }
                current = stack.removeLast();
            }
        }
    }

    @Override // com.google.gson.TypeAdapter
    public void write(JsonWriter out, Object value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        TypeAdapter<Object> typeAdapter = this.gson.getAdapter(value.getClass());
        if (typeAdapter instanceof ObjectTypeAdapter) {
            out.beginObject();
            out.endObject();
        } else {
            typeAdapter.write(out, value);
        }
    }
}
