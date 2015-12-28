package com.malalaoshi.android.usercenter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;


/**
 * Verification the phone and student's name
 * Created by tianwei on 12/26/15.
 */
public class UsercenterActivity extends Activity {

    public static final int MSG_MAIN = 0x1;
    public static final int MSG_VERIFY_NAME = 0x2;
    public static final int MSG_USER_PROTOCOL = 0x3;

    private Handler handler;

    public static final class ViewHandler extends Handler {
        private WeakReference<UsercenterActivity> reference;

        public ViewHandler(UsercenterActivity activity, Looper looper) {
            super(looper);
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            UsercenterActivity activity = reference.get();
            if (activity != null) {
                activity.setUserView(msg.what);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new ViewHandler(this, Looper.getMainLooper());
        setContentView(new VerificationView(this, null));
    }

    private void setUserView(int viewType) {
        View view = null;
        switch (viewType) {
            case MSG_MAIN:
                view = new VerificationView(this, null);
                break;
            case MSG_USER_PROTOCOL:
                view = new UserProtocolview(this, null);
                break;
            case MSG_VERIFY_NAME:
                view = new VerificationView(this, null);
                break;
            default:
                break;
        }
        if (view != null) {
            setContentView(view);
        }
    }

    public Handler getHandler() {
        return handler;
    }
}
