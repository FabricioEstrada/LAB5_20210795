package com.example.lab5_20210795;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HabitStoragePrefs {
    private static final String PREFS_NAME = "habit_prefs";
    private static final String KEY_HABITS = "habits";

    public static void guardarHabitos(Context context, List<Habit> lista) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(lista);
        editor.putString(KEY_HABITS, json);
        editor.apply(); // auto-save
    }

    public static List<Habit> cargarHabitos(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HABITS, null);

        if (json != null) {
            Type type = new TypeToken<List<Habit>>(){}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }
    public static int obtenerNuevoIdUnico(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int idActual = prefs.getInt("contador_habitos", 1); // inicia desde 1
        prefs.edit().putInt("contador_habitos", idActual + 1).apply();
        return idActual;
    }

}
