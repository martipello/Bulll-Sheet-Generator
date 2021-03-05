package com.sealstudios.bullsheetgenerator2;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.sealstudios.bullsheetgenerator2.adapters.ArchiveAdapter;
import com.sealstudios.bullsheetgenerator2.objects.Job;
import com.sealstudios.bullsheetgenerator2.objects.JobList;
import com.sealstudios.bullsheetgenerator2.utils.Constants;
import com.sealstudios.bullsheetgenerator2.utils.ListConverter;
import com.sealstudios.bullsheetgenerator2.utils.OnStartDragListener;
import com.sealstudios.bullsheetgenerator2.utils.SimpleItemTouchHelperCallback;
import com.sealstudios.bullsheetgenerator2.viewModels.ArchiveViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity implements
        OnStartDragListener {
    public static int selected = 0;
    public static ArrayList<JobList> myJoblist;
    public RecyclerView recyclerView;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    public TextView frame_text;
    public Menu myMenu;
    private Boolean cancelRunnable;
    private ItemTouchHelper mItemTouchHelper;
    private StaggeredGridLayoutManager staggeredGridLayoutManagerVertical;
    public ArchiveAdapter cardAdapter;
    private SparseBooleanArray selectedItems;
    private ArchiveViewModel viewModel;
    private String TAG = "ArchvAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archivestaggeredgrid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Archives");
        frame_text = findViewById(R.id.frameText);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        //mInterstitialAd.setAdUnitId(getString(R.string.test_ad));
        selectedItems = new SparseBooleanArray();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequestBanner = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequestBanner);
        recyclerView = findViewById(R.id.list_view);
        setmItemTouchHelper(false);
        myJoblist = new ArrayList<>();
        //INSTANTIATE VIEW MODEL AND POPULATE LIST
        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardClick(View view, int position) {
                if (cardAdapter.getSelectedItemCount() < 1) {
                    JobList jobList = cardAdapter.getList().get(position);
                    Intent intent = new Intent(ArchiveActivity.this, EditListActivity.class);
                    intent.putExtra(Constants.JOB_ID,jobList.getJobId());
                    startActivity(intent);
                } else {
                    cardAdapter.toggleSelection(position);
                    if (cardAdapter.getSelectedItemCount() < 1) {
                        hideShowMenu(false);
                    }
                }
            }
            @Override
            public void onCardLongClick(View view, int position) {
                //clearGrid();
                if (cardAdapter.getSelectedItemCount() < 1) {
                    cardAdapter.toggleSelection(position);
                    setmItemTouchHelper(true);
                    hideShowMenu(true);
                }
            }
        };
        OnFavouriteTouchListener favouriteTouchListener = new OnFavouriteTouchListener() {
            @Override
            public void onCardClick(View view, int position) {
                JobList jobList = cardAdapter.getList().get(position);
                jobList.setJobListFavourite(!jobList.isJobListFavourite());
                viewModel.updateMyJobList(jobList);
            }
        };
        staggeredGridLayoutManagerVertical = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.grid_columns), StaggeredGridLayoutManager.VERTICAL);
        cardAdapter = new ArchiveAdapter(myJoblist, ArchiveActivity.this,
                itemTouchListener,
                favouriteTouchListener,
                selectedItems,
                ArchiveActivity.this);
        recyclerView.setLayoutManager(staggeredGridLayoutManagerVertical);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
        checkText(frame_text);
        viewModel = ViewModelProviders.of(this).get(ArchiveViewModel.class);
        viewModel.getLiveJobLists().observe(this, new Observer<List<JobList>>() {
            @Override
            public void onChanged(@Nullable List<JobList> jobLists) {
                myJoblist.clear();
                myJoblist.addAll(jobLists);
                cardAdapter.refreshMyList(myJoblist);
                checkText(frame_text);
            }
        });
    }

    public void hideShowMenu(boolean isChecked) {
        myMenu.findItem(R.id.delete).setVisible(isChecked);
        myMenu.findItem(R.id.delete).setEnabled(isChecked);
        myMenu.findItem(R.id.add).setVisible(!isChecked);
        myMenu.findItem(R.id.add).setEnabled(!isChecked);
        myMenu.findItem(R.id.merge).setVisible(isChecked);
        myMenu.findItem(R.id.merge).setEnabled(isChecked);
    }

    public void undoSnackbar(final List<JobList> jobListList, final int[] positions){
        Snackbar snackbar = Snackbar.make(recyclerView,"Item removed.",Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> {
            for (JobList jobList : jobListList){
                viewModel.insertMyJobList(jobList);
            }
        });
        snackbar.setActionTextColor(ArchiveActivity.this.getResources().getColor(R.color.colorAccent));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ArchiveActivity.this.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    public JobList merge(List<JobList> jobListList) {
        JobList mergedJobList = new JobList();
        int max = 0;
        ArrayList<Job> mergedList = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        for (JobList jobList : jobListList){
            max = Math.max(max,ListConverter.jobListFromString(jobList.getJobList()).size());
            for (String tag : jobList.getJobListsTags()){
                if (!tags.contains(tag)){
                    tags.add(tag);
                }
            }
        }
        for (int i = 0; i < max; i++){
            for (JobList jobList : jobListList){
                if (i < ListConverter.jobListFromString(jobList.getJobList()).size())
                    mergedList.add(ListConverter.jobListFromString(jobList.getJobList()).get(i));
            }
        }
        mergedJobList.setJobListTitle("Title");
        mergedJobList.setJobListFavourite(false);
        mergedJobList.setJobListsTags(tags.toArray(new String[0]));
        mergedJobList.setJobListColour(this.getString(R.string.white));
        mergedJobList.setJobList(ListConverter.stringFromJobList(mergedList));
        return mergedJobList;
    }

    public void setmItemTouchHelper(boolean helperBool) {
        if(!helperBool){
            ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(cardAdapter, Constants.moveTypeArchiveActivity);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);
        }else{
            ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(cardAdapter, Constants.moveTypeArchiveActivitySelected);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    public void checkText(TextView textView) {
        if (myJoblist.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
    }

    public interface OnItemTouchListener {
        void onCardClick(View view, int position);

        void onCardLongClick(View view, int position);
    }

    public interface OnFavouriteTouchListener {
        void onCardClick(View view, int position);
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

    public void progressInvisible(final View v) {
        runOnUiThread(() -> v.setVisibility(View.INVISIBLE));
    }

    public void progressVisible(final View v) {
        runOnUiThread(() -> v.setVisibility(View.VISIBLE));
    }
    //progressBar.setVisibility(View.VISIBLE);

    public void confirmMerge(){
        int getSelectedItemCount = cardAdapter.getSelectedItemCount();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setCancelable(true);
        if (getSelectedItemCount < 2) {
            builder1.setMessage(R.string.cant_merge_lists);
            builder1.setPositiveButton(
                    R.string.ok,
                    (dialog, id) -> dialog.dismiss());
        } else {
            String mergeString = getString(R.string.merge_lists, cardAdapter.getSelectedItemCount());
            builder1.setMessage(mergeString);
            //There are enough selected ask for confirmation
            builder1.setPositiveButton(
                    R.string.yes,
                    (dialog, id) -> {
                        viewModel.insertMyJobList(merge(cardAdapter.getSelectedItems()));
                        cardAdapter.clearSelections();
                        hideShowMenu(false);
                    });
            builder1.setNegativeButton(
                    R.string.no,
                    (dialog, id) -> dialog.cancel());
        }
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void delete(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        if (cardAdapter.getSelectedItemCount() < 2) {
            builder1.setMessage(R.string.delete_one);
        } else {
            builder1.setMessage(R.string.delete_many);
        }
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                R.string.yes,
                (dialog, id) -> {
                    //NavUtils.navigateUpFromSameTask(EditListActivity.this);
                    int[] selectedPositions = cardAdapter.getSelectedItemsPositions();
                    ArrayList<JobList> jobListArrayList = new ArrayList<>(cardAdapter.getSelectedItems());
                    cardAdapter.clearSelections();
                    for (JobList jobList : jobListArrayList){
                        viewModel.deleteMyJobList(jobList);
                    }
                    undoSnackbar(jobListArrayList,selectedPositions);
                    checkText(frame_text);
                    hideShowMenu(false);
                });
        builder1.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public JobList createJobList(){
        JobList jobList = new JobList();
        jobList.setJobListTitle(getString(R.string.title));
        jobList.setJobListFavourite(false);
        jobList.setJobListsTags(new String[]{""});
        jobList.setJobListColour(getString(R.string.white));
        Date date = new Date();
        String[] parts = date.toString().split("00:00:00");
        String string  = parts[0];
        Job job = new Job("Seal Studios","Bullsheet Generator","UK","Applied","www.sealstudios.com",string.trim());
        ArrayList<Job> jobArrayList = new ArrayList<>();
        jobArrayList.add(job);
        jobList.setJobList(ListConverter.stringFromJobList(jobArrayList));
        return jobList;
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

        getMenuInflater().inflate(R.menu.archive_menu_main, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.merge: {
                confirmMerge();
                return true;
            }
            case R.id.delete: {
                delete();
                return true;
            }
            case R.id.add: {
                viewModel.insertMyJobList(createJobList());
                checkText(frame_text);
                return true;
            }
            case R.id.preferences: {
                Intent myIntent = new Intent(ArchiveActivity.this, SettingsActivity.class);
                startActivity(myIntent);
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
                Intent myIntent = new Intent(ArchiveActivity.this, about.class);
                startActivity(myIntent);
                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }
}