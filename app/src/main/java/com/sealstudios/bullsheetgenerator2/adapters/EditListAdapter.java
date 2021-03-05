package com.sealstudios.bullsheetgenerator2.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.sealstudios.bullsheetgenerator2.EditListActivity;
import com.sealstudios.bullsheetgenerator2.R;
import com.sealstudios.bullsheetgenerator2.objects.Job;
import com.sealstudios.bullsheetgenerator2.utils.ItemTouchHelperAdapter;
import com.sealstudios.bullsheetgenerator2.utils.MyMovementMethod;
import com.sealstudios.bullsheetgenerator2.utils.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditListAdapter extends RecyclerView.Adapter<EditListAdapter.MyViewHolder> implements ItemTouchHelperAdapter {
    private List<Job> cardMakerList;
    private OnStartDragListener mDragStartListener;
    private EditListActivity.OnItemTouchListener onItemTouchListener;
    private Context context;
    private SwipeListener swipeListener = null;
    public String colour;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public EditText jobDateText, jobDescriptionText, jobCompanyText, jobLocationText, jobUrlText, jobApplied;
        public ImageView close;
        public ImageView handleView;
        public ConstraintLayout cardBack;
        public CardView cardView;

        public MyViewHolder(View view){
            super(view);
            cardBack = view.findViewById(R.id.card_back);
            cardView = view.findViewById(R.id.card);
            close = view.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardClick(v, getAdapterPosition());
                }
            });
            jobDateText = view.findViewById(R.id.dateHolder);
            jobDateText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    int position = Integer.parseInt(close.getTag().toString());
                    cardMakerList.get(position).setJobDate(s.toString());
                }
            });
            jobDescriptionText = view.findViewById(R.id.job_description);
            jobDescriptionText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    int position = Integer.parseInt(close.getTag().toString());
                    cardMakerList.get(position).setJobDescription(s.toString());
                }
            });
            jobCompanyText = view.findViewById(R.id.job_company);
            jobCompanyText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    int position = Integer.parseInt(close.getTag().toString());
                    cardMakerList.get(position).setCompany(s.toString());
                }
            });
            jobLocationText = view.findViewById(R.id.job_location);
            jobLocationText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    int position = Integer.parseInt(close.getTag().toString());
                    cardMakerList.get(position).setJobLocation(s.toString());
                }
            });
            jobApplied = view.findViewById(R.id.applied);
            jobApplied.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    int position = Integer.parseInt(close.getTag().toString());
                    cardMakerList.get(position).setApplicationType(s.toString());
                }
            });
            jobUrlText = view.findViewById(R.id.job_url);
            jobUrlText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    int position = Integer.parseInt(close.getTag().toString());
                    Linkify.addLinks(s, Linkify.WEB_URLS);
                    cardMakerList.get(position).setJobUrl(s.toString());
                }
            });
            jobUrlText.setLinksClickable(true);
            jobUrlText.setAutoLinkMask(Linkify.WEB_URLS);
            jobUrlText.setMovementMethod(MyMovementMethod.getInstance());
            Linkify.addLinks(jobUrlText, Linkify.WEB_URLS);
            handleView = view.findViewById(R.id.handle);
        }

    }

    public EditListAdapter(List<Job> cardMakerList,
                      OnStartDragListener dragStartListener,
                      EditListActivity.OnItemTouchListener onItemTouchListener,
                      Context context, String colour){
        this.cardMakerList = cardMakerList;
        this.mDragStartListener = dragStartListener;
        this.onItemTouchListener = onItemTouchListener;
        this.context = context;
        this.colour = colour;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition){
            for (int i = fromPosition; i < toPosition; i++){
                Collections.swap(cardMakerList,i,i+1);
            }
        }else{
            for (int i = fromPosition; i > toPosition; i--){
                Collections.swap(cardMakerList,i,i-1);
            }
        }
        notifyItemMoved(fromPosition,toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        cardMakerList.remove(position);
        notifyItemRemoved(position);
        if (this.swipeListener != null){
            this.swipeListener.onComplete(position);
        }
    }

    public void onItemRemoved(int position) {
        cardMakerList.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<Job> getList(){
        ArrayList<Job> jobArrayList = new ArrayList<>(cardMakerList);
        return jobArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_holder, parent, false);
        return new MyViewHolder(itemView);
    }

    public void addItem(Job job, int position){
        cardMakerList.add(position,job);
        notifyItemInserted(position);
    }

    public void refreshMyList(List<Job> list){
        this.cardMakerList.clear();
        this.cardMakerList.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Job job = cardMakerList.get(position);
        holder.close.setTag(position);
        holder.jobDateText.setText(job.getJobDate());
        holder.jobDescriptionText.setText(job.getJobDescription());
        holder.jobCompanyText.setText(job.getCompany());
        holder.jobLocationText.setText(job.getJobLocation());
        holder.jobUrlText.setText(job.getJobUrl());
        holder.jobApplied.setText(job.getApplicationType());
        holder.cardView.setCardBackgroundColor(Color.parseColor(colour));
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (MotionEventCompat.getActionMasked(event)){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Adptr" , "ACTION_DOWN");
                        holder.cardView.setCardElevation(context.getResources().getDimensionPixelSize(R.dimen.elevation_pressed));
                        mDragStartListener.onStartDrag(holder);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("Adptr" , "ACTION_MOVE");
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d("Adptr" , "ACTION_UP");
                        holder.cardView.setCardElevation(context.getResources().getDimensionPixelSize(R.dimen.elevation));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.d("Adptr" , "ACTION_CANCEL");
                        holder.cardView.setCardElevation(context.getResources().getDimensionPixelSize(R.dimen.elevation));
                        break;
                }
                return true;
            }
        });
        holder.close.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //cardMakerList.remove(position);
                holder.cardView.setCardElevation(context.getResources().getDimensionPixelSize(R.dimen.elevation));
                return false;
            }
        });
    }
    @Override
    public int getItemCount(){
        return cardMakerList.size();
    }

    public EditListAdapter setSwipeListener(SwipeListener listener){
        this.swipeListener = listener;
        return this;
    }

    public interface SwipeListener {
        void onComplete(int position);
    }
}
