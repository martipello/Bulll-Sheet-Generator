package com.sealstudios.bullsheetgenerator2.jobtasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class IndeedAsyncTask extends AsyncTask<String, Void, Void> {
    private static String TAG = "IndAsncTsk";

    private WeakReference<CircularProgressButton> weakRefProgressBar;
    private long databaseId;

    public IndeedAsyncTask(CircularProgressButton progressBar) {
        this.weakRefProgressBar = new WeakReference<>(progressBar);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String[] jobTags = new String[]{Constants.INDEED};
        Context context = weakRefProgressBar.get().getContext();
        JobList jobList = new JobList();
        jobList.setJobListFavourite(false);
        jobList.setJobListColour(context.getString(R.string.white));
        jobList.setJobListsTags(jobTags);
        ArrayList<Job> jobsList = new ArrayList<>();
        Document document = null;
        ArrayList<Document> documents = new ArrayList<>();
        documents.add(connectToSite(strings[0]));
        documents.add(connectToSite(strings[0] + "&start=10"));
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
        Constants.INDEED_BOOLEAN = false;
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
        String bullsheet = "Bullsheet! something went wrong with Indeed, this can sometimes happen, try again in a minute.";
        return new Job("", bullsheet, "", "", "", "");
    }

    private static String getJobDescription(Element metaElem) {
        String titleString = "Title";
        titleString = metaElem.text();
        if (titleString.contains("-"))
            titleString = titleString.substring(0, titleString.indexOf("-"));
        return titleString;
    }

    private static String getJobUrl(String title , String employer) {
        return Constants.INDEED_URL + title.trim().replaceAll(" " , "-") + employer.trim().replaceAll(" " , "-");
    }

    private static String getJobLocation(Element location) {
        return location.text();
    }

    private static String getJobEmployer(Element employer) {
        return employer.text();
    }

    private static String getApplicationType(String employerString, String jobLink) {

        final String[] outcomeArray = new String[]{
                "Applied on company website",
                "Applied through recruitment agency",
                "Applied through indeed"
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

        ArrayList<Job> jobArrayList = new ArrayList<>();
        Elements locations = doc.select("div.companyLocation");
        Elements employers = doc.select("span.companyName");
        Elements titles = doc.select("h2.jobTitle");

        for (int i = 0; i < titles.size(); i++){
            Job job = new Job();
                job.setJobDescription(getJobDescription(titles.get(i)));
                if (i < titles.size() && i < employers.size()){
                    job.setJobUrl(getJobUrl(employers.get(i).text(),titles.get(i).text()));
                }
                if (i < locations.size()){
                    job.setJobLocation(getJobLocation(locations.get(i)));
                }
                if (i < employers.size()){
                    job.setCompany(getJobEmployer(employers.get(i)));
                }
                if (i < titles.size() && i < employers.size()){
                    job.setApplicationType(getApplicationType(getJobEmployer(employers.get(i)),
                            job.getJobUrl()));
                }
                jobArrayList.add(job);
        }
        return jobArrayList;

    }

}
