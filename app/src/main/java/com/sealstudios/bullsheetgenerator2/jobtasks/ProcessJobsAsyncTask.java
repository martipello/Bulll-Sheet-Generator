package com.sealstudios.bullsheetgenerator2.jobtasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sealstudios.bullsheetgenerator2.FinalListActivity;
import com.sealstudios.bullsheetgenerator2.R;
import com.sealstudios.bullsheetgenerator2.database.JobListRepository;
import com.sealstudios.bullsheetgenerator2.objects.Job;
import com.sealstudios.bullsheetgenerator2.objects.JobList;
import com.sealstudios.bullsheetgenerator2.utils.Constants;
import com.sealstudios.bullsheetgenerator2.utils.ListConverter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

import static com.sealstudios.bullsheetgenerator2.utils.DateListGenerator.getDates;

public class ProcessJobsAsyncTask extends AsyncTask<String, Void, JobList> {

    private WeakReference<CircularProgressButton> weakRefProgressBar;
    private String TAG = "PrcsJbsAsncTsk";

    public ProcessJobsAsyncTask(CircularProgressButton progressBar) {
        this.weakRefProgressBar = new WeakReference<>(progressBar);
    }

    @Override
    protected JobList doInBackground(String... strings) {
        ArrayList<Job> listOfJobs = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        Context context = weakRefProgressBar.get().getContext();
        JobListRepository jobListRepository = new JobListRepository(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dateTo = sharedPreferences.getString(Constants.DATE_TO,"");
        String dateFrom = sharedPreferences.getString(Constants.DATE_FROM, "");
        int max = 0;
        //all dates between the two dates
        List<Date> dateList = getDates(dateFrom, dateTo);
        //get the most recent created lists limited by JOB_LISTS_TO_RETURN
        //create an executor or async task that return these and makes us wait
        //List<JobList> jobListArrayList = jobListRepository.getAllWithLimit(Constants.JOB_LISTS_TO_RETURN);
        List<JobList> jobListArrayList = jobListRepository.getFutureAllWithLimit(Constants.JOB_LISTS_TO_RETURN);

        for (JobList jobList : jobListArrayList){
            max = Math.max(max,ListConverter.jobListFromString(jobList.getJobList()).size());
            for (String tag : jobList.getJobListsTags()){
                if (!tags.contains(tag)){
                    tags.add(tag);
                }
            }
        }
        for (int i = 0; i < max; i++){
            for (JobList jobList : jobListArrayList){
                if (i < ListConverter.jobListFromString(jobList.getJobList()).size())
                    listOfJobs.add(ListConverter.jobListFromString(jobList.getJobList()).get(i));
            }
        }
        // repetition how many times to repeat the date
        int repetition = listOfJobs.size() / dateList.size();
        List<Date> results = new ArrayList<>();
        for (Date d : dateList) {
            for (int i = 0; i <= repetition; ++i)
                //create a new bigger list of dates
                results.add(d);
        }
        //insert date to each job in the list
        for(int i = 0; i < listOfJobs.size(); i++){
            String[] parts = results.get(i).toString().split("00:00:00");
            String string  = parts[0];
            listOfJobs.get(i).setJobDate(string.trim());
        }
        JobList myJobList = new JobList();
        myJobList.setJobListFavourite(false);
        myJobList.setJobListsTags(tags.toArray(new String[0]));
        myJobList.setJobListTitle(dateFrom + " - " + dateTo);
        myJobList.setJobListColour(context.getString(R.string.white));
        myJobList.setJobList(ListConverter.stringFromJobList(listOfJobs));
        for (JobList jobList : jobListArrayList){
            jobListRepository.deleteJobList(jobList);
        }
        return myJobList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JobList jobList) {
        super.onPostExecute(jobList);
        if (jobList != null){
            if (ListConverter.jobListFromString(jobList.getJobList()).isEmpty()){
                String bullsheet = "Bullsheet! something went wrong, this can sometimes happen, try again in a minute.";
                Job job = new Job("",bullsheet,"","","","");
                ListConverter.jobListFromString(jobList.getJobList()).add(job);
            }
            JobListRepository jobListRepository = new JobListRepository(weakRefProgressBar.get().getContext());
            jobListRepository.addList(jobList);
        }else{
            //TODO create a joblist
            jobList = new JobList();
            ArrayList<Job> jobArrayList = new ArrayList<>();
            String bullsheet = "Bullsheet! something went wrong, this can sometimes happen, try again in a minute.";
            Job job = new Job("",bullsheet,"","","","");
            jobArrayList.add(job);
            jobList.setJobList(ListConverter.stringFromJobList(jobArrayList));
            jobList.setJobListsTags(new String[]{"EMPTY"});
            jobList.setJobListFavourite(false);
        }

        Context context = weakRefProgressBar.get().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.DATE_FROM, "");
        editor.putString(Constants.DATE_TO, "");
        editor.apply();
        Bitmap b = BitmapFactory.decodeResource(weakRefProgressBar.get().getContext().getResources(), R.drawable.ic_done_white_48dp);
        weakRefProgressBar.get().doneLoadingAnimation(
                weakRefProgressBar.get().getContext().getResources().getColor(R.color.colorPrimary), b);
        Constants.JOB_LISTS_TO_RETURN = 0;
        waitAndChangeActiviy(weakRefProgressBar.get().getContext());
        //weakRefProgressBar.get().revertAnimation();
    }

    public void waitAndChangeActiviy(final Context context){
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString("title", "Job List");
                Intent i = new Intent(context, FinalListActivity.class);
                i.putExtras(bundle);
                context.startActivity(i);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

}
