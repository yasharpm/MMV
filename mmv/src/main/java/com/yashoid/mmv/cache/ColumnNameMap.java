package com.yashoid.mmv.cache;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ColumnNameMap implements ModelCacheColumns {

    private static final String PREFERENCES_NAME = "com.yashoid.mmv.cache_namemap";

    private static final String KEY_IDENTIFIERS = "identifiers";

    private static ColumnNameMap mInstance = null;

    protected static ColumnNameMap getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ColumnNameMap(context);
        }

        return mInstance;
    }

    private SharedPreferences mPreferences;

    private List<String> mIdentifiers;

    private ColumnNameMap(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        String sIdentifiers = mPreferences.getString(KEY_IDENTIFIERS, "[]");

        try {
            JSONArray jIdentifiers = new JSONArray(sIdentifiers);

            mIdentifiers = new ArrayList<>(jIdentifiers.length());

            for (int i = 0; i < jIdentifiers.length(); i++) {
                mIdentifiers.add(jIdentifiers.getString(i));
            }
        } catch (JSONException e) { }
    }

    protected synchronized String getColumnName(String identifier) {
        int index = mIdentifiers.indexOf(identifier);

        if (index < 0) {
            mIdentifiers.add(identifier);

            update();

            return DATA + (mIdentifiers.size() - 1);
        }

        return DATA + identifier;
    }

    private void update() {
        JSONArray jIdentifiers = new JSONArray();

        for (String identifier: mIdentifiers) {
            jIdentifiers.put(identifier);
        }

        mPreferences.edit().putString(KEY_IDENTIFIERS, jIdentifiers.toString()).apply();
    }

}
