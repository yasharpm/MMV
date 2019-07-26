package com.yashoid.mmv.cache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ValueMapper {

    protected static JSONObject fromValue(Map<String, Object> value) {
        JSONObject json = new JSONObject();

        for (Map.Entry<String, Object> entry: value.entrySet()) {
            try {
                json.put(entry.getKey(), fromValue(entry.getValue()));
            } catch (JSONException e) { }
        }

        return json;
    }

    private static Object fromValue(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return value;
        }

        if (value instanceof Number) {
            return value;
        }

        if (value instanceof Boolean) {
            return value;
        }

        if (value instanceof Character) {
            return "" + value;
        }

        if (value instanceof Map) {
            return fromValue((Map<String, Object>) value);
        }

        if (value instanceof List) {
            JSONArray array = new JSONArray();

            for (Object obj: (List) value) {
                array.put(fromValue(obj));
            }

            return array;
        }

        return null;
    }

    protected static Object toValue(Object rawValue) {
        if (rawValue == null || rawValue == JSONObject.NULL) {
            return null;
        }

        if (rawValue instanceof String) {
            return toValueFromString((String) rawValue);
        }

        if (rawValue instanceof JSONObject) {
            return toValueFromJsonObject((JSONObject) rawValue);
        }

        if (rawValue instanceof JSONArray) {
            return toValueFromJsonArray((JSONArray) rawValue);
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

    private static Object toValueFromJsonObject(JSONObject rawValue) {
        Map<String, Object> value = new HashMap<>(rawValue.length());

        Iterator<String> keyIterator = rawValue.keys();

        while (keyIterator.hasNext()) {
            String key = keyIterator.next();

            try {
                value.put(key, toValue(rawValue.get(key)));
            } catch (JSONException e) { }
        }

        return value;
    }

    private static Object toValueFromJsonArray(JSONArray rawValue) {
        List<Object> values = new ArrayList<>(rawValue.length());

        int stringTypeCount = 0;
        int nonNullCount = 0;

        for (int i = 0; i < rawValue.length(); i++) {
            try {
                Object value = toValue(rawValue.get(i));

                if (value != null) {
                    nonNullCount++;

                    if (value instanceof String) {
                        stringTypeCount++;
                    }
                }

                values.add(value);
            } catch (JSONException e) { }
        }

        if (stringTypeCount > 0 && nonNullCount != stringTypeCount) {
            // Convert all to String

            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) != null) {
                    values.set(i, values.get(i).toString());
                }
            }
        }

        return values;
    }

}
