package com.sealstudios.bullsheetgenerator2.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import com.sealstudios.bullsheetgenerator2.objects.Job;
import com.sealstudios.bullsheetgenerator2.objects.JobList;

@Database(entities = {JobList.class}, version = 1)
public abstract class JobDbHelper extends RoomDatabase {
    public abstract JobDao jobDaoLive();
}
