package com.malalaoshi.android.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.dialogs.PromptDialog;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.pay.api.DeleteOrderApi;
import com.malalaoshi.android.result.OkResult;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.FragmentUtil;
import com.malalaoshi.android.util.MiscUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Pay activity
 * PayActivity不负责创建订单，创建订单的过程由业务自己调用PayManger.createOrder()完成了，业务自己创建好订单以后把订单ID传给PayActivity
 * Created by tianwei on 2/27/16.
 */
public class PayActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    private static final String EXTRA_ORDER_ID = "order_id";
    private static final String EXTRA_IS_EVALUATED = "is_evaluated";
    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;

    private CreateCourseOrderResultEntity orderEntity;
    private boolean isEvaluated;

    private PayFragment payFragment;

    /**
     *
     * @param entity
     * @param context
     * @param isEvaluated   true:已经完成测评   false:没有进行测评
     */
    public static void startPayActivity(CreateCourseOrderResultEntity entity, Activity context, boolean isEvaluated) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, entity);
        intent.putExtra(EXTRA_IS_EVALUATED,isEvaluated);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        titleBarView.setOnTitleBarClickListener(this);
        orderEntity = (CreateCourseOrderResultEntity) getIntent().getSerializableExtra(EXTRA_ORDER_ID);
        isEvaluated = getIntent().getBooleanExtra(EXTRA_IS_EVALUATED,true);
        if (orderEntity == null) {
            finish();
        }
        payFragment = PayFragment.newInstance(orderEntity,isEvaluated);
        FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(), null,
                payFragment, "payfragment");
    }

    @Override
    public void onTitleLeftClick() {
        checkCancelCourseOrder();
    }

    @Override
    public void onTitleRightClick() {
        //Hide
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        payFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        checkCancelCourseOrder();
    }

    private void checkCancelCourseOrder() {

        DialogUtil.showDoubleButtonPromptDialog(getSupportFragmentManager(), R.drawable.ic_cancel_order, "确认取消订单吗?", "确认", "取消", new PromptDialog.OnCloseListener() {
            @Override
            public void onLeftClick() {
                cancelOrder();
            }

            @Override
            public void onRightClick() {

            }
        }, false, true);
    }

    private void cancelOrder() {
        ApiExecutor.exec(new CancelCourseOrderRequest(PayActivity.this, orderEntity.getId()));
    }

    private static final class CancelCourseOrderRequest extends BaseApiContext<PayActivity, OkResult> {

        private String orderId;

        public CancelCourseOrderRequest(PayActivity payActivity, String orderId) {
            super(payActivity);
            this.orderId = orderId;
        }

        @Override
        public OkResult request() throws Exception {
            return new DeleteOrderApi().delete(orderId);
        }

        @Override
        public void onApiSuccess(@NonNull OkResult response) {
            if (response.isOk()) {
                MiscUtil.toast("订单已取消!");
            } else {
                MiscUtil.toast("订单状态取消失败!");
            }
        }

        @Override
        public void onApiFinished() {
            get().finish();
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("订单状态取消失败,请检查网络!");
        }
    }

    @Override
    protected String getStatName() {
        return "支付页面";
    }
}
