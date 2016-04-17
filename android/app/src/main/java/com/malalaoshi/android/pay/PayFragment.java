package com.malalaoshi.android.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.malalaoshi.android.MainActivity;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.OrderStatusModel;
import com.malalaoshi.android.pay.api.OrderStatusApi;
import com.malalaoshi.android.util.MiscUtil;

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

    private DialogFragment pendingDialog;

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
        if (pendingDialog != null) {
            showDialog(pendingDialog);
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
            StatReporter.pay();
            pay();
        }
    }



    private void pay() {
        if (resultEntity == null || TextUtils.isEmpty(resultEntity.getOrder_id())) {
            return;
        }
        ApiExecutor.exec(new FetchOrderInfoRequest(this, resultEntity.getId(), currentPay.name()));
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
            pendingDialog = dialog;
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
                try {
                    PayFragment.this.getActivity().finish();
                } catch (Exception e) {
                }
            }
        });
        if (isResumed()) {
            showDialog(dialog);
        } else {
            pendingDialog = dialog;
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
        ApiExecutor.exec(new FetchOrderStatusRequest(this, resultEntity.getId()));
    }

    private void getOrderStatusSuccess(@NonNull OrderStatusModel status) {
        if ("p".equals(status.getStatus())) {
            showResultDialog(PayResultDialog.Type.PAY_FAILED);
            return;
        }
        if (status.is_timeslot_allocated()) {
            showResultDialog(PayResultDialog.Type.PAY_SUCCESS);
        } else {
            //课程被占用
            showAllocateDialog();
        }
    }

    private void getOrderStatusFailed() {
        showResultDialog(PayResultDialog.Type.NETWORK_ERROR);
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
        pendingDialog = null;
    }

    private static final class FetchOrderInfoRequest extends BaseApiContext<PayFragment, String> {

        private String orderId;
        private String payment;

        public FetchOrderInfoRequest(PayFragment payFragment, String orderId, String payChannel) {
            super(payFragment);
            this.orderId = orderId;
            this.payment = payChannel;
        }

        @Override
        public String request() throws Exception {
            return PayManager.getInstance().getOrderInfo(orderId, payment);
        }

        @Override
        public void onApiSuccess(@NonNull String response) {
            get().payInternal(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("订单状态不正确");
        }
    }

    private static final class FetchOrderStatusRequest extends BaseApiContext<PayFragment, OrderStatusModel> {

        private String orderId;

        public FetchOrderStatusRequest(PayFragment payFragment, String orderId) {
            super(payFragment);
            this.orderId = orderId;
        }

        @Override
        public OrderStatusModel request() throws Exception {
            return new OrderStatusApi().getOrderStatus(orderId);
        }

        @Override
        public void onApiSuccess(@NonNull OrderStatusModel response) {
            get().getOrderStatusSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().getOrderStatusFailed();
        }
    }

}
