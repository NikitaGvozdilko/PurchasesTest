package com.example.purchasestest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;


public class AppPref {
    private static final String PREF_ID = "id";
    private SharedPreferences prefs;

    public AppPref(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getId() {
        int id = prefs.getInt(PREF_ID, 0);
        updateId(id);
        return id;
    }

    private void updateId(int id) {
        prefs.edit().putInt(PREF_ID, id + 1).apply();
    }


}
