package com.sealstudios.bullsheetgenerator2;


import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ShareCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.andremion.counterfab.CounterFab;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.sealstudios.bullsheetgenerator2.adapters.JobAdapter;
import com.sealstudios.bullsheetgenerator2.database.JobListRepository;
import com.sealstudios.bullsheetgenerator2.objects.Job;
import com.sealstudios.bullsheetgenerator2.objects.JobList;
import com.sealstudios.bullsheetgenerator2.utils.Constants;
import com.sealstudios.bullsheetgenerator2.utils.ListConverter;
import com.sealstudios.bullsheetgenerator2.utils.OnStartDragListener;
import com.sealstudios.bullsheetgenerator2.utils.SimpleItemTouchHelperCallback;
import com.sealstudios.bullsheetgenerator2.viewModels.FinalListViewModel;

import java.util.ArrayList;

public class FinalListActivity extends AppCompatActivity implements
        OnStartDragListener {

    private String TAG = "FnlLstActvty";
    public RecyclerView recyclerView;
    InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private Boolean cancelRunnable;
    private CoordinatorLayout coordinatorLayout;
    private static ItemTouchHelper mItemTouchHelper;
    private FinalListViewModel viewModel;
    public JobAdapter jobAdapter;
    private JobList myJoblist;
    public String title;
    CounterFab fab;
    private static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Job List");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequestBanner = new AdRequest.Builder().build();
        mAdView.loadAd(adRequestBanner);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            title = getIntent().getExtras().getString("title");
        }
        coordinatorLayout = findViewById(R.id.coordinator);
        recyclerView = findViewById(R.id.list_view);
        final OnItemTouchListener itemTouchListener = new FinalListActivity.OnItemTouchListener() {
            @Override
            public void onCardClick(View view, int position) {
                ArrayList<Job> jobArrayList = new ArrayList<>(jobAdapter.getList());
                Job job = jobArrayList.get(position);
                jobArrayList.remove(position);
                myJoblist.setJobList(ListConverter.stringFromJobList(jobArrayList));
                viewModel.updateMyJobList(myJoblist);
                undoSnackbar(job,position);
            }

            @Override
            public void onCardLongClick(View view, int position) {
                //clearGrid();
            }
        };
        viewModel = ViewModelProviders.of(this).get(FinalListViewModel.class);
        viewModel.getLiveJobList().observe(this, new Observer<JobList>() {
            @Override
            public void onChanged(@Nullable JobList jobList) {
                if (jobList != null) {
                    myJoblist = jobList;
                    if (jobAdapter == null) {
                        jobAdapter = new JobAdapter(ListConverter.jobListFromString(jobList.getJobList()),
                                FinalListActivity.this,
                                itemTouchListener,
                                FinalListActivity.this);
                        jobAdapter.setSwipeListener(new JobAdapter.SwipeListener() {
                            @Override
                            public void onComplete(int position) {
                                ArrayList<Job> jobArrayList = new ArrayList<>(jobAdapter.getList());
                                Job job = jobArrayList.get(position);
                                jobArrayList.remove(position);
                                myJoblist.setJobList(ListConverter.stringFromJobList(jobArrayList));
                                viewModel.updateMyJobList(myJoblist);
                                undoSnackbar(job,position);
                            }
                        });
                        recyclerView.setAdapter(jobAdapter);
                        ItemTouchHelper.Callback callback =
                                new SimpleItemTouchHelperCallback(jobAdapter, Constants.moveTypeFinalActivity);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FinalListActivity.this,
                                LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        mItemTouchHelper = new ItemTouchHelper(callback);
                        mItemTouchHelper.attachToRecyclerView(recyclerView);
                        count = jobAdapter.getItemCount();
                        fab.setCount(getCount());
                    }
                } else {
                    Log.d(TAG, "list is null");
                }
            }
        });
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        showInterstitial();
                    }

                    @Override
                    public void onAdClosed() {
                        ShareCompat.IntentBuilder
                                .from(FinalListActivity.this) // getActivity() or activity field if within Fragment
                                .setText(jobAdapter.getList().toString().replace("[", "").replace("]", "").replace(",", "\n"))
                                .setType("text/plain") // most general text sharing MIME type
                                .setChooserTitle("Send via")
                                .startChooser();
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                    }

                    @Override
                    public void onAdLeftApplication() {
                    }

                    @Override
                    public void onAdOpened() {
                    }
                });
            }
        });
    }

    public void undoSnackbar(final Job job, final int position) {
        cancelRunnable = false;
        Snackbar snackbar = Snackbar.make(recyclerView, "Item removed.", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Job> jobArrayList = new ArrayList<>(ListConverter.jobListFromString(myJoblist.getJobList()));
                jobArrayList.add(position,job);
                myJoblist.setJobList(ListConverter.stringFromJobList(jobArrayList));
                viewModel.updateMyJobList(myJoblist);
            }
        });
        snackbar.setActionTextColor(FinalListActivity.this.getResources().getColor(R.color.colorAccent));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(FinalListActivity.this.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    public interface OnItemTouchListener {
        void onCardClick(View view, int position);

        void onCardLongClick(View view, int position);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public static void setCount(int myCount) {
        count = myCount;
    }

    private int getCount() {
        return count;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.save: {
                //save the list
                if (!jobAdapter.getList().isEmpty()) {
                    myJoblist.setJobList(ListConverter.stringFromJobList(jobAdapter.getList()));
                    viewModel.updateMyJobList(myJoblist);
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Saved to archives", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    //TODO add action to snackbar to view list
                } else {
                    Snackbar.make(recyclerView, R.string.empty_list, Snackbar.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.archive: {
                Intent myIntent = new Intent(FinalListActivity.this, ArchiveActivity.class);
                startActivity(myIntent);
                return true;
            }
            case R.id.preferences: {
                Intent myIntent = new Intent(FinalListActivity.this, SettingsActivity.class);
                startActivity(myIntent);

                return true;
            }
            case R.id.delete: {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Are you sure you want to delete this list");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                JobListRepository jobListRepository = new JobListRepository(FinalListActivity.this);
                                jobListRepository.deleteJobList(myJoblist);
                                ArrayList<Job> jobs = new ArrayList<>();
                                jobAdapter.refreshMyList(jobs);
                            }
                        });
                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();

                return true;
            }
            case R.id.share: {
                int applicationNameId = this.getApplicationInfo().labelRes;
                final String appPackageName = this.getPackageName();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, this.getString(applicationNameId));
                String text = "Try Bull Sheet Generator ";
                String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
                i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
                startActivity(Intent.createChooser(i, "Share link:"));
                return true;
            }
            case R.id.about: {
                Intent myIntent = new Intent(FinalListActivity.this, about.class);
                startActivity(myIntent);
                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }
}