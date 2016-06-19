package com.malalaoshi.android.pay.coupon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.util.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Coupon Activity.
 * Created by tianwei on 1/24/16.
 */
public class CouponActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    private static final String EXTRA_CAN_SELECT = "extra_can_select";
    public static final String EXTRA_COUPON = "extra_coupon_selected";
    private static final String EXTRA_AMOUNT = "extra_order_amount";

    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;

    private CouponListFragment fragment;

    public static void launch(Context context, boolean canSelect) {
        Intent intent = new Intent(context, CouponActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_CAN_SELECT, canSelect);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void launch(Activity activity, int requestCode, CouponEntity coupon, long amount) {
        Intent intent = new Intent(activity, CouponActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_CAN_SELECT, true);
        bundle.putParcelable(EXTRA_COUPON, coupon);
        bundle.putLong(EXTRA_AMOUNT, amount);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        ButterKnife.bind(this);
        titleBarView.setTitle(R.string.scholarship);
        titleBarView.setOnTitleBarClickListener(this);
        initFragment();
        if (!UserManager.getInstance().isLogin()) {
            finish();
            UserManager.getInstance().startLoginActivity();
        }
    }

    private void initFragment() {
        fragment = (CouponListFragment) Fragment.instantiate(this, CouponListFragment.class.getName(), getIntent().getExtras());
        FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(),
                null, fragment, "couponfragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTitleLeftClick() {
        fragment.onBackClicked();
    }

    @Override
    public void onBackPressed() {
        fragment.onBackClicked();
    }

    @Override
    public void onTitleRightClick() {
        //奖学金使用规则
        CouponProtocolDialog couponProtocolDialog = CouponProtocolDialog.newInstance();
        couponProtocolDialog.show(getSupportFragmentManager(), CouponProtocolDialog.class.getName());
    }

    @Override

    protected String getStatName() {
        return "优惠劵";
    }
}
