package com.ravencoin.tools.manager;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class RReportsManager {

    private static final String TAG = RReportsManager.class.getName();

    public static void reportBug(RuntimeException er, boolean crash) {
        Log.e(TAG, "reportBug: ", er);
        try {
            Crashlytics.logException(er);
        } catch (Exception e) {
            Log.e(TAG, "reportBug: failed to report to FireBase: ", e);
        }
        if (crash) throw er;
    }

    public static void reportBug(Exception er) {
        Log.e(TAG, "reportBug: ", er);
        try {
            Crashlytics.logException(er);
        } catch (Exception e) {
            Log.e(TAG, "reportBug: failed to report to FireBase: ", e);
        }
    }
}
