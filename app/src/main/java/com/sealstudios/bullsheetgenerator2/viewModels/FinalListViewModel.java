package com.sealstudios.bullsheetgenerator2.viewModels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.sealstudios.bullsheetgenerator2.database.JobListRepository;
import com.sealstudios.bullsheetgenerator2.objects.JobList;


public class FinalListViewModel extends AndroidViewModel {
    private JobListRepository jobListRepository;
    private LiveData<JobList> jobList;

    public FinalListViewModel(Application application){
        super(application);
        jobListRepository = new JobListRepository(application);
        jobList = jobListRepository.getJobListWithLimit(1);
    }

    public LiveData<JobList> getLiveJobList(){
        return jobList;
    }

    public LiveData<JobList> getLiveJobListById(){
        return jobList;
    }

    public void insertMyJobList(JobList jobList){
        jobListRepository.addList(jobList);
    }

    public void updateMyJobList(JobList jobList){
        jobListRepository.updateList(jobList);
    }

    public void deleteMyJobList(JobList jobList){
        jobListRepository.deleteJobList(jobList);
    }
}

