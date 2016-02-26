package com.malalaoshi.android.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.util.FragmentUtil;
import com.malalaoshi.android.view.TitleBarView;

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
        finish();
    }

    @Override
    public void onTitleRightClick() {
        //Hide
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        payFragment.onActivityResult(requestCode, resultCode, data);
    }
}
