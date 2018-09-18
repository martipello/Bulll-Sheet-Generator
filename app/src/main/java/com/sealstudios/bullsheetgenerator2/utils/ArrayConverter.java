package com.sealstudios.bullsheetgenerator2.utils;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ArrayConverter {
    @TypeConverter
    public static String[] fromStringToArray(String value) {
        Type listType = new TypeToken<String[]>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayToString(String[] list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}
