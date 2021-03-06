package com.sealstudios.bullsheetgenerator2;


import android.app.Dialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.andremion.counterfab.CounterFab;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.sealstudios.bullsheetgenerator2.adapters.EditListAdapter;
import com.sealstudios.bullsheetgenerator2.database.JobListRepository;
import com.sealstudios.bullsheetgenerator2.objects.Job;
import com.sealstudios.bullsheetgenerator2.objects.JobList;
import com.sealstudios.bullsheetgenerator2.utils.Constants;
import com.sealstudios.bullsheetgenerator2.utils.ListConverter;
import com.sealstudios.bullsheetgenerator2.utils.OnStartDragListener;
import com.sealstudios.bullsheetgenerator2.utils.SimpleItemTouchHelperCallback;
import com.sealstudios.bullsheetgenerator2.viewModels.EditListViewModel;
import com.sealstudios.bullsheetgenerator2.viewModels.EditListViewModelFactory;

import java.util.ArrayList;

public class EditListActivity extends AppCompatActivity implements
        OnStartDragListener {

    public static ArrayList<String> list = new ArrayList<>();
    public static RecyclerView recyclerView;
    InterstitialAd mInterstitialAd;
    private AdView mAdView;
    public String backgroundColour;
    private ArrayList<String> finalListWithDates;
    private static ItemTouchHelper mItemTouchHelper;
    private JobList myJobList;
    public EditListAdapter jobAdapter;
    public ConstraintLayout background;
    private EditListViewModel viewModel;
    CounterFab fab;
    private int jobId;
    private boolean cancelRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Job List");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequestBanner = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequestBanner);
        background = findViewById(R.id.background);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.list_view);
        Intent intent = getIntent();
        jobId = intent.getIntExtra(Constants.JOB_ID,0);
        viewModel = ViewModelProviders.of(this, new EditListViewModelFactory(EditListActivity.class,jobId)).get(EditListViewModel.class);
        viewModel.getLiveJobListById().observe(this, new Observer<JobList>() {
            @Override
            public void onChanged(@Nullable JobList jobList) {
                myJobList = jobList;
                assert myJobList != null;
                backgroundColour = myJobList.getJobListColour();
                background.setBackgroundColor(Color.parseColor(backgroundColour));
                jobAdapter.refreshMyList(ListConverter.jobListFromString(myJobList.getJobList()));
            }
        });
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Job job : ListConverter.jobListFromString(myJobList.getJobList())){
                    stringBuilder.append(job.getJobDate());
                    stringBuilder.append("\n");
                    stringBuilder.append(job.getJobDescription());
                    stringBuilder.append("\n");
                    stringBuilder.append(job.getCompany());
                    stringBuilder.append("\n");
                    stringBuilder.append(job.getJobLocation());
                    stringBuilder.append("\n");
                    stringBuilder.append(job.getApplicationType());
                    stringBuilder.append("\n");
                    stringBuilder.append(job.getJobUrl());
                    stringBuilder.append("\n");
                    stringBuilder.append("\n");
                }
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                // Load ads into Interstitial Ads
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        showInterstitial();
                    }
                    @Override
                    public void onAdClosed() {
                        ShareCompat.IntentBuilder
                                .from(EditListActivity.this) // getActivity() or activity field if within Fragment
                                .setText(stringBuilder.toString())
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EditListActivity.this,
                LinearLayoutManager.VERTICAL, false);
        OnItemTouchListener itemTouchListener = new EditListActivity.OnItemTouchListener() {
            @Override
            public void onCardClick(View view, int position) {
                ArrayList<Job> jobArrayList = new ArrayList<>(jobAdapter.getList());
                Job job = jobArrayList.get(position);
                jobArrayList.remove(position);
                myJobList.setJobList(ListConverter.stringFromJobList(jobArrayList));
                viewModel.updateMyJobList(myJobList);
                undoSnackbar(job,position);
            }
            @Override
            public void onCardLongClick(View view, int position) {
                //clearGrid();
            }
        };
        ArrayList<Job> jobArrayList = new ArrayList<>();
        jobAdapter = new EditListAdapter(jobArrayList,this,itemTouchListener,this,getString(R.string.white));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(jobAdapter);
        fab.setCount(jobAdapter.getItemCount());
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(jobAdapter, Constants.moveTypeFinalActivity);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        jobAdapter.setSwipeListener(new EditListAdapter.SwipeListener() {
            @Override
            public void onComplete(int position) {
                ArrayList<Job> jobArrayList = new ArrayList<>(jobAdapter.getList());
                Job job = jobArrayList.get(position);
                jobArrayList.remove(position);
                myJobList.setJobList(ListConverter.stringFromJobList(jobArrayList));
                viewModel.updateMyJobList(myJobList);
                undoSnackbar(job,position);
            }
        });
    }

    public void undoSnackbar(final Job job, final int position){
        Snackbar snackbar = Snackbar.make(recyclerView,"Item removed.",Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Job> jobArrayList = new ArrayList<>(ListConverter.jobListFromString(myJobList.getJobList()));
                jobArrayList.add(position,job);
                myJobList.setJobList(ListConverter.stringFromJobList(jobArrayList));
                viewModel.updateMyJobList(myJobList);
            }
        });
        snackbar.setActionTextColor(EditListActivity.this.getResources().getColor(R.color.colorAccent));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(EditListActivity.this.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    public interface OnItemTouchListener {
        public void onCardClick(View view, int position);
        public void onCardLongClick(View view, int position);
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

    public class ViewDialog implements View.OnClickListener{
        final Dialog dialog = new Dialog(EditListActivity.this);
        @Override
        public void onClick(View v) {
            myJobList.setJobListColour(v.getTag().toString());
            viewModel.updateMyJobList(myJobList);
            dialog.dismiss();
        }
        public void showDialog(){
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.TOP;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            //wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            final Button orange,red,yellow,green,white,pink,purple,blue;
            Button cancel;
            orange = dialog.findViewById(R.id.orange);
            orange.setOnClickListener(this);
            red = dialog.findViewById(R.id.red);
            red.setOnClickListener(this);
            yellow = dialog.findViewById(R.id.yellow);
            yellow.setOnClickListener(this);
            green = dialog.findViewById(R.id.green);
            green.setOnClickListener(this);
            white = dialog.findViewById(R.id.white);
            white.setOnClickListener(this);
            pink = dialog.findViewById(R.id.pink);
            pink.setOnClickListener(this);
            purple = dialog.findViewById(R.id.purple);
            purple.setOnClickListener(this);
            blue = dialog.findViewById(R.id.blue);
            blue.setOnClickListener(this);
            cancel = (Button)dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

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

        getMenuInflater().inflate(R.menu.edit_menu_main, menu);

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Are you sure you want to exit without saving");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                NavUtils.navigateUpFromSameTask(EditListActivity.this);
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

            case R.id.save: {
                viewModel.updateMyJobList(myJobList);
                finish();
                return true;
            }
            case R.id.add: {
                Job job = new Job("","","","","","");
                ArrayList<Job> jobArrayList = new ArrayList<>(ListConverter.jobListFromString(myJobList.getJobList()));
                jobArrayList.add(0,job);
                myJobList.setJobList(ListConverter.stringFromJobList(jobArrayList));
                viewModel.updateMyJobList(myJobList);
                return true;
            }
            case R.id.preferences: {
                Intent myIntent = new Intent(EditListActivity.this, SettingsActivity.class);
                startActivity(myIntent);
                return true;
            }
            case R.id.colour: {
                //System.out.println("button");
                ViewDialog alert = new ViewDialog();
                alert.showDialog();
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
                                JobListRepository jobListRepository = new JobListRepository(EditListActivity.this);
                                jobListRepository.deleteJobList(myJobList);
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

                Intent myIntent = new Intent(EditListActivity.this, About.class);
                startActivity(myIntent);

                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }
}