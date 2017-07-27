package com.bh.olga_pc.hostess;

/**
 * Created by Olga-PC on 7/8/2017.
 */

import android.content.Context;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by olgats on 11/04/2016.
 */
public class Utilities {
    /////////////////////////////////////////////////////////////////
    //
    //      Helper methods.
    //
    /////////////////////////////////////////////////////////////////

    /**
     * Checks if two times are on the same day.
     *
     * @param dayOne The first day.
     * @param dayTwo The second day.
     * @return Whether the times are on the same day.
     */
    public static boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Checks if two times are on the same day.
     *
     * @param dayOne The first day.
     * @param dayTwo The second day.
     * @return Whether the times are on the same day.
     */
    public static boolean isSameDay(long dayOne, long dayTwo) {
        Calendar cOne = Calendar.getInstance();
        cOne.setTimeInMillis(dayOne);
        Calendar cTwo = Calendar.getInstance();
        cTwo.setTimeInMillis(dayTwo);
        return isSameDay(cOne, cTwo);
    }

    /**
     * Returns a calendar instance at the start of this day
     *
     * @return the calendar instance
     */
    public static Calendar today() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today;
    }

    public static String interpretTime(Context context,int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);

        try {
            SimpleDateFormat sdf = DateFormat.is24HourFormat(context) ? new SimpleDateFormat("HH:mm", Locale.getDefault()) : new SimpleDateFormat("hh a", Locale.getDefault());
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

   /* public static void saveImage(Context mContext, ItemImage image) {
        File photo = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), image.getFileName());

        if (!photo.exists()) {
            try (FileOutputStream fos = new FileOutputStream(photo.getPath());) {
                fos.write(image.getImage());
                fos.close();
            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }
        }
        return;
    }*/

    public static boolean isImageExists(Context mContext, String code) {
        boolean fl = false;
        File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null && dir.exists() && dir.isDirectory()) {
            String[] arr = dir.list(new ImageFilenameFilter(code));
            fl = arr != null && arr.length > 0;
        }
        return fl;
    }

    public static File getImageFile(Context mContext, String code) {
        File f = null;
        File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] arr = dir.listFiles(new ImageFilenameFilter(code));
            f = arr != null && arr.length > 0 ? arr[0] : null;
        }
        return f;
    }

    private static class ImageFilenameFilter implements FilenameFilter {
        private String code;

        public ImageFilenameFilter(String code) {
            this.code = code;
        }

        @Override
        public boolean accept(File arg0, String fileName) {
            return fileName.contains(code);
        }
    }

    public static final int getColor(Context context, @ColorRes int id) {
        // final int version = Build.VERSION.SDK_INT;
        return ContextCompat.getColor(context, id);
    }
}
