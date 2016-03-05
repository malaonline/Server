package com.malalaoshi.android.pay;

import android.content.Intent;
import android.os.Bundle;

import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.event.ChoiceCouponEvent;
import com.malalaoshi.android.util.EventDispatcher;
import com.malalaoshi.android.util.FragmentUtil;
import com.malalaoshi.android.view.TitleBarView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Coupon Activity.
 * Created by tianwei on 1/24/16.
 */
public class CouponActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    public static final String COUPON_ID = "coupon_id";

    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;
    //The coupon that is chose currently.
    private CouponEntity couponEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        ButterKnife.bind(this);
        FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(), null
                , CouponListFragment.newInstance(), "couponfragment");
        titleBarView.setTitle(R.string.scholarship);
        titleBarView.setOnTitleBarClickListener(this);
        EventDispatcher.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        EventDispatcher.getInstance().unregister(this);
        super.onDestroy();
    }

    public void onEvent(ChoiceCouponEvent event) {
        couponEntity = event.COUPON;
    }

    @Override
    public void onTitleLeftClick() {
        Intent intent = new Intent();
        if (couponEntity != null) {
            intent.putExtra(COUPON_ID, couponEntity.getAmount());
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onTitleRightClick() {

    }
}
