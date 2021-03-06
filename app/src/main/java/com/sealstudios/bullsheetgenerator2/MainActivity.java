//package com.sealstudios.bullsheetgenerator2;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.webkit.WebView;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.internal.ConnectionCallbacks;
//import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
//import com.google.android.material.snackbar.Snackbar;
//import com.sealstudios.bullsheetgenerator2.intro.MyIntro;
//import com.sealstudios.bullsheetgenerator2.jobtasks.FindAJobAsyncTask;
//import com.sealstudios.bullsheetgenerator2.jobtasks.IndeedAsyncTask;
//import com.sealstudios.bullsheetgenerator2.jobtasks.TotalJobsAsyncTask;
//import com.sealstudios.bullsheetgenerator2.utils.Constants;
//import com.sealstudios.bullsheetgenerator2.utils.SharedPreferenceHelper;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.List;
//import java.util.Locale;
//
//import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
//import hotchemi.android.rate.AppRate;
//
//import static com.sealstudios.bullsheetgenerator2.utils.Constants.PERMISSION_ACCESS_FINE_LOCATION;
//
//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener, ConnectionCallbacks,
//        OnConnectionFailedListener, DatePickerDialog.OnDateSetListener {
//
//    private EditText jobDescriptionEditText, jobLocationEditText;
//    private TextView radiusText;
//    private CircularProgressButton submit;
//    private Button dateTo, dateFrom;
//    private AdView mAdView;
//    private int radius = 1;
//    private DatePickerDialogFragment mDatePickerDialogFragment;
//    private Location mLastLocation;
//    public static final int FLAG_START_DATE = 0;
//    public static final int FLAG_END_DATE = 1;
//    public final String TAG = "MnAct";
//    private int flag = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        callRateApp();
//        t.start();
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setIcon(R.mipmap.toolbar_icon);
//        getSupportActionBar().setTitle(getString(R.string.app_short_name));
//        jobDescriptionEditText = findViewById(R.id.job_description_text_view);
//        jobLocationEditText = findViewById(R.id.job_location_text_view);
//        radiusText = findViewById(R.id.radius_text);
//        dateFrom = findViewById(R.id.dateFrom);
//        dateTo = findViewById(R.id.dateTo);
//        dateFrom.setOnClickListener(this);
//        dateTo.setOnClickListener(this);
//        SeekBar sb = findViewById(R.id.seekbar);
//        setUpSeekBar(sb);
//        mDatePickerDialogFragment = new DatePickerDialogFragment();
//        submit = findViewById(R.id.submit_btn);
//        submit.setOnClickListener(v -> {
//            validateData();
//            submit.startAnimation();
//        });
//        ImageButton lctnBtn = findViewById(R.id.get_location_button);
//        lctnBtn.setOnClickListener(v -> {
//            if (checkPermission(MainActivity.this)) {
//                initializaGoogleApi();
//            } else {
//                requestPermission(MainActivity.this);
//            }
//        });
//
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequestBanner = new AdRequest.Builder()
//                .build();
//        mAdView.loadAd(adRequestBanner);
//    }
//
//    Thread t = new Thread(() -> {
//        SharedPreferenceHelper sharedPreferencesHelper = new SharedPreferenceHelper(this);
//        if (sharedPreferencesHelper.getBool(SharedPreferenceHelper.isFirstTime, false)) {
//            Intent i = new Intent(MainActivity.this, MyIntro.class);
//            startActivity(i);
//            sharedPreferencesHelper.setBool(SharedPreferenceHelper.isFirstTime, false);
//        }
//    });
//
//    public void callRateApp() {
//        AppRate.with(this)
//                .setInstallDays(1)
//                .setLaunchTimes(5)
//                .setRemindInterval(1)
//                .setShowLaterButton(true)
//                .setDebug(false)
//                .setOnClickButtonListener(which -> {
//                    Intent intent = new Intent(MainActivity.this, WebView.class);
//                    startActivity(intent);
//                })
//                .monitor();
//        AppRate.showRateDialogIfMeetsConditions(this);
//    }
//
//    public int getRadius() {
//        return radius;
//    }
//
//    public void setRadius(int i) {
//        this.radius = i;
//    }
//
//    public void setUpSeekBar(SeekBar sb) {
//        sb.setProgress(1);
//        radiusText.setText(getString(R.string.radius, seekValue(getRadius())));
//        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                String radius = getString(R.string.radius, seekValue(progress));
//                setRadius(progress);
//                radiusText.setText(radius);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//    }
//
//    public void validateData() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        boolean totaljobsSrc = sharedPreferences.getBoolean(Constants.TOTAL_JOBS, true);
//        boolean fajSrc = sharedPreferences.getBoolean(Constants.FIND_A_JOB, true);
//        boolean indeedSrc = sharedPreferences.getBoolean(Constants.INDEED, true);
//        if (!totaljobsSrc && !fajSrc && !indeedSrc) {
//            Snackbar.make(jobLocationEditText, "Enable a site in settings", Snackbar.LENGTH_SHORT).show();
//            submit.revertAnimation();
//        } else {
//            //if no date is picked use today
//            if (dateFrom.getText().toString().equals("Date From")) {
//                final Calendar c = Calendar.getInstance();
//                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy", Locale.ENGLISH);
//                dateFrom.setText(format.format(c.getTime()));
//            }
//            //if no date is picked use today
//            if (dateTo.getText().toString().equals("Date To")) {
//                final Calendar c = Calendar.getInstance();
//                c.add(Calendar.DAY_OF_MONTH, 2);
//                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy", Locale.ENGLISH);
//                dateTo.setText(format.format(c.getTime()));
//            }
//            editor.putString(Constants.DATE_FROM, dateFrom.getText().toString());
//            editor.putString(Constants.DATE_TO, dateTo.getText().toString());
//            editor.apply();
//
//            if (totaljobsSrc) {
//                Log.d(TAG, "validateTotalJobs");
//                Constants.JOB_LISTS_TO_RETURN++;
//                Constants.TOTAL_JOBS_BOOLEAN = true;
//                validateTotalJobs();
//            }
//            if (fajSrc) {
//                Log.d(TAG, "validateFindAJob");
//                Constants.JOB_LISTS_TO_RETURN++;
//                Constants.FIND_A_JOB_BOOLEAN = true;
//                validateFindAJob();
//            }
//            if (indeedSrc) {
//                if (jobLocationEditText.getText().toString().isEmpty() && jobDescriptionEditText.getText().toString().isEmpty()) {
//                    Snackbar.make(jobLocationEditText, R.string.indeed_needs, Snackbar.LENGTH_LONG).show();
//                    if (!fajSrc && !totaljobsSrc) {
//                        submit.revertAnimation();
//                    }
//                } else {
//                    Constants.JOB_LISTS_TO_RETURN++;
//                    Constants.INDEED_BOOLEAN = true;
//                    validateIndeed();
//                }
//            }
//        }
//    }
//
//    public void validateIndeed() {
//        String location = jobLocationEditText.getText().toString();
//        String description = jobDescriptionEditText.getText().toString();
//        if (!location.isEmpty()) {
//            location = location.trim().replaceAll(" ", "+");
//        }
//        if (!description.isEmpty()) {
//            description = description.trim().replaceAll(" ", "+");
//        }
//        String searchUrl = Constants.INDEED_URL + getString(R.string.indeedJobSearch, description, location);
//        getIndeedResults(searchUrl);
//    }
//
//    public void validateFindAJob() {
//        String location = jobLocationEditText.getText().toString();
//        String description = jobDescriptionEditText.getText().toString();
//        if (!location.isEmpty()) {
//            location = location.trim().replaceAll(" ", "+");
//        }
//        if (!description.isEmpty()) {
//            description = description.trim().replaceAll(" ", "+");
//        }
//        String searchUrl = Constants.FIND_A_JOB_URL + getString(R.string.findAJobSearch, description, location);
//        getFindAJobResults(searchUrl);
//    }
//
//    public void validateTotalJobs() {
//        String location = jobLocationEditText.getText().toString();
//        String description = jobDescriptionEditText.getText().toString();
//        String radius;
//
//        if (!location.isEmpty() && !location.contains(" ")) {
//            location = new StringBuilder(location).insert(location.length() - 3, " ").toString();
//        }
//        if (getRadius() < 1) {
//            radius = "5";
//        } else {
//            radius = String.valueOf(seekValue(getRadius()));
//        }
//        String totalJobsUrl = Constants.TOTAL_JOBS_URL;
//        String locationUrl = getString(R.string.totalJobPostCode, location.replaceAll(" ", "-"), radius);
//        String descriptionUrl = getString(R.string.totalJobDescription, description.toLowerCase().trim().replaceAll(" ", "-"));
//        String siteUrl;
//
//        if (description.trim().isEmpty() &&
//                description.length() < 1 &&
//                location.trim().isEmpty() &&
//                location.length() < 1) {
//            siteUrl = totalJobsUrl + getString(R.string.totalJobDescriptionEmpty, locationUrl);
//            getTotalJobsResults(siteUrl);
//        } else if (description.trim().isEmpty() && description.length() < 1) {
//            siteUrl = totalJobsUrl + getString(R.string.totalJobDescriptionEmpty, locationUrl);
//            getTotalJobsResults(siteUrl);
//        } else if (location.trim().isEmpty() && location.length() < 1) {
//            siteUrl = totalJobsUrl + descriptionUrl;
//            getTotalJobsResults(siteUrl);
//        } else {
//            siteUrl = totalJobsUrl + descriptionUrl + locationUrl;
//            getTotalJobsResults(siteUrl);
//        }
//    }
//
//    public void getTotalJobsResults(String siteUrl) {
//        new TotalJobsAsyncTask(submit).execute(siteUrl);
//    }
//
//    public void getFindAJobResults(String siteUrl) {
//        new FindAJobAsyncTask(submit).execute(siteUrl);
//    }
//
//    public void getIndeedResults(String siteUrl) {
//        new IndeedAsyncTask(submit).execute(siteUrl);
//    }
//
//    private void initializaGoogleApi() {
////        if (mGoogleApiClient == null) {
////            mGoogleApiClient = new GoogleApiClient.Builder(this)
////                    .addConnectionCallbacks(this)
////                    .addOnConnectionFailedListener(this)
////                    .addApi(LocationServices.API)
////                    .build();
////        }
////        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.dateFrom) {
//            setFlag(FLAG_START_DATE);
//            mDatePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
//        } else if (id == R.id.dateTo) {
//            setFlag(FLAG_END_DATE);
//            mDatePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
//        }
//    }
//
//    public int seekValue(int i) {
//        return i * 5;
//    }
//
//    @SuppressLint("ValidFragment")
//    public static class DatePickerDialogFragment extends DialogFragment {
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//            return new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, (MainActivity) getActivity(), year, month, day);
//        }
//
//    }
//
//    public void setFlag(int i) {
//        flag = i;
//    }
//
//    public int getFlag() {
//        return flag;
//    }
//
//    @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
//        cal.set(year, month, dayOfMonth);
//        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy", Locale.ENGLISH);
//        if (getFlag() == FLAG_START_DATE) {
//            dateFrom.setText(format.format(cal.getTime()));
//        } else if (getFlag() == FLAG_END_DATE) {
//            dateTo.setText(format.format(cal.getTime()));
//        }
//    }
//
//    private boolean checkPermission(Context context) {
//        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
//        return result == PackageManager.PERMISSION_GRANTED;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_ACCESS_FINE_LOCATION:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    initializaGoogleApi();
//                } else {
//                    Snackbar.make(jobLocationEditText, "Enter a postcode", Snackbar.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }
//
//    private void requestPermission(Context context) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            Toast.makeText(context, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
//        } else {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
//        }
//    }
//
//    private void getPostcode(Double longitude, Double latitude) {
//        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
//        List<Address> addresses = null;
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (addresses != null && addresses.size() > 0) {
//
//            for (int i = 0; i < addresses.size(); i++) {
//                Address address = addresses.get(i);
//                if (address.getPostalCode() != null) {
//                    String zipCode = address.getPostalCode();
//                    jobLocationEditText.setText(zipCode);
//                    break;
//                }
//
//            }
//        }
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
////        try {
////            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
////                    mGoogleApiClient);
////            if (mLastLocation != null) {
////                getPostcode(mLastLocation.getLongitude(), mLastLocation.getLatitude());
////            }
////        } catch (SecurityException e) {
////        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Snackbar.make(jobLocationEditText, "Failed to get location.", Snackbar.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//        savedInstanceState.putString(Constants.JOB_DESCRIPTION, jobDescriptionEditText.getText().toString());
//        savedInstanceState.putString(Constants.JOB_LOCATION, jobLocationEditText.getText().toString());
//        savedInstanceState.putString("dateFrom", dateFrom.getText().toString());
//        savedInstanceState.putString("dateTo", dateTo.getText().toString());
//        // etc.
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        jobDescriptionEditText.setText(savedInstanceState.getString(Constants.JOB_DESCRIPTION));
//        jobLocationEditText.setText(savedInstanceState.getString(Constants.JOB_LOCATION));
//        dateFrom.setText(savedInstanceState.getString("dateFrom"));
//        dateTo.setText(savedInstanceState.getString("dateTo"));
//    }
//
//    protected void onStart() {
//
//        super.onStart();
//    }
//
//    protected void onStop() {
//        super.onStop();
////        if (mGoogleApiClient != null) {
////            mGoogleApiClient.disconnect();
////        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mAdView != null) {
//            mAdView.pause();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mAdView != null) {
//            mAdView.resume();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.preferences: {
//                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(myIntent);
//                return true;
//            }
//            case R.id.archive: {
//                Intent myIntent = new Intent(MainActivity.this, ArchiveActivity.class);
//                startActivity(myIntent);
//                return true;
//            }
//            case R.id.share: {
//                int applicationNameId = this.getApplicationInfo().labelRes;
//                final String appPackageName = this.getPackageName();
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_SUBJECT, this.getString(applicationNameId));
//                String text = "Try Bull Sheet Generator ";
//                String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
//                i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
//                startActivity(Intent.createChooser(i, "Share link:"));
//                return true;
//            }
//            case R.id.about: {
//                Intent myIntent = new Intent(MainActivity.this, about.class);
//                startActivity(myIntent);
//                return true;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//}
