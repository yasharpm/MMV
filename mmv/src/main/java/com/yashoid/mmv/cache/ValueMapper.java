package com.yashoid.mmv.cache;

import com.yashoid.mmv.ModelFeatures;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ValueMapper {

    private static final int TYPE_NULL = 0;
    private static final int TYPE_STRING = 1;
    private static final int TYPE_INTEGER = 2;
    private static final int TYPE_LONG = 3;
    private static final int TYPE_FLOAT = 4;
    private static final int TYPE_DOUBLE = 5;
    private static final int TYPE_BOOLEAN = 6;
    private static final int TYPE_CHAR = 7;
    private static final int TYPE_LIST = 8;
    private static final int TYPE_MAP = 9;
    private static final int TYPE_MODEL_FEATURES = 10;

    protected static JSONObject fromValue(Map<String, Object> value) {
        JSONObject obj = new JSONObject();

        for (Map.Entry<String, Object> entry: value.entrySet()) {
            try {
                obj.put(entry.getKey(), new ValueModel(entry.getValue()).toJSON());
            } catch (JSONException e) { }
        }

        return obj;
    }

    protected static Object toValue(Object rawValue) throws JSONException {
        if (rawValue == null || rawValue == JSONObject.NULL) {
            return null;
        }

        if (rawValue instanceof String) {
            return toValueFromString((String) rawValue);
        }

        if (rawValue instanceof JSONObject) {
            return toValueFromJsonObject((JSONObject) rawValue);
        }

        return rawValue;
    }

    private static Object toValueFromString(String rawValue) {
        try {
            return Integer.parseInt(rawValue);
        } catch (Throwable t) {}

        try {
            return Long.parseLong(rawValue);
        } catch (Throwable t) {}

        try {
            return Float.parseFloat(rawValue);
        } catch (Throwable t) {}

        try {
            return Double.parseDouble(rawValue);
        } catch (Throwable t) {}

        try {
            return Boolean.parseBoolean(rawValue);
        } catch (Throwable t) {}

        return rawValue;
    }

    private static Object toValueFromJsonObject(JSONObject rawValue) throws JSONException {
        return new ValueModel(rawValue).value;
    }

    private static class ValueModel {

        int type = TYPE_NULL;
        Object value = null;

        private ValueModel(Object rawValue) {
            if (rawValue == null) {
                type = TYPE_NULL;
                value = null;
                return;
            }

            if (rawValue instanceof String) {
                type = TYPE_STRING;
                value = rawValue;
                return;
            }

            if (rawValue instanceof Integer) {
                type = TYPE_INTEGER;
                value = rawValue;
                return;
            }

            if (rawValue instanceof Long) {
                type = TYPE_LONG;
                value = rawValue;
                return;
            }

            if (rawValue instanceof Float) {
                type = TYPE_FLOAT;
                value = rawValue;
                return;
            }

            if (rawValue instanceof Double) {
                type = TYPE_DOUBLE;
                value = rawValue;
                return;
            }

            if (rawValue instanceof Boolean) {
                type = TYPE_BOOLEAN;
                value = rawValue;
                return;
            }

            if (rawValue instanceof Character) {
                type = TYPE_CHAR;
                value = rawValue;
                return;
            }

            if (rawValue instanceof List) {
                type = TYPE_LIST;

                JSONArray array = new JSONArray();

                for (Object obj: ((List) rawValue)) {
                    array.put(new ValueModel(obj).toJSON());
                }

                value = array;
            }

            if (rawValue instanceof Map) {
                type = TYPE_MAP;

                JSONObject object = new JSONObject();

                for (Map.Entry<String, Object> entry: (((Map<String, Object>) rawValue).entrySet())) {
                    try {
                        object.put(entry.getKey(), new ValueModel(entry.getValue()).toJSON());
                    } catch (JSONException e) { }
                }

                value = object;
            }

            if (rawValue instanceof ModelFeatures) {
                type = TYPE_MODEL_FEATURES;
                value = new ValueModel(((ModelFeatures) rawValue).getAll()).value;
            }

            return;
        }

        private ValueModel(JSONObject json) throws JSONException {
            type = json.getInt("t");

            String rawValue = json.isNull("v") ? null : json.getString("v");

            switch (type) {
                case TYPE_NULL:
                    value = null;
                    break;
                case TYPE_STRING:
                    value = rawValue;
                    break;
                case TYPE_INTEGER:
                    value = Integer.parseInt(rawValue);
                    break;
                case TYPE_LONG:
                    value = Long.parseLong(rawValue);
                    break;
                case TYPE_FLOAT:
                    value = Float.parseFloat(rawValue);
                    break;
                case TYPE_DOUBLE:
                    value = Double.parseDouble(rawValue);
                    break;
                case TYPE_BOOLEAN:
                    value = Boolean.parseBoolean(rawValue);
                    break;
                case TYPE_CHAR:
                    value = rawValue.charAt(0);
                    break;
                case TYPE_LIST:
                    JSONArray array = new JSONArray(rawValue);

                    value = new ArrayList(array.length());

                    for (int i = 0; i < array.length(); i++) {
                        ((List) value).add(new ValueModel(array.getJSONObject(i)).value);
                    }

                    break;
                case TYPE_MAP:
                    value = mapFromJSON(rawValue);
                    break;
                case TYPE_MODEL_FEATURES:
                    value = new ModelFeatures.Builder().addAll(mapFromJSON(rawValue)).build();
                    break;
            }
        }

        public JSONObject toJSON() {
            JSONObject json = new JSONObject();

            try {
                json.put("t", type);
                json.put("v", value == null ? JSONObject.NULL : value.toString());
            } catch (JSONException e) { }

            return json;
        }

    }

    private static Map<String, Object> mapFromJSON(String rawValue) throws JSONException {
        JSONObject object = new JSONObject(rawValue);

        Map<String, Object> value = new HashMap<>(object.length());

        Iterator<String> keyIterator = object.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            value.put(key, new ValueModel(object.getJSONObject(key)).value);
        }

        return value;
    }

}
