package com.sealstudios.bullsheetgenerator2.database;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sealstudios.bullsheetgenerator2.objects.JobList;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JobListRepository {
    private final JobDao jobDao;
    private LiveData<JobList> jobListLiveData;
    private static String TAG = "JbLstRepo";
    private JobDbHelper db;

    public JobListRepository(Application application) {
        db = DatabaseBuilder.getDatabase(application);
        jobDao = db.jobDaoLive();
    }

    public JobListRepository(Context application) {
        db = DatabaseBuilder.getDatabase(application);
        jobDao = db.jobDaoLive();
    }

    public LiveData<List<JobList>> getLists() {
        return jobDao.getAllJobLists();
    }

    public LiveData<JobList> getJobListById(int id){
        return jobDao.getJobListById(id);
    }

    public LiveData<JobList> getJobListWithLimit(int limit){
        return jobDao.getAllLiveJobListsWithLimit(limit);
    }

    public LiveData<List<JobList>> getJobListByTag(String tag){
        return jobDao.getAllJobListsByTag(tag);
    }

    public void addList(JobList jobList){
        new insertJobList(jobDao, jobList).execute();
    }

    public void updateList(JobList jobList){
        new updateJobList(jobDao, jobList).execute();
    }

    public void deleteJobList(JobList jobList){
        new deleteJobList(jobDao, jobList).execute();
    }

    public void deleteAllJobList(){
        new deleteAllJobList(db).execute();
    }

    public List<JobList> getAllWithLimit(int i) {
        try {
            return new getJobListsWithLimit(jobDao, i).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<JobList> getFutureAllWithLimit(int i){
        JobListRepository.getFutureJobListsWithLimit task = new JobListRepository.getFutureJobListsWithLimit(jobDao, i);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<List<JobList>> future = executor.submit(task);
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public class getFutureJobListsWithLimit implements Callable<List<JobList>> {

        private JobDao mDao;
        private int limit;

        public getFutureJobListsWithLimit(JobDao dao, int limit) {
            this.mDao = dao;
            this.limit = limit;
        }

        @Override
        public List<JobList> call() {
            return mDao.getAllJobListsWithLimit(limit);
        }
    }

    private static class getJobListsWithLimit extends AsyncTask<Void, Void, List<JobList>> {

        private JobDao mDao;
        private int limit;

        getJobListsWithLimit(JobDao dao, int limit) {
            this.mDao = dao;
            this.limit = limit;
            Log.d(TAG,"async task");
        }

        @Override
        protected List<JobList> doInBackground(Void... params) {
            Log.d(TAG,"doInBackground");
            return mDao.getAllJobListsWithLimit(limit);
        }
    }

    private static class insertJobList extends AsyncTask<Void, Void, Void> {

        private JobDao mDao;
        private JobList jobList;

        insertJobList(JobDao dao, JobList jobList) {
            this.mDao = dao;
            this.jobList = jobList;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG,"insert joblist");
            mDao.insertJobList(jobList);
            return null;
        }
    }

    private static class updateJobList extends AsyncTask<Void, Void, Void> {

        private JobDao mDao;
        private JobList jobList;

        updateJobList(JobDao dao, JobList jobList) {
            this.mDao = dao;
            this.jobList = jobList;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG,"insert joblist");
            mDao.updateJobList(jobList);
            return null;
        }
    }

    private static class deleteJobList extends AsyncTask<Void, Void, Void> {

        private JobDao mDao;
        private JobList jobList;

        deleteJobList(JobDao dao, JobList jobList) {
            this.mDao = dao;
            this.jobList = jobList;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG,"insert joblist");
            mDao.deleteJobList(jobList);
            return null;
        }
    }

    private static class deleteAllJobList extends AsyncTask<Void, Void, Void> {

        private JobDbHelper mDao;

        deleteAllJobList(JobDbHelper dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDao.clearAllTables();
            return null;
        }
    }

}
