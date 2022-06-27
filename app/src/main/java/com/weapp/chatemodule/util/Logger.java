package com.weapp.chatemodule.util;

import android.util.Log;

public class Logger {

    public static void e(final String TAG, final String message) {
        int maxLogSize = 4000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = Math.min(end, message.length());

            Log.e(TAG + "<<<<<<<<<<< ", message.substring(start, end));
        }
    }
}