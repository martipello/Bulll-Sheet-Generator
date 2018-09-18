package com.sealstudios.bullsheetgenerator2.jobtasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.sealstudios.bullsheetgenerator2.FinalListActivity;
import com.sealstudios.bullsheetgenerator2.database.JobListRepository;
import com.sealstudios.bullsheetgenerator2.objects.Job;
import com.sealstudios.bullsheetgenerator2.objects.JobList;
import com.sealstudios.bullsheetgenerator2.utils.Constants;
import com.sealstudios.bullsheetgenerator2.utils.ListConverter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static com.sealstudios.bullsheetgenerator2.utils.DateListGenerator.getDates;

public class ProcessJobsAsyncTaskForTesting extends AsyncTask<String, Void, JobList> {

    private String TAG = "PrcsJbsAsncTsk";
    private processJobsGetTaskListener processJobsGetTaskListener = null;
    Exception exception = null;
    private Context context;

    public ProcessJobsAsyncTaskForTesting(Context context) {
        this.context = context;
    }

    @Override
    protected JobList doInBackground(String... strings) {
        ArrayList<Job> listOfJobs = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        JobListRepository jobListRepository = new JobListRepository(context);
        String dateTo = "01-09-2018";
        String dateFrom = "15-09-2018";
        //all dates between the two dates
        List<Date> dateList = getDates(dateFrom, dateTo);
        //get the most recent created lists limited by JOB_LISTS_TO_RETURN
        //create an executor or async task that return these and makes us wait
        //List<JobList> jobListArrayList = jobListRepository.getAllWithLimit(Constants.JOB_LISTS_TO_RETURN);
        List<JobList> jobListArrayList = jobListRepository.getFutureAllWithLimit(Constants.JOB_LISTS_TO_RETURN);
        //
        if (jobListArrayList != null){
            for (JobList jobList : jobListArrayList){
                Log.d(TAG,"for loop");
                //get all tags and only add them if they're unique, could use a hashset
                for (String tag : jobList.getJobListsTags()){
                    if (!tags.contains(tag)){
                        tags.add(tag);
                        Log.d(TAG,"tag = " + tag);
                    }
                }
                listOfJobs.addAll(ListConverter.jobListFromString(jobList.getJobList()));
                //TODO MAYBE DELETE THE LEFT OVER LISTS
            }
        }else{
            return null;
        }

        // repetition how many times to repeat the date
        int repetition = listOfJobs.size() / dateList.size();
        Log.d(TAG,"repetition is " + repetition);
        List<Date> results = new ArrayList<>();
        for (Date d : dateList) {
            for (int i = 0; i <= repetition; ++i)
                //create a new bigger list of dates
                results.add(d);
        }
        Log.d(TAG,"result list size is " + results.size());
        //insert date to each job in the list
        for(int i = 0; i < listOfJobs.size(); i++){
            String[] parts = results.get(i).toString().split("00:00:00");
            String string  = parts[0];
            listOfJobs.get(i).setJobDate(string.trim());
        }
        JobList myJobList = new JobList();
        myJobList.setJobList(ListConverter.stringFromJobList(listOfJobs));
        myJobList.setJobListFavourite(false);
        myJobList.getJobListsTags();
        return myJobList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public ProcessJobsAsyncTaskForTesting  setProcessJobsGetTaskListener(processJobsGetTaskListener listener){
        this.processJobsGetTaskListener = listener;
        return this;
    }

    @Override
    protected void onCancelled(JobList jobList) {
        super.onCancelled(jobList);
        if (this.processJobsGetTaskListener != null){
            exception = new InterruptedException("Async cancelled");
            this.processJobsGetTaskListener.onComplete(null,exception);
        }
    }

    @Override
    protected void onPostExecute(JobList jobList) {
        super.onPostExecute(jobList);
        if (jobList != null){
            if (jobList.getJobList().isEmpty()){
                Log.d(TAG,"list is empty");
                String bullsheet = "Bullsheet! something went wrong, this can sometimes happen, try again in a minute.";
                Job job = new Job("","","","","",bullsheet);
                ListConverter.jobListFromString(jobList.getJobList()).add(job);
            }
            Log.d(TAG,"list size is " + ListConverter.jobListFromString(jobList.getJobList()).size());
            JobListRepository jobListRepository = new JobListRepository(context);
            jobListRepository.addList(jobList);
        }else{
            //TODO create a joblist
            jobList = new JobList();
            ArrayList<Job> jobArrayList = new ArrayList<>();
            Log.d(TAG,"list is null");
            String bullsheet = "Bullsheet! something went wrong, this can sometimes happen, try again in a minute.";
            Job job = new Job("","","","","",bullsheet);
            jobArrayList.add(job);
            jobList.setJobList(ListConverter.stringFromJobList(jobArrayList));
            jobList.setJobListsTags(new String[]{"EMPTY"});
            jobList.setJobListFavourite(false);
        }

        if (this.processJobsGetTaskListener != null){
            this.processJobsGetTaskListener.onComplete(jobList,exception);
        }
    }

    public interface processJobsGetTaskListener {
        void onComplete(JobList jobList, Exception e);
    }


}
