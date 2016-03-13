package com.malalaoshi.android.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.util.*;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Payment UI
 * Created by tianwei on 2/27/16.
 */
public class PayFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.btn_ali)
    protected ImageView alipayBtn;
    @Bind(R.id.btn_wx)
    protected ImageView wxpayBtn;
    @Bind(R.id.rl_ali)
    protected View alipayLayout;
    @Bind(R.id.rl_wx)
    protected View wxpayLayout;
    @Bind(R.id.tv_total)
    protected TextView totalView;

    private PayManager.Pay currentPay;

    @Bind(R.id.tv_pay)
    protected TextView payView;

    private DialogFragment pendingDailog;

    private CreateCourseOrderResultEntity resultEntity;


    public static PayFragment newInstance(CreateCourseOrderResultEntity orderEntity) {
        PayFragment fragment = new PayFragment();
        fragment.setOrderEntity(orderEntity);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay, container, false);
        ButterKnife.bind(this, view);
        setCurrentPay(PayManager.Pay.alipay);
        alipayLayout.setOnClickListener(this);
        wxpayLayout.setOnClickListener(this);
        payView.setOnClickListener(this);
        if (resultEntity != null) {
            double value = Double.valueOf(resultEntity.getTotal()) * 0.01d;
            totalView.setText(com.malalaoshi.android.util.Number.subZeroAndDot(value));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pendingDailog != null) {
            showDialog(pendingDailog);
        }
    }

    private void setOrderEntity(CreateCourseOrderResultEntity entity) {
        resultEntity = entity;
    }

    private void setCurrentPay(PayManager.Pay pay) {
        currentPay = pay;
        alipayBtn.setImageResource(pay == PayManager.Pay.alipay ?
                R.drawable.ic_check : R.drawable.ic_check_out);
        wxpayBtn.setImageResource(pay == PayManager.Pay.wx ?
                R.drawable.ic_check : R.drawable.ic_check_out);
    }

    public void onClick(View view) {

        if (view.getId() == R.id.rl_ali) {
            setCurrentPay(PayManager.Pay.alipay);
        } else if (view.getId() == R.id.rl_wx) {
            setCurrentPay(PayManager.Pay.wx);
        } else if (view.getId() == R.id.tv_pay) {
            //payView.setOnClickListener(null);
            pay();
        }
    }

    private void pay() {
        if (resultEntity == null || TextUtils.isEmpty(resultEntity.getOrder_id())) {
            return;
        }
        PayManager.getInstance().getOrderInfo(resultEntity.getId(),
                currentPay.name(), new ResultCallback<Object>() {
                    @Override
                    public void onResult(Object data) {
                        if (data != null) {
                            payInternal(data.toString());
                        } else {
                            MiscUtil.toast("订单状态不正确");
                        }
                    }
                });
    }

    private void payInternal(final String charge) {
        MalaContext.exec(new Runnable() {
            @Override
            public void run() {
                PayManager.getInstance().pay(charge, getActivity());
            }
        });
    }

    /**
     * 处理返回值
     * "success" - payment succeed
     * "fail"    - payment failed
     * "cancel"  - user canceld
     * "invalid" - payment plugin not installed
     * TODO 现在只能模拟两种，一种失败，一种成功。其它每一支付或是支付重复我现在没有模拟出来。以后加上
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == PayManager.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                Log.e("AABB", "On activity result: " + result);
                PayResultDialog dialog = new PayResultDialog();
                if (result == null) {
                    dialog.setType(PayResultDialog.Type.PAY_FAILED);
                } else if (result.equals("success")) {
                    dialog.setType(PayResultDialog.Type.PAY_SUCCESS);
                } else if (result.equals("cancel")) {
                    dialog.setType(PayResultDialog.Type.CANCEL);
                } else if (result.equals("invalid")) {
                    dialog.setType(PayResultDialog.Type.INVALID);
                } else {
                    dialog.setType(PayResultDialog.Type.PAY_FAILED);
                }
                if (isResumed()) {
                    showDialog(dialog);
                } else {
                    pendingDailog = dialog;
                }
                Log.e("AABB", "On activity result end");
            }
        }
    }

    private void showDialog(DialogFragment fragment) {
        final String FLAG = "payresultidialog";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(FLAG);
        if (prev != null) {
            ft.remove(prev);
        }
        try {
            fragment.show(ft, FLAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pendingDailog = null;
    }

}
