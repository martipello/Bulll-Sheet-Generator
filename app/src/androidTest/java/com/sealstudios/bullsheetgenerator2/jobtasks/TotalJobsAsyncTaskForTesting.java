package com.sealstudios.bullsheetgenerator2.jobtasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sealstudios.bullsheetgenerator2.objects.Job;
import com.sealstudios.bullsheetgenerator2.objects.JobList;
import com.sealstudios.bullsheetgenerator2.utils.Constants;
import com.sealstudios.bullsheetgenerator2.utils.ListConverter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class TotalJobsAsyncTaskForTesting extends AsyncTask<String, Void, JobList> {

    private long databaseId;
    private String TAG = "TtlJbsAsncTsk";
    private totalJobsGetTaskListener totalJobsGetTaskListener = null;
    Exception exception = null;

    public TotalJobsAsyncTaskForTesting() {
    }

    @Override
    protected JobList doInBackground(String... strings) {
        String siteUrl = strings[0];
        String[] jobTags = new String[]{Constants.TOTAL_JOBS};
        String bullsheet = "Bullsheet! something went wrong with TotalJobs, this can sometimes happen, try again in a minute.";
        JobList jobList = new JobList();
        jobList.setJobListFavourite(false);
        jobList.setJobListsTags(jobTags);
        ArrayList<Job> jobsList = new ArrayList<>();


        System.out.println("async task ");

        Document doc = null;
        try {
            doc = Jsoup.connect(strings[0]).get();
            Log.d(TAG, doc.body().text());
            Elements metaElems = doc.select("div");
            for (Element elements : metaElems){
                System.out.println("text " + elements.text());
                System.out.println("tag " + elements.tag());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Job job = new Job("",bullsheet,"","","","");
            jobsList.add(job);
        }


        jobList.setJobList(ListConverter.stringFromJobList(jobsList));

        if (ListConverter.jobListFromString(jobList.getJobList()).isEmpty()){
            Job job = new Job("","","","","",bullsheet);
            jobsList.add(job);
        }

        jobList.setJobList(ListConverter.stringFromJobList(jobsList));
        //JobListRepository jobListRepository = new JobListRepository(weakRefProgressBar.get().getContext());
        //TODO save this ID somewhere like shared preferences for ProcessJobsAsyncTask
        //databaseId = jobListRepository.addList(jobList);
        //jobListRepository.addList(jobList);

        return jobList;
    }
    public TotalJobsAsyncTaskForTesting  setTotalJobsGetTaskListener(totalJobsGetTaskListener listener){
        this.totalJobsGetTaskListener = listener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JobList jobList) {
        super.onPostExecute(jobList);
        if (this.totalJobsGetTaskListener != null){
            this.totalJobsGetTaskListener.onComplete(jobList,exception);
        }
    }

    @Override
    protected void onCancelled(JobList jobList) {
        super.onCancelled(jobList);
        if (this.totalJobsGetTaskListener != null){
            exception = new InterruptedException("Async cancelled");
            this.totalJobsGetTaskListener.onComplete(null,exception);
        }
    }

    public interface totalJobsGetTaskListener {
        void onComplete(JobList jobList, Exception e);
    }


}
