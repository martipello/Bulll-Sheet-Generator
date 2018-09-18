package com.sealstudios.bullsheetgenerator2.objects;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Job implements Parcelable{

    private String company;
    private String jobDescription;
    private String jobLocation;
    private String applicationType;
    private String jobUrl;
    private String jobDate;

    public Job(){}

    public Job(String company, String jobDescription, String jobLocation, String applicationType, String jobUrl, String jobDate) {
        this.company = company;
        this.jobDescription = jobDescription;
        this.jobLocation = jobLocation;
        this.applicationType = applicationType;
        this.jobUrl = jobUrl;
        this.jobDate = jobDate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getJobDate() {
        return jobDate;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    protected Job(Parcel in) {
        company = in.readString();
        jobDescription = in.readString();
        jobLocation = in.readString();
        applicationType = in.readString();
        jobUrl = in.readString();
        jobDate = in.readString();
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(company);
        dest.writeString(jobDescription);
        dest.writeString(jobLocation);
        dest.writeString(applicationType);
        dest.writeString(jobUrl);
        dest.writeString(jobDate);
    }

    public static class jobList {
        private List<Job> jobList;
        public List<Job> getJobList() {
            return jobList;
        }
    }
}
