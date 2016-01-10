package com.malalaoshi.android.net;

import com.android.volley.VolleyError;

/**
 * Network listener
 * Created by tianwei on 1/3/16.
 */
public interface NetworkListener {
    void onSucceed(Object json);

    void onFailed(VolleyError error);
}
