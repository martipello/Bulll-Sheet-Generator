package com.sealstudios.bullsheetgenerator2.database;

import androidx.room.Room;
import android.content.Context;

import com.sealstudios.bullsheetgenerator2.utils.Constants;

public class DatabaseBuilder {
    private static JobDbHelper jobDbHelper;
    private static final Object LOCK = new Object();

    public synchronized static JobDbHelper getDatabase(Context context) {
        if (jobDbHelper == null) {
            synchronized (LOCK) {
                if (jobDbHelper == null) {
                    jobDbHelper = Room.databaseBuilder(context,
                            JobDbHelper.class, Constants.JOB_LIST).build();
                }
            }
        }
        return jobDbHelper;
    }
}
