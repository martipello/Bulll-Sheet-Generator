package com.sealstudios.bullsheetgenerator2.jobtasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
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
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;

public class FindAJobAsyncTask extends AsyncTask<String, Void, Void> {
    private static String TAG = "FndJbAsncTsk";

    private WeakReference<CircularProgressButton> weakRefProgressBar;
    private long databaseId;

    public FindAJobAsyncTask(CircularProgressButton progressBar) {
        this.weakRefProgressBar = new WeakReference<>(progressBar);
    }

    @Override
    protected Void doInBackground(String... strings) {
        Context context = weakRefProgressBar.get().getContext();
        String[] jobTags = new String[]{Constants.FIND_A_JOB_URL};
        JobList jobList = new JobList();
        jobList.setJobListFavourite(false);
        jobList.setJobListsTags(jobTags);
        jobList.setJobListColour(context.getString(R.string.white));
        ArrayList<Job> jobsList = new ArrayList<>();
        Document document = null;
        ArrayList<Document> documents = new ArrayList<>();
        documents.add(connectToSite(strings[0]));
        String page2 = strings[0].replace("search?q=","search?d=10&p=2&q=");
        documents.add(connectToSite(page2));

        for (Document doc : documents) {
            jobsList.addAll(createJobListFromDoc(doc));
        }
        jobList.setJobList(ListConverter.stringFromJobList(jobsList));
        if (ListConverter.jobListFromString(jobList.getJobList()).isEmpty()) {
            jobsList.add(errorJob());
            Log.d(TAG ,"jobArrayList size " + jobsList.size());
        }
        jobList.setJobList(ListConverter.stringFromJobList(jobsList));
        JobListRepository jobListRepository = new JobListRepository(weakRefProgressBar.get().getContext());
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
        Constants.FIND_A_JOB_BOOLEAN = false;
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
        String bullsheet = "Bullsheet! something went wrong with Find A Job, this can sometimes happen, try again in a minute.";
        return new Job("", bullsheet, "", "", "", "");
    }

    private static String getJobDescription(Element metaElem) {
        return metaElem.text();
    }

    private static String getJobUrl(Element link) {
        String elementText = link.html();
        int start = elementText.indexOf("http");
        String firstUrl = elementText.substring(start,elementText.length());
        int end = firstUrl.indexOf("\"");
        return firstUrl.substring(0,end);
    }

    private static String getJobLocation(Element location) {
        return location.text();
    }

    private static String getJobEmployer(Element employer) {
        return employer.text();
    }

    private static String getApplicationType(String employerString) {

        final String[] outcomeArray = new String[]{
                "Applied on company website",
                "Applied through recruitment agency",
                "Applied through find a job"
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
        Elements titles = doc.select("h3.heading-small");
        Elements links = doc.select("h3");
        Elements employers = doc.select("ul.search-result-details > li > strong");
        Elements locations = doc.select("ul.search-result-details > li > span");
        for (int i = 0; i < titles.size(); i++){
            Job job = new Job();
                job.setJobDescription(getJobDescription(titles.get(i)));
                if (i < links.size()){
                    job.setJobUrl(getJobUrl(links.get(i)));
                }
                if (i < locations.size()){
                    job.setJobLocation(getJobLocation(locations.get(i)));
                }
                if (i * 2 < employers.size()){
                    job.setCompany(getJobEmployer(employers.get(i * 2)));
                }
                if (i * 2 < employers.size() && i < links.size()){
                    job.setApplicationType(getApplicationType(getJobEmployer(employers.get(i * 2))));
                }
                jobArrayList.add(job);
        }
        return jobArrayList;
    }

}
