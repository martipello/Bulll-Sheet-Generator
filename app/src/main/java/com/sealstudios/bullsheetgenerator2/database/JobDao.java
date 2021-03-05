package com.sealstudios.bullsheetgenerator2.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.sealstudios.bullsheetgenerator2.objects.JobList;

import java.util.List;

@Dao
public interface JobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertJobList(JobList jobList);

    @Query("SELECT * FROM joblist ORDER BY jobListFavourite Desc , jobId DESC")
    LiveData<List<JobList>> getAllJobLists();

    @Query("SELECT * FROM joblist WHERE jobId == :i")
    LiveData<JobList> getJobListById(int i);

    @Query("SELECT * FROM joblist ORDER BY jobId DESC LIMIT :i")
    LiveData<JobList> getAllLiveJobListsWithLimit(int i);

    @Query("SELECT * FROM joblist ORDER BY jobId DESC LIMIT :i")
    List<JobList> getAllJobListsWithLimit(int i);

    @Query("SELECT * FROM joblist WHERE jobListsTags = :tag ORDER BY jobListFavourite ASC, jobId DESC")
    LiveData<List<JobList>> getAllJobListsByTag(String tag);


    @Update
    void updateJobList(JobList jobList);

    @Delete
    void deleteJobList(JobList jobList);

}

