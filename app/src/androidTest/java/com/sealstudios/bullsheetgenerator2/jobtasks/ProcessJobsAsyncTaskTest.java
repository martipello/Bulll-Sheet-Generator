package com.sealstudios.bullsheetgenerator2.jobtasks;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;

import com.sealstudios.bullsheetgenerator2.objects.JobList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class ProcessJobsAsyncTaskTest {

    private Context context;
    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void processJobsGetJobsList() {
        ProcessJobsAsyncTaskForTesting task = new ProcessJobsAsyncTaskForTesting(context);
        task.setProcessJobsGetTaskListener(new ProcessJobsAsyncTaskForTesting.processJobsGetTaskListener(){
            @Override
            public void onComplete(JobList jobList, Exception e) {
                System.out.println(Arrays.toString(jobList.getJobListsTags()));
                System.out.println(jobList.getJobList());

            }
        }).execute();
    }
}