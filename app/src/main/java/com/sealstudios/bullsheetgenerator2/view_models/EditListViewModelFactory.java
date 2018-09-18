package com.sealstudios.bullsheetgenerator2.view_models;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

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
