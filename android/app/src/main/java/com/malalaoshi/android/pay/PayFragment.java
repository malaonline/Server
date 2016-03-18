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

import com.android.volley.VolleyError;
import com.malalaoshi.android.MainActivity;
import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.OrderStatusModel;
import com.malalaoshi.android.event.BusEvent;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.util.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

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
            double value = Double.valueOf(resultEntity.getTo_pay()) * 0.01d;
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
            pay();
        }
    }

    private void pay() {
        if (resultEntity == null || TextUtils.isEmpty(resultEntity.getOrder_id())) {
            return;
        }
        PayManager.getInstance().getOrderInfo(resultEntity.getId(),
                currentPay.name(), new UIResultCallback<String>() {
                    @Override
                    protected void onResult(String data) {
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
     * "cancel"  - user cancel
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
                Log.e("MALA", "On activity result: " + result);

                if (result == null) {
                    showResultDialog(PayResultDialog.Type.PAY_FAILED);
                } else if (result.equals("success")) {
                    EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA));
                    getOrderStatusFromOurServer();
                } else if (result.equals("cancel")) {
                    showResultDialog(PayResultDialog.Type.CANCEL);
                } else if (result.equals("invalid")) {
                    showResultDialog(PayResultDialog.Type.INVALID);
                } else {
                    showResultDialog(PayResultDialog.Type.PAY_FAILED);
                }

            }
        }
    }

    private void showResultDialog(PayResultDialog.Type type) {
        PayResultDialog dialog = new PayResultDialog();
        dialog.setOnDismissListener(new PayResultDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                try {
                    PayFragment.this.getActivity().finish();
                } catch (Exception e) {
                }
            }
        });
        dialog.setType(type);
        if (isResumed()) {
            showDialog(dialog);
        } else {
            pendingDailog = dialog;
        }
    }

    private void showAllocateDialog() {
        PayTimeAllocateDialog dialog = new PayTimeAllocateDialog();
        dialog.setOnDismissListener(new PayTimeAllocateDialog.OnCloseListener() {
            @Override
            public void onLeftClick() {
                goToHome();
            }

            @Override
            public void onRightClick() {
                //
            }
        });
        if (isResumed()) {
            showDialog(dialog);
        } else {
            pendingDailog = dialog;
        }
    }

    private void goToHome() {
        Intent i = new Intent(getContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(i);
    }

    private void getOrderStatusFromOurServer() {
        if (resultEntity == null) {
            return;
        }
        String orderId = resultEntity.getOrder_id();
        NetworkSender.getOrderStatus(orderId, new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                try {
                    Log.i("MALA", json.toString());
                    OrderStatusModel model = JsonUtil.parseStringData(json.toString(), OrderStatusModel.class);
                    //支付失败
                    if (!model.getStatus().equals("p")) {
                        showResultDialog(PayResultDialog.Type.PAY_FAILED);
                        return;
                    }
                    if (model.is_timeslot_allocated()) {
                        showResultDialog(PayResultDialog.Type.PAY_SUCCESS);
                    } else {
                        //课程被占用
                        showAllocateDialog();
                    }
                } catch (Exception e) {
                    showResultDialog(PayResultDialog.Type.NETWORK_ERROR);
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                showResultDialog(PayResultDialog.Type.NETWORK_ERROR);
            }
        });
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
