package com.malalaoshi.android.usercenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.malalaoshi.android.BuildConfig;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.net.Constants;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.view.MaClickableSpan;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * verify the phone of user by verify code.
 * Created by tianwei on 12/27/15.
 */
public class VerificationView extends RelativeLayout implements MaClickableSpan.OnLinkClickListener {

    private static final String TAG = "VerificationView";
    private static final int SEND_VERIFY_INTERVAL = 60;
    private Context context;
    private ViewController controller;

    @Bind(R.id.et_code)
    protected EditText codeEditView;
    @Bind(R.id.et_Phone)
    protected EditText phoneEditView;
    @Bind(R.id.tv_user_agree)
    protected TextView userAgreeView;
    @Bind(R.id.btn_verify)
    protected TextView verifyButton;
    @Bind(R.id.tv_warn)
    protected TextView errorView;
    @Bind(R.id.btn_fetch_code)
    protected TextView btnFetchCodeView;
    @Bind(R.id.tv_warn_phone)
    protected TextView errorPhoneView;
    private boolean enableVerifyButton;
    private Handler handler;

    public VerificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (context instanceof ViewController) {
            controller = (ViewController) context;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.activity_usercenter, this, true);
        ButterKnife.bind(this, rootView);
        buildLinkText();
        phoneEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkVerifyButtonStatus();
                errorPhoneView.setVisibility(INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        codeEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkVerifyButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacks(null);
            handler = null;
        }
    }

    @OnClick(R.id.iv_quit)
    protected void onQuitClick() {
        ((SmsAuthActivity) context).finish();
    }

    @OnClick(R.id.btn_fetch_code)
    protected void onFetchCodeClick() {
        String phone = phoneEditView.getText().toString();
        // For Debug: close the phone verification
        if (!BuildConfig.DEBUG && (TextUtils.isEmpty(phone) || !MiscUtil.isMobilePhone(phone))) {
            errorPhoneView.setVisibility(VISIBLE);
            return;
        }
        fetchVerifyCode(phone);
    }

    @OnClick(R.id.btn_verify)
    protected void onVerifyClick() {
        verify();
    }

    @Override
    public void onLinkClick(View view) {
        openUserProtocol();
    }

    private void checkVerifyButtonStatus() {
        if (errorView.getVisibility() == VISIBLE) {
            errorView.setVisibility(GONE);
        }
        boolean status = phoneEditView.getText().length() > 0 && codeEditView.getText().length() > 0;
        if (status != enableVerifyButton) {
            enableVerifyButton = status;
            verifyButton.setEnabled(enableVerifyButton);
            verifyButton.setClickable(enableVerifyButton);
        }
    }

    private void buildLinkText() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        MaClickableSpan span = new MaClickableSpan();
        span.setOnLinkClickListener(this);
        String prefix = getResources().getString(R.string.usercenter_touch_protocol);
        String suffix = getResources().getString(R.string.usercenter_user_agreement);
        builder.append(prefix + suffix);
        builder.setSpan(span, prefix.length(),
                prefix.length() + suffix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userAgreeView.setText(builder);
        userAgreeView.setMovementMethod(new LinkMovementMethod());
    }

    protected void openUserProtocol() {
        if (controller != null) {
            controller.onChangeView(this, true, UserProtocolView.class);
        }
    }

    private void fetchVerifyCode(String phone) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.ACTION, Constants.SEND);
        params.put(Constants.PHONE, phone);
        NetworkSender.verifyCode(params, new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                try {
                    JSONObject jo = new JSONObject(json.toString());
                    if (jo.optBoolean(Constants.SENT, false)) {
                        fetchSucceeded();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fetchCodeFailed();
            }

            @Override
            public void onFailed(VolleyError error) {
                fetchCodeFailed();
            }
        });
    }

    private void fetchSucceeded() {
        MiscUtil.toast(R.string.usercenter_verify_code_sent);
        btnFetchCodeView.setEnabled(false);
        countDown(SEND_VERIFY_INTERVAL);
    }

    private void countDown(final int time) {
        btnFetchCodeView.setText(getResources().getString(R.string.seconds_count_down, time));
        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (time < 1) {
                        btnFetchCodeView.setEnabled(true);
                        btnFetchCodeView.setText(R.string.usercenter_fetch_verify_code);
                    } else {
                        countDown(time - 1);
                    }
                }
            }, 1000);
        }
    }

    private void fetchCodeFailed() {
        MiscUtil.toast(R.string.usercenter_fetch_code_failed);
    }

    private void verify() {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.ACTION, "verify");
        params.put(Constants.PHONE, phoneEditView.getText().toString());
        params.put(Constants.CODE, codeEditView.getText().toString());
        NetworkSender.verifyCode(params, new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                try {
                    Log.i(TAG, "::: " + json.toString());
                    verifySucceeded(new JSONObject(json.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                    verifyFailed();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                verifyFailed();
            }
        });
    }

    private void verifyFailed() {
        errorView.setVisibility(VISIBLE);
    }

    private void verifySucceeded(JSONObject json) {
        if (!json.optBoolean(Constants.VERIFIED, false)) {
            verifyFailed();
            return;
        }
        updateLoginToken(json.optString(Constants.TOKEN));
        updateParentId(json.optString(Constants.PARENT_ID));
        if (json.optBoolean(Constants.FIRST_LOGIN, false)) {
            if (controller != null) {
                controller.onChangeView(this, false, VerifyStudentNameView.class);
            }
        } else {
            ((SmsAuthActivity) context).setActivityResult(null);
            ((SmsAuthActivity) context).finish();
        }
    }

    private void updateParentId(String parentId) {
        if (!TextUtils.isEmpty(parentId)) {
            MalaApplication.getInstance().setParentId(parentId);
        }
    }

    private void updateLoginToken(String token) {
        if (!TextUtils.isEmpty(token)) {
            MalaApplication.getInstance().setToken(token);
        }
    }
}
