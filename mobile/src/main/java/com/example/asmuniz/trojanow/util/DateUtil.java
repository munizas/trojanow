package com.example.asmuniz.trojanow.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * Created by asmuniz on 4/13/15.
 */
public class DateUtil {

    public static Date dateFromTimestamptz(String ts) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");//'T'HH:mm:ss.SSSZ");
        ParsePosition pos = new ParsePosition(0);
        Date d = df.parse(ts, pos);
        return d;
    }
}
