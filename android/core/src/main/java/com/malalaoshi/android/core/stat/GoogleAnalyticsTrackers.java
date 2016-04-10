package com.malalaoshi.android.core.stat;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.malalaoshi.android.core.R;

import java.util.HashMap;
import java.util.Map;


public final class GoogleAnalyticsTrackers {

  public enum Target {
    APP,
    // Add more trackers here if you need, and update the code in #get(Target) below
  }

  private static GoogleAnalyticsTrackers instance;

  public static synchronized void initialize(Context context) {
    if (instance != null) {
      throw new IllegalStateException("Extra call to initialize analytics trackers");
    }

    instance = new GoogleAnalyticsTrackers(context);
  }

  public static synchronized GoogleAnalyticsTrackers getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Call initialize() before getInstance()");
    }

    return instance;
  }

  private final Map<Target, Tracker> mTrackers = new HashMap<>();
  private final Context mContext;

  /**
   * Don't instantiate directly - use {@link #getInstance()} instead.
   */
  private GoogleAnalyticsTrackers(Context context) {
    mContext = context.getApplicationContext();
  }

  public synchronized Tracker get(Target target) {
    if (!mTrackers.containsKey(target)) {
      Tracker tracker;
      switch (target) {
        case APP:
          tracker = GoogleAnalytics.getInstance(mContext).newTracker(R.xml.app_tracker);
          break;
        default:
          throw new IllegalArgumentException("Unhandled analytics target " + target);
      }
      mTrackers.put(target, tracker);
    }

    return mTrackers.get(target);
  }
}
