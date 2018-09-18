package com.sealstudios.bullsheetgenerator2.jobtasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.sealstudios.bullsheetgenerator2.R;
import com.sealstudios.bullsheetgenerator2.database.JobListRepository;
import com.sealstudios.bullsheetgenerator2.objects.Job;
import com.sealstudios.bullsheetgenerator2.objects.JobList;
import com.sealstudios.bullsheetgenerator2.utils.Constants;
import com.sealstudios.bullsheetgenerator2.utils.ListConverter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class TotalJobsAsyncTask extends AsyncTask<String, Void, Void> {
    private static String TAG = "TtlJbsAsncTsk";

    private WeakReference<CircularProgressButton> weakRefProgressBar;
    private long databaseId;

    public TotalJobsAsyncTask(CircularProgressButton progressBar) {
        this.weakRefProgressBar = new WeakReference<>(progressBar);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String[] jobTags = new String[]{Constants.TOTAL_JOBS};
        Context context = weakRefProgressBar.get().getContext();
        JobList jobList = new JobList();
        jobList.setJobListFavourite(false);
        jobList.setJobListsTags(jobTags);
        jobList.setJobListColour(context.getString(R.string.white));
        ArrayList<Job> jobsList = new ArrayList<>();
        Document document = null;
        ArrayList<Document> documents = new ArrayList<>();
        documents.add(connectToSite(strings[0]));
        documents.add(connectToSite(strings[0] + "&page=2"));

        for (Document doc : documents) {
            jobsList.addAll(createJobListFromDoc(doc));
        }
        jobList.setJobList(ListConverter.stringFromJobList(jobsList));
        if (ListConverter.jobListFromString(jobList.getJobList()).isEmpty()) {
            jobsList.add(errorJob());
        }
        jobList.setJobList(ListConverter.stringFromJobList(jobsList));
        JobListRepository jobListRepository = new JobListRepository(weakRefProgressBar.get().getContext());
        //TODO save this ID somewhere like shared preferences for ProcessJobsAsyncTask
        //databaseId = jobListRepository.addList(jobList);
        jobListRepository.addList(jobList);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        Constants.TOTAL_JOBS_BOOLEAN = false;
        if (!Constants.INDEED_BOOLEAN && !Constants.FIND_A_JOB_BOOLEAN && !Constants.TOTAL_JOBS_BOOLEAN) {
            new ProcessJobsAsyncTask(weakRefProgressBar.get()).execute();
        }
    }

    public static Document connectToSite(String siteUrl) {
        Document document = null;
        try {
            document = Jsoup.connect(siteUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    private static Job errorJob() {
        String bullsheet = "Bullsheet! something went wrong with TotalJobs, this can sometimes happen, try again in a minute.";
        return new Job("", bullsheet, "", "", "", "");
    }

    private static String getJobDescription(Element metaElem) {
        String titleString = "Title";
        titleString = metaElem.text();
        if (titleString.contains("Featured"))
            titleString = titleString.replaceAll("Featured", "");
        else if (titleString.contains("found"))
            titleString = titleString.substring(0, titleString.indexOf("found"));
        else if (titleString.contains("Premium"))
            titleString = titleString.substring(0, titleString.indexOf("Premium"));
        return titleString;
    }

    private static String getJobUrl(Element link) {
        String url = Constants.TOTAL_JOBS_URL;
        String linkSub = Constants.TOTAL_JOBS_URL;
        if (link.toString().contains("https")) {
            int start = link.toString().indexOf("https");
            linkSub = link.toString().substring(start,link.toString().length());
            int end = linkSub.indexOf("\"");
            linkSub = linkSub.substring(0,end);
        } else {
            int start = link.toString().indexOf("/job");
            linkSub = link.toString().substring(start,link.toString().length());
            int end = linkSub.indexOf("\"");
            linkSub = url + linkSub.substring(0,end);
        }
        return linkSub;
    }

    private static String getJobLocation(Element location) {
        String locationString;
        locationString = location.text().substring(0, location.text().indexOf("from"));
        return locationString;
    }

    private static String getJobEmployer(Element employer) {
        String employerString = "Recruiter";
        String[] parts = employer.toString().split("\"");
        employerString = parts[3];
        return employerString;
    }

    private static String getApplicationType(String employerString, String jobLink) {

        final String[] outcomeArray = new String[]{
                "Applied on company website",
                "Applied through recruitment agency",
                "Applied through totaljobs"
        };
        final String[] companies = new String[]{
                "scs sofa", "carphone", "asda",
                "tesco", "sainsbury", "mcdonald",
                "lidl", "aldi", "sainsbury"
        };
        final String[] recruiters = new String[]{
                "recruit", "solution", "resourc",
                "reed", "adecco", "personnel",
                "agency", "employment"
        };
        String appliedString = "default";
        String[] employerParts = employerString.split(" ");
        for (String employerPart : employerParts) {
            if (jobLink.toLowerCase().contains(employerPart.toLowerCase()))
                appliedString = outcomeArray[0];
        }
        for (String company : companies) {
            if (employerString.toLowerCase().contains(company))
                appliedString = outcomeArray[0];
        }
        for (String recruiter : recruiters) {
            if (employerString.toLowerCase().contains(recruiter))
                appliedString = outcomeArray[1];
        }
        if (appliedString.equals("default")) {
            appliedString = outcomeArray[2];
        }
        return appliedString;
    }

    private static ArrayList<Job> createJobListFromDoc(Document doc) {
        //Job job = new Job("Company", "Description", "Location", "Applied", Constants.TOTAL_JOBS_URL, "");
        ArrayList<Job> jobArrayList = new ArrayList<>();
        Elements locations = doc.select("ul.header-list > li.location");
        Elements links = doc.select("div.job-title > a[href]"); //also holds a reference to the job id
        Elements titles = doc.select("div.job-title > a[href] > h2");
        Elements employers = doc.select("div.recruiter-image > a[href]");
        for (int i = 0; i < titles.size(); i++){
            Job job = new Job();
                job.setJobDescription(getJobDescription(titles.get(i)));
                if (i < links.size()){
                    job.setJobUrl(getJobUrl(links.get(i)));
                }
                if (i < locations.size()){
                    job.setJobLocation(getJobLocation(locations.get(i)));
                }
                if (i < employers.size()){
                    job.setCompany(getJobEmployer(employers.get(i)));
                }
                if (i < employers.size() && i < links.size()){
                    job.setApplicationType(getApplicationType(getJobEmployer(employers.get(i)),
                            getJobUrl(links.get(i))));
                }
                jobArrayList.add(job);
        }
        return jobArrayList;
    }

}
