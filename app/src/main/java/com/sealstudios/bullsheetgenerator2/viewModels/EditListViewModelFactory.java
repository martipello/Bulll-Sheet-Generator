package com.sealstudios.bullsheetgenerator2.viewModels;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.sealstudios.bullsheetgenerator2.EditListActivity;

public class EditListViewModelFactory implements ViewModelProvider.Factory {
    private final int jobId;
    private Application application;

    public EditListViewModelFactory(Class<EditListActivity> editListActivityClass, int jobId) {
        this.jobId = jobId;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EditListViewModel(application, jobId);
    }

}
