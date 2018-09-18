package com.sealstudios.bullsheetgenerator2.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sealstudios.bullsheetgenerator2.ArchiveActivity;
import com.sealstudios.bullsheetgenerator2.R;
import com.sealstudios.bullsheetgenerator2.objects.JobList;
import com.sealstudios.bullsheetgenerator2.utils.Constants;
import com.sealstudios.bullsheetgenerator2.utils.ItemTouchHelperAdapter;
import com.sealstudios.bullsheetgenerator2.utils.ListConverter;
import com.sealstudios.bullsheetgenerator2.utils.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<JobList> jobListArrayList;
    private OnStartDragListener mDragStartListener;
    private ArchiveActivity.OnItemTouchListener onItemTouchListener;
    private ArchiveActivity.OnFavouriteTouchListener favouriteTouchListener;
    private Context context;
    private SparseBooleanArray selectedItems;
    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_MEDIUM = 2;
    private static final int VIEW_TYPE_LARGE = 3;
    private String TAG = "ArchvAdptr";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardBack;
        public FrameLayout fade;
        public ImageButton favourite;
        public ConstraintLayout constraintLayout;
        public TextView title;
        public TextView list;

        public MyViewHolder(View view) {
            super(view);
            Log.d(TAG,"MY_VIEW_HOLDER");
            cardBack = view.findViewById(R.id.cardBack);
            title = view.findViewById(R.id.title);
            list = view.findViewById(R.id.list);
            fade = view.findViewById(R.id.fade);
            favourite = view.findViewById(R.id.favourite);
            constraintLayout = view.findViewById(R.id.constraint_layout);
            favourite.setOnClickListener(v -> favouriteTouchListener.onCardClick(v,getAdapterPosition()));
            cardBack.setOnClickListener(v -> onItemTouchListener.onCardClick(v, getAdapterPosition()));
            cardBack.setOnLongClickListener(v -> {
                onItemTouchListener.onCardLongClick(v, getAdapterPosition());
                return false;
            });
        }
    }

    public ArchiveAdapter(ArrayList<JobList> cardMakerList, OnStartDragListener dragStartListener,
                          ArchiveActivity.OnItemTouchListener onItemTouchListener,
                          ArchiveActivity.OnFavouriteTouchListener onFavTouchListener,
                          SparseBooleanArray selectedItems, Context context) {
        this.jobListArrayList = cardMakerList;
        this.mDragStartListener = dragStartListener;
        this.onItemTouchListener = onItemTouchListener;
        this.favouriteTouchListener = onFavTouchListener;
        this.selectedItems = selectedItems;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        JobList jobList = getList().get(position);
        int size = ListConverter.jobListFromString(jobList.getJobList()).size();
        if (size < 30) {
            return VIEW_TYPE_SMALL;
        } else if (size < 50) {
            return VIEW_TYPE_MEDIUM;
        }else {
            return VIEW_TYPE_LARGE;
        }
    }


    @NonNull
    @Override
    public ArchiveAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_SMALL) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.archive_view_holder_small, parent, false);
            return new MyViewHolder(itemView);
        } else if(viewType == VIEW_TYPE_MEDIUM) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.archive_view_holder_med, parent, false);
            return new MyViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.archive_view_holder_large, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveAdapter.MyViewHolder holder, int position) {
        JobList archiveObjects = getList().get(position);
        if (archiveObjects.getJobListTitle() != null){
            holder.title.setText(archiveObjects.getJobListTitle().trim().replaceAll("-" , " "));
        }else{
            holder.title.setText(context.getString(R.string.title));
        }
        holder.list.setText(archiveObjects.getJobList().replaceAll("[(){}\"\\[\\]]", "").replaceAll(":"," - ").replaceAll(",","\n"));
        holder.cardBack.setCardBackgroundColor(Color.parseColor(archiveObjects.getJobListColour()));
        if (archiveObjects.isJobListFavourite()){
            holder.favourite.setBackground(context.getResources().getDrawable(R.drawable.ic_pin_black_24dp));
        }else{
            holder.favourite.setBackground(context.getResources().getDrawable(R.drawable.ic_pin_outline_black_24dp));
        }
        if (selectedItems.get(position, false)) {
            holder.fade.setVisibility(View.VISIBLE);
            holder.fade.setBackgroundColor(ResourcesCompat.getColor(holder.itemView.getResources(), R.color.cardview_shadow_start_color, null));
            holder.favourite.setVisibility(View.INVISIBLE);
        } else {
            holder.fade.setVisibility(View.INVISIBLE);
            holder.favourite.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return getList().size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(getList(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(getList(), i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        getList().remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(JobList jobList, int position){
        getList().add(position,jobList);
        notifyItemInserted(position);
    }

    public ArrayList<JobList> getList(){
        return jobListArrayList;
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void onItemRemoved(int position) {
        getList().remove(position);
        notifyItemRemoved(position);
    }

    public List<JobList> getSelectedItems() {
        List<JobList> items = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(getList().get(selectedItems.keyAt(i)));
        }
        return items;
    }

    public int[] getSelectedItemsPositions(){
        int[] itemPositions = new int[getSelectedItemCount()];
        for (int i = 0; i < getSelectedItemCount(); i++) {
            itemPositions[i] = selectedItems.keyAt(i);
        }
        return itemPositions;
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void refreshMyList(ArrayList<JobList> list) {
        ArrayList<JobList> jobListList = new ArrayList<>(list);
        Log.d(TAG,"refreshMyList " + list.size());
        jobListArrayList.clear();
        jobListArrayList.addAll(jobListList);
        Log.d(TAG,"refreshMyList " + list.size());
        this.notifyDataSetChanged();
    }

    public void removeSelections() {
        this.notifyDataSetChanged();
        clearSelections();
    }
}

