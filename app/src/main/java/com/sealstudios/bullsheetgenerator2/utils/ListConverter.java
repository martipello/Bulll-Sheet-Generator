package com.sealstudios.bullsheetgenerator2.utils;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sealstudios.bullsheetgenerator2.objects.Job;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ListConverter {

    @TypeConverter
    public static ArrayList<Job> jobListFromString(String value) {
        Type listType = new TypeToken<ArrayList<Job>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String stringFromJobList(ArrayList<Job> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
