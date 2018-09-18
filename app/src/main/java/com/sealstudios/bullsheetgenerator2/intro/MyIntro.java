package com.sealstudios.bullsheetgenerator2.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.sealstudios.bullsheetgenerator2.MainActivity;
import com.sealstudios.bullsheetgenerator2.R;

/**
 * Created by marti on 09/05/2016.
 */
public class MyIntro extends AppIntro2 {

    private static int SPLASH_TIME_OUT = 5000;
    public static boolean timeOver = false;
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.

        /*
        addSlide(new OneFragment());
        addSlide(new TwoFragment());
        addSlide(new ThreeFragment());
        addSlide(new FourFragment());
        */
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance("BullSheet Generator","Welcome", R.drawable.bssg_launcher_icon, getResources().getColor(R.color.colorAccent)));
        addSlide(new IntroFrag());
        //change this for my own fragment that has a scroll view and that makes you tick a box accepting terms of use and maybe have a timer on the accept button
                //with funny comments like "you have not read that this quick please read the terms of service this button will be available soon"
        // OPTIONAL METHODS

        // SHOW or HIDE the statusbar
        showStatusBar(false);

        // Edit the color of the nav bar on Lollipop+ devices
        //setNavBarColor(Color.parseColor());

        // Turn vibration on and set intensity
        // NOTE: you will need to ask VIBRATE permission in Manifest if you haven't already
        setVibrate(true);
        setVibrateIntensity(10);

        // Animations -- use only one of the below. Using both could cause errors.
        setFadeAnimation(); // OR
        //setZoomAnimation(); // OR
        //setFlowAnimation(); // OR
        //setSlideOverAnimation(); // OR
        //setDepthAnimation(); // OR
       // setCustomTransformer(yourCustomTransformer);

        // Permissions -- takes a permission and slide number
      //  askForPermissions(new String[]{Manifest.permission.CAMERA}, 3);
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                timeOver = true;
            }
        }, SPLASH_TIME_OUT);
    }


    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        //recreate();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(MyIntro.this);
        final String accepted = sharedPreferences.getString("TERMS","NOT ACCEPTED");
        if (timeOver && accepted.equals( "ACCEPTED")){
            Intent intent = new Intent(MyIntro.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (timeOver && accepted.equals( "NOT ACCEPTED")){
            Snackbar sb = Snackbar.make(MyIntro.this.doneButton,"Please accept the terms and conditions", Snackbar.LENGTH_LONG);
            sb.show();
        }
        else {
            Snackbar sb = Snackbar.make(MyIntro.this.doneButton,"You haven't read this in under 7 seconds, please just read it.", Snackbar.LENGTH_LONG);
            sb.show();
            //if they press it again and timeOver is still false tell them to just read the fucking bull shit
            //
        }

    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                timeOver = true;
            }
        }, SPLASH_TIME_OUT);
    }
}
