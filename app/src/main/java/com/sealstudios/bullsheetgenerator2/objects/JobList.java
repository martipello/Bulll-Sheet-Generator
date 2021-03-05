package com.sealstudios.bullsheetgenerator2.objects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.sealstudios.bullsheetgenerator2.utils.ArrayConverter;
import com.sealstudios.bullsheetgenerator2.utils.Constants;
import com.sealstudios.bullsheetgenerator2.utils.ListConverter;

@Entity
public class JobList implements Parcelable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Constants.JOB_ID)
    private int jobId;

    @ColumnInfo(name = Constants.JOB_LIST_TITLE)
    private String jobListTitle;

    @ColumnInfo(name = Constants.JOB_LIST)
    @TypeConverters({ListConverter.class})
    private String jobList;

    @ColumnInfo(name = Constants.JOB_LIST_FAVOURITE)
    private boolean jobListFavourite;

    @ColumnInfo(name = Constants.JOB_LIST_COLOUR)
    private String jobListColour;

    @ColumnInfo(name = Constants.JOB_LIST_TAGS)
    @TypeConverters({ArrayConverter.class})
    private String[] jobListsTags;

    public JobList() {
    }

    public JobList(int jobId, String jobListTitle, String jobList, Boolean jobListFavourite,String jobListColour,String[] jobListsTags) {
    }

    @NonNull
    public int getJobId() {
        return jobId;
    }

    public void setJobId(@NonNull int jobId) {
        this.jobId = jobId;
    }

    public String getJobList() {
        return jobList;
    }

    public void setJobList(String jobList) {
        this.jobList = jobList;
    }

    public boolean isJobListFavourite() {
        return jobListFavourite;
    }

    public void setJobListFavourite(boolean jobListFavourite) {
        this.jobListFavourite = jobListFavourite;
    }

    public String getJobListColour() {
        return jobListColour;
    }

    public void setJobListColour(String jobListColour) {
        this.jobListColour = jobListColour;
    }

    public String[] getJobListsTags() {
        return jobListsTags;
    }

    public void setJobListsTags(String[] jobListsTags) {
        this.jobListsTags = jobListsTags;
    }

    public String getJobListTitle() {
        return jobListTitle;
    }

    public void setJobListTitle(String jobListTitle) {
        this.jobListTitle = jobListTitle;
    }

    protected JobList(Parcel in) {
        jobId = in.readInt();
        jobListTitle = in.readString();
        jobList = in.readString();
        jobListColour = in.readString();
        jobListFavourite = in.readByte() != 0;
        jobListsTags = in.createStringArray();
    }

    public static final Creator<JobList> CREATOR = new Creator<JobList>() {
        @Override
        public JobList createFromParcel(Parcel in) {
            return new JobList(in);
        }

        @Override
        public JobList[] newArray(int size) {
            return new JobList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(jobId);
        dest.writeString(jobList);
        dest.writeString(jobListTitle);
        dest.writeString(jobListColour);
        dest.writeByte((byte) (jobListFavourite ? 1 : 0));
        dest.writeStringArray(jobListsTags);
    }
}
