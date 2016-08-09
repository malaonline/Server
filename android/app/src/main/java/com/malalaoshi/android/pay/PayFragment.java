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
import com.malalaoshi.android.course.CourseConfirmActivity;
import com.malalaoshi.android.dialogs.PromptDialog;
import com.malalaoshi.android.entity.ChargeOrder;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.OrderStatusModel;
import com.malalaoshi.android.pay.api.OrderStatusApi;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.JsonUtil;
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
    private boolean isEvaluated = true;

    private static final int STATUS_BEFORE_PAY_RES_NOALLOCATE  = 0; //付款前,检测上课时间被占用
    private static final int STATUS_AFTER_PAY_RES_NOPUBLIC     = 1; //付款后,订单状态:教师已经下架
    private static final int STATUS_AFTER_PAY_RES_ALLOCATED    = 2; //付款后,订单状态:购课成功
    private static final int STATUS_AFTER_PAY_RES_NOALLOCATE   = 3; //付款后,订单状态:课程被占用
    private static final int STATUS_AFTER_PAY_RES_TIMEOUT      = 4; //付款后,订单状态:支付超时
    private static final int STATUS_AFTER_PAY_RES_FAILED       = 5; //付款后,订单失败
    private static final int STATUS_AFTER_PAY_RES_NET_ERROR    = 6; //付款后,订单状态获取失败

    public static PayFragment newInstance(CreateCourseOrderResultEntity orderEntity, boolean isEvaluated) {
        PayFragment fragment = new PayFragment();
        fragment.setOrderEntity(orderEntity);
        fragment.setEvaluated(isEvaluated);
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

    public void setEvaluated(boolean evaluated) {
        isEvaluated = evaluated;
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
        if (charge==null){
            return;
        }
        ChargeOrder chargeOrder = JsonUtil.parseStringData(charge,ChargeOrder.class);
        if (chargeOrder!=null&&chargeOrder.isOk()&&-1==chargeOrder.getCode()){
            showPromptDialog(STATUS_BEFORE_PAY_RES_NOALLOCATE,R.drawable.ic_timeallocate,"部分课程时间已被占用，请重新选择上课时间!","确定");
            return;
        }
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
                Log.i("MALA", "On activity result: " + result);

                if (result == null) {
                    showPromptDialog(STATUS_AFTER_PAY_RES_FAILED,R.drawable.ic_pay_failed,"支付失败，请重试！","知道了");
                } else if (result.equals("success")) {
                    EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_PAY_SUCCESS));
                    getOrderStatusFromOurServer();
                } else if (result.equals("cancel")) {
                    showPromptDialog(STATUS_AFTER_PAY_RES_FAILED,R.drawable.ic_pay_failed,"支付用户已取消！","知道了");
                } else if (result.equals("invalid")) {
                    showPromptDialog(STATUS_AFTER_PAY_RES_FAILED,R.drawable.ic_pay_failed,"微信支付要先安装微信！","知道了");
                } else {
                    showPromptDialog(STATUS_AFTER_PAY_RES_FAILED,R.drawable.ic_pay_failed,"支付失败，请重试！","知道了");
                }

            }
        }
    }

    private void goToSchedule() {
        Intent i = new Intent(getContext(), MainActivity.class);
        i.putExtra(MainActivity.EXTRAS_PAGE_INDEX, MainActivity.PAGE_INDEX_COURSES);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(i);
    }

    private void goToTeacherList() {
        Intent i = new Intent(getContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(i);
    }

    private void goToConfirmCourse() {
        Intent i = new Intent(getContext(), CourseConfirmActivity.class);
        i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
    }

    private void getOrderStatusFromOurServer() {
        if (resultEntity == null) {
            return;
        }
        ApiExecutor.exec(new FetchOrderStatusRequest(this, resultEntity.getId()));
    }

    private void getOrderStatusSuccess(@NonNull OrderStatusModel status) {

        if (status.getIs_teacher_published()==null||!status.getIs_teacher_published()){
            showPromptDialog(STATUS_AFTER_PAY_RES_NOPUBLIC, R.drawable.ic_pay_failed,"购课失败,该老师已经下架,稍后会自动退款！","知道了");
            return;
        }
        if (status.getStatus().equals("p")){
            if (status.is_timeslot_allocated()) {
                String message = "";
                if (!isEvaluated){
                    message = "恭喜您已支付成功！销售顾问会稍后跟您电话确认课前测评时间！";
                }else{
                    message = "恭喜您支付成功！您的课表已经安排好，快去查看吧！";
                }
                showPromptDialog(STATUS_AFTER_PAY_RES_ALLOCATED,R.drawable.ic_pay_success,message,"知道了");
            } else {
                //课程被占用
                showDoubleButtonPromptDialog(STATUS_AFTER_PAY_RES_NOALLOCATE,R.drawable.ic_timeallocate,"课程被抢占，稍后会自动退款。请重新选择时间段！","返回首页", "查看其他时间");
            }
        }else if (status.getStatus().equals("d")){
            //付款超时,订单被系统取消,稍后自动退款
            showDoubleButtonPromptDialog(STATUS_AFTER_PAY_RES_TIMEOUT,R.drawable.ic_pay_failed,"付款超时,当前订单已被系统取消,稍后会自动退款！","返回首页", "查看其他时间");
        }else{
            //订单状态错误
            showDoubleButtonPromptDialog(STATUS_AFTER_PAY_RES_FAILED,R.drawable.ic_pay_failed,"购买失败,稍后会自动退款！","返回首页", "查看其他时间");
        }
    }

    //付款结果对话框
    private void showDoubleButtonPromptDialog(final int status, int resId, String message, String leftText, String rightText) {
        PromptDialog dialog = DialogUtil.createDoubleButtonPromptDialog( resId
                , message,leftText, rightText,
                new PromptDialog.OnCloseListener() {
                    @Override
                    public void onLeftClick() {
                        if (STATUS_AFTER_PAY_RES_NOALLOCATE==status
                                ||STATUS_AFTER_PAY_RES_TIMEOUT==status
                                ||STATUS_AFTER_PAY_RES_FAILED==status){
                            //返回首页
                            goToSchedule();
                        }
                    }

                    @Override
                    public void onRightClick() {
                        if (STATUS_AFTER_PAY_RES_NOALLOCATE==status
                                ||STATUS_AFTER_PAY_RES_TIMEOUT==status
                                ||STATUS_AFTER_PAY_RES_FAILED==status){
                            //跳转到购课页面,重新拉取教师上课时间
                            goToConfirmCourse();
                            EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_FETCHEVALUATED));
                        }
                    }
                },false,false);
        if (isResumed()) {
            showDialog(dialog);
        } else {
            pendingDialog = dialog;
        }
    }

    //付款结果对话框
    private void showPromptDialog(final int status, int resId, String message, String btnText) {
        PromptDialog dialog = DialogUtil.createPromptDialog(resId
                , message, btnText,
                new PromptDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if(STATUS_BEFORE_PAY_RES_NOALLOCATE==status){
                            //付款前,部分课程已经被占,跳转到课程购买页,刷新教师上课时间
                            goToConfirmCourse();
                            EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_FETCHEVALUATED));
                        }else if (STATUS_AFTER_PAY_RES_FAILED==status){
                            //支付失败,什么也不做,关闭提示对话框
                        }else if(STATUS_AFTER_PAY_RES_ALLOCATED==status){
                            //支付成功,跳转到课表页,更新课表
                            //需要刷新课表页
                            goToSchedule();
                            EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA));
                        }else if (STATUS_AFTER_PAY_RES_NOPUBLIC==status){
                            //教师已经下架,跳转到教师列表页,需要添加刷新教师列表通知
                            goToTeacherList();
                            EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_TEACHERLIST_DATA));
                        }else if(STATUS_AFTER_PAY_RES_NET_ERROR==status){
                            goToTeacherList();
                        }
                    }
                }
                , false, false);
        if (isResumed()) {
            showDialog(dialog);
        } else {
            pendingDialog = dialog;
        }
    }

    private void getOrderStatusFailed() {
        showPromptDialog(STATUS_AFTER_PAY_RES_NET_ERROR,R.drawable.ic_pay_failed,"订单状态获取失败,稍后请在订单列表中查看支付详情!","知道了");
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
