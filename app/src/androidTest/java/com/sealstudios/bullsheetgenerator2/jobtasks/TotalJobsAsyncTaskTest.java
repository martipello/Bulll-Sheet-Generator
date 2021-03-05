package com.sealstudios.bullsheetgenerator2.jobtasks;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;

import com.sealstudios.bullsheetgenerator2.objects.JobList;
import com.sealstudios.bullsheetgenerator2.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class TotalJobsAsyncTaskTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getContext();
    }

    @After
    public void tearDown() {
    }

    public void totalJobsGetJobsList(String siteUrl) {
        TotalJobsAsyncTaskForTesting task = new TotalJobsAsyncTaskForTesting();
        task.setTotalJobsGetTaskListener(new TotalJobsAsyncTaskForTesting.totalJobsGetTaskListener(){
            @Override
            public void onComplete(JobList jobList, Exception e) {
                if (jobList != null){
                    System.out.println("totalJobs " + Arrays.toString(jobList.getJobListsTags()));
                    System.out.println("totalJobs " + jobList.getJobList());
                }
                ProcessJobsAsyncTaskForTesting task = new ProcessJobsAsyncTaskForTesting(context);
                task.setProcessJobsGetTaskListener(new ProcessJobsAsyncTaskForTesting.processJobsGetTaskListener(){
                    @Override
                    public void onComplete(JobList jobList, Exception e) {
                        if (jobList != null){
                            System.out.println("processJob " + Arrays.toString(jobList.getJobListsTags()));
                            System.out.println("processJob " + jobList.getJobList());
                        }

                    }
                }).execute();

            }
        }).execute(siteUrl);
    }

    @Test
    public void validateTotalJobs(){
        String location = "WS10 0HX";
        String description = "sales";
        String radius;
        if (!location.isEmpty() && !location.contains(" ")){
            location = new StringBuilder(location).insert(location.length()-3, " ").toString();
        }
        radius = "5";
        String totalJobsUrl = Constants.TOTAL_JOBS_URL;
        String locationUrl = "in-" + location.replaceAll(" ","-") + "?radius=" + radius;
        String descriptionUrl = "jobs/" + description.toLowerCase().trim().replaceAll(" ","-") + "/";
        String siteUrl;
        /*
        <string name="totalJobPostCode">in-%s?radius=%s</string>
        <string name="totalJobDescription">jobs/%s/</string>
        <string name="totalJobDescriptionEmpty">jobs/</string>
         */
        if (description.trim().isEmpty() &&
                description.length() < 1 &&
                location.trim().isEmpty() &&
                location.length() < 1){
            siteUrl = totalJobsUrl + "jobs/" + locationUrl;
            try {
                totalJobsGetJobsList(siteUrl);
                System.out.println("1 " + siteUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if (description.trim().isEmpty() && description.length() < 1){
            siteUrl = totalJobsUrl + "jobs/" + locationUrl;
            try {
                totalJobsGetJobsList(siteUrl);
                System.out.println("2 " +  siteUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if (location.trim().isEmpty() && location.length() < 1){
            siteUrl = totalJobsUrl + descriptionUrl;
            try {
                totalJobsGetJobsList(siteUrl);
                System.out.println("3 " + siteUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            siteUrl = totalJobsUrl + descriptionUrl + locationUrl;
            System.out.println("4 " +  siteUrl);
            try {
                totalJobsGetJobsList(siteUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}