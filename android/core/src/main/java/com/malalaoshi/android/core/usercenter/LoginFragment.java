package com.malalaoshi.android.core.usercenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.api.VerifyCodeApi;
import com.malalaoshi.android.core.usercenter.entity.AuthUser;
import com.malalaoshi.android.core.usercenter.entity.SendSms;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.core.view.MaClickableSpan;

/**
 * Login in UI
 * Created by tianwei on 3/26/16.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener, MaClickableSpan.OnLinkClickListener {
    private static final int SEND_VERIFY_INTERVAL = 60;

    private EditText codeEditView;
    private EditText phoneEditView;
    private TextView userAgreeView;
    private TextView verifyButton;
    private TextView errorView;
    private TextView btnFetchCodeView;
    private TextView errorPhoneView;
    private boolean enableVerifyButton;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.core__fragment_login, container, false);
        initView(rootView);
        return rootView;
    }


    private void initView(View rootView) {
        codeEditView = (EditText) rootView.findViewById(R.id.et_code);
        phoneEditView = (EditText) rootView.findViewById(R.id.et_Phone);
        userAgreeView = (TextView) rootView.findViewById(R.id.tv_user_agree);
        verifyButton = (TextView) rootView.findViewById(R.id.btn_verify);
        errorView = (TextView) rootView.findViewById(R.id.tv_warn);
        btnFetchCodeView = (TextView) rootView.findViewById(R.id.btn_fetch_code);
        errorPhoneView = (TextView) rootView.findViewById(R.id.tv_warn_phone);
        btnFetchCodeView.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
        phoneEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkVerifyButtonStatus();
                errorPhoneView.setVisibility(View.INVISIBLE);
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
        buildLinkText();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_fetch_code) {
            onFetchCodeClick();
        } else if (v.getId() == R.id.btn_verify) {
            verify();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(null);
            handler = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    protected void onFetchCodeClick() {
        StatReporter.fetchCode(getStatName());
        String phone = phoneEditView.getText().toString();
        // For Debug: close the phone verification
        if (TextUtils.isEmpty(phone) || !MiscUtil.isMobilePhone(phone)) {
            errorPhoneView.setVisibility(View.VISIBLE);
            return;
        }
        ApiExecutor.exec(new FetchVerifyCodeRequest(this, phone));
    }

    @Override
    public void onLinkClick(View view) {
        getActivity().startActivity(new Intent(getActivity(), UserProtocolActivity.class));
        StatReporter.userProtocol(getStatName());
    }

    private void checkVerifyButtonStatus() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
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
        String prefix = "轻触上面验证按钮,即表示您同意";
        String suffix = "麻辣教师用户协议";
        builder.append(prefix).append(suffix);
        builder.setSpan(span, prefix.length(),
                prefix.length() + suffix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userAgreeView.setText(builder);
        userAgreeView.setMovementMethod(new LinkMovementMethod());
    }

    /**
     * 这样写的目的是防止内存溢出，同时可以不用主动去取消一个请求
     */
    private static class FetchVerifyCodeRequest extends BaseApiContext<LoginFragment, SendSms> {

        private String phone;

        public FetchVerifyCodeRequest(LoginFragment loginFragment, String phone) {
            super(loginFragment);
            this.phone = phone;
        }

        @Override
        public SendSms request() throws Exception {
            return new VerifyCodeApi().get(phone);
        }

        @Override
        public void onApiSuccess(@NonNull SendSms sendSms) {
            if (!sendSms.isSent()) {
                get().fetchCodeFailed();
            } else {
                get().fetchSucceeded();
            }
        }
    }

    private void fetchCodeFailed() {
        MiscUtil.toast("验验证码发送失败，请稍候重试！");
    }

    private void fetchSucceeded() {
        MiscUtil.toast("验证码已发送");
        btnFetchCodeView.setEnabled(false);
        countDown(SEND_VERIFY_INTERVAL);
    }

    private void countDown(final int time) {
        if (handler != null) {
            btnFetchCodeView.setText(getResources().getString(R.string.seconds_count_down, time));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (time < 1) {
                        btnFetchCodeView.setEnabled(true);
                        btnFetchCodeView.setText("获取验证码");
                    } else {
                        countDown(time - 1);
                    }
                }
            }, 1000);
        }
    }

    /**
     * 这样写的目的是防止内存溢出，同时可以不用主动去取消一个请求
     */
    private static class CheckVerifyCodeRequest extends BaseApiContext<LoginFragment, AuthUser> {

        private String phone;
        private String code;

        public CheckVerifyCodeRequest(LoginFragment loginFragment, String phone, String code) {
            super(loginFragment);
            this.phone = phone;
            this.code = code;
        }

        @Override
        public AuthUser request() throws Exception {
            return new VerifyCodeApi().check(phone, code);
        }

        @Override
        public void onApiSuccess(@NonNull AuthUser user) {
            if (!user.isVerified()) {
                get().verifyFailed();
            } else {
                get().verifySucceeded(user);
            }
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().verifyFailed();
        }
    }

    private void verify() {
        StatReporter.verifyCode(getStatName());
        String phone = phoneEditView.getText().toString();
        String code = codeEditView.getText().toString();
        ApiExecutor.exec(new CheckVerifyCodeRequest(this, phone, code));
    }

    private void verifySucceeded(AuthUser user) {
        UserManager.getInstance().login(user);
        if (user.isFirst_login()) {
            Intent intent = new Intent(getActivity(), AddStudentNameActivity.class);
            startActivity(intent);
        }
        getActivity().setResult(LoginActivity.RESULT_CODE_LOGIN_SUCCESS);
        getActivity().finish();
    }

    private void verifyFailed() {
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public String getStatName() {
        return "用户登录";
    }
}
