package com.sealstudios.bullsheetgenerator2.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.sealstudios.bullsheetgenerator2.objects.JobList;

@Database(entities = {JobList.class}, version = 1)
public abstract class JobDbHelper extends RoomDatabase {
    public abstract JobDao jobDaoLive();
}
