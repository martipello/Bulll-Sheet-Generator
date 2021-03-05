package com.sealstudios.bullsheetgenerator2.viewModels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sealstudios.bullsheetgenerator2.database.JobListRepository;
import com.sealstudios.bullsheetgenerator2.objects.JobList;

import java.util.List;


public class ArchiveViewModel extends AndroidViewModel {
    private JobListRepository jobListRepository;
    private LiveData<List<JobList>> jobList;
    private MutableLiveData<String> tag;

    public ArchiveViewModel(Application application){
        super(application);
        tag = new MutableLiveData<>();
        jobListRepository = new JobListRepository(application);
        jobList = jobListRepository.getLists();
        /*
        jobList = Transformations.switchMap(tag, myTag ->
                jobListRepository.getJobListByTag(myTag));
                */
    }

    public LiveData<List<JobList>> getLiveJobLists(){
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

