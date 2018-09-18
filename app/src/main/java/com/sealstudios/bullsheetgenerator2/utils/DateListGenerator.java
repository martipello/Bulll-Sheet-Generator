package com.sealstudios.bullsheetgenerator2.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateListGenerator {

    public static List<Date> getDates(String dateString1, String dateString2) {
        ArrayList<Date> dates = new ArrayList<Date>();
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(dateString1);
            date2 = df1 .parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            if(cal1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                //do nothing
            }else{
                dates.add(cal1.getTime());
            }
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }
}
