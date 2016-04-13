package com.malalaoshi.android.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.dialogs.PromptDialog;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.FragmentUtil;
import com.malalaoshi.android.util.MiscUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Pay activity
 * PayActivity不负责创建订单，创建订单的过程由业务自己调用PayManger.createOrder()完成了，业务自己创建好订单以后把订单ID传给PayActivity
 * Created by tianwei on 2/27/16.
 */
public class PayActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    private static final String EXTRA_ORDER_ID = "order_id";
    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;

    private CreateCourseOrderResultEntity orderEntity;

    private PayFragment payFragment;

    public static void startPayActivity(CreateCourseOrderResultEntity entity, Activity context) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, entity);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        titleBarView.setOnTitleBarClickListener(this);
        orderEntity = (CreateCourseOrderResultEntity) getIntent().getSerializableExtra(EXTRA_ORDER_ID);
        if (orderEntity == null) {
            finish();
        }
        payFragment = PayFragment.newInstance(orderEntity);
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
        //super.onBackPressed();
        checkCancelCourseOrder();
    }

    private void checkCancelCourseOrder() {

        DialogUtil.showDoubleButtonPromptDialog(getSupportFragmentManager(), R.drawable.ic_pay_success, "确认取消订单吗?", "确认", "取消", new PromptDialog.OnCloseListener() {
            @Override
            public void onLeftClick() {
                cancelCourseOrder();
            }

            @Override
            public void onRightClick() {

            }
        },false,true);
    }

    private void cancelCourseOrder() {
        NetworkSender.cancelCourseOrder(orderEntity.getId(), new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                finish();
                if (json==null){
                    MiscUtil.toast("订单状态取消失败!");
                    return;
                }
                dealCancelCourseRes(json.toString());
            }

            @Override
            public void onFailed(VolleyError error) {
                MiscUtil.toast("订单状态取消失败,请检查网络!");
                finish();
            }
        });
    }

    private void dealCancelCourseRes(String json) {
        JSONTokener jsonParser = new JSONTokener(json);
        boolean isOk = false;
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
            isOk = jsonObject.getBoolean("ok");
        } catch (JSONException e) {
            e.printStackTrace();

        } finally {
            if (isOk){
                MiscUtil.toast("订单已取消!");;
            }else{
                MiscUtil.toast("订单状态取消失败!");;
            }
        }
    }

    @Override
    protected String getStatName() {
        return "支付页面";
    }
}
